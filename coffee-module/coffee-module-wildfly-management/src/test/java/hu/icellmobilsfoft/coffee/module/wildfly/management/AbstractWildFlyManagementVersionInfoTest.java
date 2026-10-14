/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package hu.icellmobilsfoft.coffee.module.wildfly.management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Path;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.as.server.mgmt.domain.ExtensibleHttpManagement;
import org.jboss.msc.service.ServiceContainer;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.contexts.ContextNotActiveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import hu.icellmobilsoft.coffee.rest.system.AbstractSystemRest;
import hu.icellmobilsfoft.coffee.module.wildfly.rest.system.AbstractWildFlySystemRest;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.util.StatusCodes;

/**
 * Tests management route registration, manifest responses and cleanup during application shutdown.
 *
 * @author balazs.bakos
 * @since 3.0.0
 */
class AbstractWildFlyManagementVersionInfoTest {

    private static ServletContext lifecycleServletContext;

    private final ServletContext servletContext = mock(ServletContext.class);
    private final ExtensibleHttpManagement management = mock(ExtensibleHttpManagement.class);
    private final AbstractWildFlyManagementVersionInfo endpoint = new AbstractWildFlyManagementVersionInfo() {
    };
    private ServiceContainer previousServiceContainer;

    @BeforeEach
    void installManagementService() throws Exception {
        previousServiceContainer = CurrentServiceContainer.getServiceContainer();
        ServiceContainer container = mock(ServiceContainer.class);
        ServiceController<?> controller = mock(ServiceController.class);
        doReturn(controller).when(container).getRequiredService(ServiceName.parse("org.wildfly.management.http.extensible"));
        doReturn(management).when(controller).getValue();
        setServiceContainer(container);
    }

    @AfterEach
    void restoreServiceContainer() throws Exception {
        setServiceContainer(previousServiceContainer);
    }

    private void setServiceContainer(ServiceContainer container) throws Exception {
        Method setter = CurrentServiceContainer.class.getDeclaredMethod("setServiceContainer", ServiceContainer.class);
        setter.setAccessible(true);
        setter.invoke(null, container);
    }

    private HttpHandler register() throws Exception {
        endpoint.registerManagementRoutes(servletContext);
        ArgumentCaptor<HttpHandler> handler = ArgumentCaptor.forClass(HttpHandler.class);
        verify(management).addManagementHandler(eq("/version"), eq(false), handler.capture());
        return handler.getValue();
    }

    @Test
    void servesWarManifestAndClosesStreamBeforeRequests() throws Exception {
        AtomicReference<Boolean> closed = new AtomicReference<>(false);
        String manifestText = "Manifest-Version: 1.0\r\nImplementation-Version: 1.2.3\r\nImplementation-Title: deployed-war\r\n"
                + "Class-Path: dependency.jar\r\nBuild-Description: first\r\n second\r\n\r\n"
                + "Name: sample.txt\r\nSHA-256-Digest: example-digest\r\n\r\n";
        when(servletContext.getResourceAsStream(AbstractSystemRest.META_INF_MANIFEST_MF)).thenAnswer(invocation -> new ByteArrayInputStream(
                manifestText.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        });
        HttpHandler handler = register();
        assertTrue(closed.get());
        HttpResponse<String> response = request(handler, "/version", "GET");
        assertEquals(StatusCodes.OK, response.statusCode());
        assertEquals("text/plain; charset=UTF-8", response.headers().firstValue("Content-Type").orElseThrow());
        assertEquals(manifestText.replace("\r\n", "\n"), response.body());
        TestSystemRest legacy = new TestSystemRest();
        Field contextField = AbstractWildFlySystemRest.class.getDeclaredField("servletContext");
        contextField.setAccessible(true);
        contextField.set(legacy, servletContext);
        assertEquals(response.body(), legacy.versionInfo());
        verify(servletContext, times(2)).getResourceAsStream(AbstractSystemRest.META_INF_MANIFEST_MF);
    }

    @Test
    void normalizesLineEndingsAndAddsLfAfterUnterminatedFinalLine() throws Exception {
        String manifestText = "Manifest-Version: 1.0\r\nCustom-Field: first\r continuation\n\nName: sample.txt\r\nCustom-Value: last";
        when(servletContext.getResourceAsStream(AbstractSystemRest.META_INF_MANIFEST_MF))
                .thenReturn(new ByteArrayInputStream(manifestText.getBytes(StandardCharsets.UTF_8)));
        assertEquals("Manifest-Version: 1.0\nCustom-Field: first\n continuation\n\nName: sample.txt\nCustom-Value: last\n",
                request(register(), "/version", "GET").body());
    }

    @Test
    void missingManifestReturnsEmptyResponse() throws Exception {
        HttpResponse<String> response = request(register(), "/version", "GET");
        assertEquals(StatusCodes.OK, response.statusCode());
        assertEquals("", response.body());
    }

    @Test
    void readFailureReturnsServerErrorAndClosesStream() throws Exception {
        InputStream manifest = mock(InputStream.class);
        when(manifest.read(any(byte[].class), anyInt(), anyInt())).thenThrow(new IOException("unreadable"));
        when(servletContext.getResourceAsStream(AbstractSystemRest.META_INF_MANIFEST_MF)).thenReturn(manifest);
        HttpResponse<String> response = request(register(), "/version", "GET");
        assertEquals(StatusCodes.INTERNAL_SERVER_ERROR, response.statusCode());
        assertEquals("Error reading manifest: unreadable\n", response.body());
        verify(manifest).close();
    }

    @Test
    void rejectsOtherMethods() throws Exception {
        HttpResponse<String> response = request(register(), "/version", "POST");
        assertEquals(StatusCodes.METHOD_NOT_ALLOWED, response.statusCode());
        assertEquals("GET", response.headers().firstValue("Allow").orElseThrow());
    }

    @Test
    void rejectsNestedPaths() throws Exception {
        assertEquals(StatusCodes.NOT_FOUND, request(register(), "/version/other", "GET").statusCode());
    }

    @Test
    void removesContextOnShutdownOnlyOnce() throws Exception {
        register();
        endpoint.unregisterManagementRoutes(new Object());
        endpoint.unregisterManagementRoutes(new Object());
        verify(management).removeContext("/version");
    }

    @Test
    void failedRegistrationDoesNotRemoveAnotherDeploymentsContext() throws Exception {
        doThrow(new IllegalStateException("context already exists")).when(management).addManagementHandler(eq("/version"), eq(false), any());
        assertThrows(IllegalStateException.class, this::register);
        endpoint.unregisterManagementRoutes(new Object());
        verify(management, never()).removeContext(any());
    }

    @Test
    void inheritedCdiObserversRegisterWithoutActiveRequestContextAndRemoveContext() {
        lifecycleServletContext = mock(ServletContext.class);
        try (WeldContainer container = new Weld().disableDiscovery().addBeanClasses(LifecycleEndpoint.class, ContextProducer.class).initialize()) {
            verify(management, never()).addManagementHandler(any(), eq(false), any());
            ServletContext requestScopedProxy = container.select(ServletContext.class).get();
            assertThrows(ContextNotActiveException.class,
                    () -> requestScopedProxy.getResourceAsStream(AbstractSystemRest.META_INF_MANIFEST_MF));
            container.getBeanManager().getEvent()
                    .select(ServletContext.class, Initialized.Literal.of(ApplicationScoped.class))
                    .fire(lifecycleServletContext);
            verify(management).addManagementHandler(eq("/version"), eq(false), any());
            verify(lifecycleServletContext).getResourceAsStream(AbstractSystemRest.META_INF_MANIFEST_MF);
        }
        verify(management).removeContext("/version");
    }

    /**
     * Concrete application bean inheriting the management lifecycle observers.
     */
    @ApplicationScoped
    public static class LifecycleEndpoint extends AbstractWildFlyManagementVersionInfo {
    }

    /**
     * Simulates the request-scoped servlet context supplied by a web CDI container.
     */
    @Dependent
    public static class ContextProducer {
        @Produces
        @RequestScoped
        public ServletContext servletContext() {
            return lifecycleServletContext;
        }
    }

    /**
     * Concrete legacy REST resource used to compare the two endpoint responses.
     */
    @Path("")
    public static class TestSystemRest extends AbstractWildFlySystemRest {
    }

    private HttpResponse<String> request(HttpHandler handler, String path, String method) throws Exception {
        Undertow server = Undertow.builder().setIoThreads(1).setWorkerThreads(2).addHttpListener(0, "127.0.0.1").setHandler(handler).build();
        server.start();
        try {
            HttpClient client = HttpClient.newHttpClient();
            int port = ((java.net.InetSocketAddress) server.getListenerInfo().get(0).getAddress()).getPort();
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + path))
                    .method(method, HttpRequest.BodyPublishers.noBody())
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } finally {
            server.stop();
        }
    }
}

