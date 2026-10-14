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

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.servlet.ServletContext;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.as.server.mgmt.domain.ExtensibleHttpManagement;
import org.jboss.msc.service.ServiceName;

import hu.icellmobilsfoft.coffee.module.wildfly.util.VersionInfoUtil;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.StatusCodes;

/**
 * Registers the WAR's manifest on the WildFly HTTP management interface, at {@code /version} by default.
 * Applications enable this endpoint by extending this class with an {@link ApplicationScoped} CDI bean.
 * Applications can override {@link #getPath()} to use distinct paths for multiple deployments on the same server.
 *
 * @author balazs.bakos
 * @since 3.0.0
 */
public abstract class AbstractWildFlyManagementVersionInfo {

    private static final String VERSION_PATH = "/version";
    private static final ServiceName HTTP_MANAGEMENT_SERVICE = ServiceName.parse("org.wildfly.management.http.extensible");

    private ExtensibleHttpManagement registeredManagement;
    private String registeredPath;

    /**
     * Default constructor.
     */
    public AbstractWildFlyManagementVersionInfo() {
        // Default constructor for java 21
    }

    /**
     * Returns the management endpoint path. Override in the application bean to avoid conflicts with other deployments.
     *
     * @return absolute context path starting with {@code /}, without a trailing slash; defaults to {@code /version}
     */
    protected String getPath() {
        return VERSION_PATH;
    }

    /**
     * Registers the management handler using the servlet context supplied by the web application lifecycle event.
     * This avoids resolving a request-scoped ServletContext proxy during deployment.
     *
     * @param servletContext
     *            servlet context supplied as the application initialization event payload
     */
    public void registerManagementRoutes(@Observes @Initialized(ApplicationScoped.class) ServletContext servletContext) {
        String content;
        int status;
        try {
            content = VersionInfoUtil.getVersionInfo(servletContext);
            status = StatusCodes.OK;
        } catch (IOException e) {
            content = "Error reading manifest: " + e.getMessage() + "\n";
            status = StatusCodes.INTERNAL_SERVER_ERROR;
        }

        String response = content;
        int responseStatus = status;
        String path = getPath();
        ExtensibleHttpManagement management = getHttpManagement();
        management.addManagementHandler(path, false, exchange -> handleRequest(exchange, path, response, responseStatus));
        registeredManagement = management;
        registeredPath = path;
    }

    /**
     * Removes the handler before the application is stopped or redeployed.
     *
     * @param destroy
     *            application shutdown event
     */
    public void unregisterManagementRoutes(@Observes @BeforeDestroyed(ApplicationScoped.class) Object destroy) {
        if (registeredManagement != null) {
            registeredManagement.removeContext(registeredPath);
            registeredManagement = null;
            registeredPath = null;
        }
    }

    /**
     * Resolves WildFly's extensible HTTP management service.
     *
     * @return the running management service
     * @throws IllegalStateException
     *             if the server's HTTP management service is unavailable
     */
    private ExtensibleHttpManagement getHttpManagement() {
        return (ExtensibleHttpManagement) CurrentServiceContainer.getServiceContainer().getRequiredService(HTTP_MANAGEMENT_SERVICE).getValue();
    }

    private void handleRequest(HttpServerExchange exchange, String path, String content, int status) {
        if (!path.equals(exchange.getRequestPath())) {
            exchange.setStatusCode(StatusCodes.NOT_FOUND);
            exchange.endExchange();
        } else if (!Methods.GET.equals(exchange.getRequestMethod())) {
            exchange.setStatusCode(StatusCodes.METHOD_NOT_ALLOWED);
            exchange.getResponseHeaders().put(Headers.ALLOW, Methods.GET_STRING);
            exchange.endExchange();
        } else {
            exchange.setStatusCode(status);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain; charset=UTF-8");
            exchange.getResponseSender().send(content);
        }
    }
}

