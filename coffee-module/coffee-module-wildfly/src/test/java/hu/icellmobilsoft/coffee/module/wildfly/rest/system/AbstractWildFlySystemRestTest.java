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
package hu.icellmobilsoft.coffee.module.wildfly.rest.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.junit.jupiter.api.Test;


/**
 * Tests the legacy manifest response and registration of the deprecated WildFly REST endpoint.
 *
 * @author balazs.bakos
 * @since 4.0.0
 */
class AbstractWildFlySystemRestTest {

    @Test
    void preservesLegacyManifestResponse() throws Exception {
        String manifestText = "Manifest-Version: 1.0\r\nBuild-Description: \u00e1rv\u00edzt\u0171r\u0151\r\n second\r\n\r\n"
                + "Name: sample.txt\r\nSHA-256-Digest: example-digest";
        StringBuilder legacyResponse = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(manifestText.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                legacyResponse.append(line).append("\n");
            }
        }

        ServletContext context = mock(ServletContext.class);
        when(context.getResourceAsStream("META-INF/MANIFEST.MF"))
                .thenReturn(new ByteArrayInputStream(manifestText.getBytes(StandardCharsets.UTF_8)));
        TestSystemRest rest = new TestSystemRest();
        Field field = AbstractWildFlySystemRest.class.getDeclaredField("servletContext");
        field.setAccessible(true);
        field.set(rest, context);
        assertEquals(legacyResponse.toString(), rest.versionInfo());
    }

    @Test
    void stillRegistersDeprecatedVersionInfo() throws Exception {
        ResteasyProviderFactory.setInstance(new ResteasyProviderFactoryImpl());
        try {
            ResourceMethod[] methods = ResourceBuilder.rootResourceFromAnnotations(TestSystemRest.class).getResourceMethods();
            assertTrue(Arrays.stream(methods).anyMatch(method -> "versionInfo".equals(method.getMethod().getName())
                    && method.getHttpMethods().contains("GET") && "/versionInfo".equals(method.getPath())));
            assertTrue(Arrays.stream(methods).anyMatch(method -> "getEvict".equals(method.getMethod().getName())));
            assertTrue(AbstractWildFlySystemRest.class.getMethod("versionInfo").isAnnotationPresent(Deprecated.class));
        } finally {
            ResteasyProviderFactory.setInstance(null);
        }
    }

    /**
     * Concrete REST resource used to verify the inherited endpoint annotations.
     */
    @Path("")
    public static class TestSystemRest extends AbstractWildFlySystemRest {
    }
}
