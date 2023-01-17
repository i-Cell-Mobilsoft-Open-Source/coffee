/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.rest.cdi.BaseApplicationContainer;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;
import hu.icellmobilsoft.coffee.rest.utils.RestLoggerUtil;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.utils.stream.RequestLoggerInputStream;
import hu.icellmobilsoft.coffee.tool.utils.stream.ResponseEntityCollectorOutputStream;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * Base class for REST logging
 *
 * @author ischeffer
 * @since 1.0.0
 */
public abstract class BaseRestLogger implements ContainerRequestFilter, WriterInterceptor {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private BaseApplicationContainer baseApplicationContainer;

    @Inject
    private RequestResponseLogger requestResponseLogger;

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletResponse httpServletResponse;

    /** {@inheritDoc} */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MDC.clear();
        MDC.put(LogConstants.LOG_SERVICE_NAME, baseApplicationContainer.getCoffeeAppName());
        processRequest(requestContext);
    }

    /** {@inheritDoc} */
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException {
        processResponse(context);
    }

    /**
     * Processes HTTP request.
     *
     * @param requestContext
     *            context
     */
    protected void processRequest(ContainerRequestContext requestContext) {
        if (RestLoggerUtil.logDisabled(requestContext, LogSpecifierTarget.REQUEST)) {
            return;
        }

        StringBuilder message = new StringBuilder();
        appendRequestLine(message, requestContext);
        appendRequestHeaders(message, requestContext);

        int maxRequestEntityLogSize = requestResponseLogger.getMaxRequestEntityLogSize(requestContext);

        RequestLoggerInputStream requestLoggerInputStream = new RequestLoggerInputStream(requestContext.getEntityStream(), maxRequestEntityLogSize,
                RequestResponseLogger.REQUEST_PREFIX, message);

        // a saját InputStream-et állítjuk be a context-be, ami majd az entity stream olvasáskor log-olja a request-et
        requestContext.setEntityStream(requestLoggerInputStream);
    }

    /**
     * Processes HTTP response.
     *
     * @param context
     *            context
     * @throws IOException
     *             if response cannot be processed.
     */
    protected void processResponse(WriterInterceptorContext context) throws IOException {
        if (RestLoggerUtil.logDisabled(context, LogSpecifierTarget.RESPONSE)) {
            context.proceed();
            return;
        }

        StringBuilder message = new StringBuilder();
        try {
            printResponseLine(message, context);
            printResponseHeaders(message, context);

            OutputStream originalStream = context.getOutputStream();
            byte[] entity = new byte[0];
            int maxResponseEntityLogSize = RestLoggerUtil.getMaxEntityLogSize(context, LogSpecifierTarget.RESPONSE);
            if (maxResponseEntityLogSize != LogSpecifier.NO_LOG) {
                ResponseEntityCollectorOutputStream responseEntityCollectorOutputStream = new ResponseEntityCollectorOutputStream(originalStream);
                // a saját OutputStream-et állítjuk be a context-be, ami majd az entity stream-be írásakor gyűjti azt a log-olás számára
                context.setOutputStream(responseEntityCollectorOutputStream);
                context.proceed();
                entity = responseEntityCollectorOutputStream.getEntityText().getBytes(StandardCharsets.UTF_8);
            } else {
                context.proceed();
            }

            printResponseEntity(message, context, entity);
        } finally {
            log.info(message.toString());
        }
    }

    /**
     * HTTP headerben szereplo session kulcs neve. Ezt a kulcsot fogja a logger keresni a http headerekből, aminek az értékét fel használja a
     * <code>MDC.put(LogConstants.LOG_SESSION_ID, ertek)</code> részben.<br>
     * <br>
     * Folyamat azonosítás, Graylog loggolásban van nagy értelme
     * 
     * @return session key
     */
    public abstract String sessionKey();

    /**
     * Prints request headers from {@link ContainerRequestContext} and appends given {@link StringBuilder} with the print result.
     *
     * @param b
     *            request message
     * @param requestContext
     *            context
     * @see RequestResponseLogger#printRequestHeaders(java.util.Map)
     */
    protected void appendRequestHeaders(StringBuilder b, ContainerRequestContext requestContext) {
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        b.append(requestResponseLogger.printRequestHeaders(headers));
        String sessionId = null;
        if (headers != null && headers.containsKey(sessionKey())) {
            sessionId = headers.get(sessionKey()).get(0);
        }
        MDC.put(LogConstants.LOG_SESSION_ID, StringUtils.defaultIfBlank(sessionId, RandomUtil.generateId()));
    }

    /**
     * Prints http path info from {@link ContainerRequestContext} and appends given {@link StringBuilder} with the print result.
     *
     * @param b
     *            request message
     * @param requestContext
     *            context
     * @see RequestResponseLogger#printRequestLine(ContainerRequestContext)
     */
    protected void appendRequestLine(StringBuilder b, ContainerRequestContext requestContext) {
        b.append(requestResponseLogger.printRequestLine(requestContext));
    }

    /**
     * Prints response URL line and appends given {@link StringBuilder} with the print result.
     * 
     * @param b
     *            response message
     * @param context
     *            context
     * @see RequestResponseLogger#printResponseLine(String, int, String, String)
     */
    protected void printResponseLine(StringBuilder b, WriterInterceptorContext context) {
        String fullPath = uriInfo.getAbsolutePath().toASCIIString();
        int status = httpServletResponse.getStatus();
        Status statusEnum = Status.fromStatusCode(status);
        String statusInfo = statusEnum != null ? statusEnum.getReasonPhrase() : null;
        MediaType mediaType = context.getMediaType();
        b.append(requestResponseLogger.printResponseLine(fullPath, status, String.valueOf(statusInfo), String.valueOf(mediaType)));
    }

    /**
     * Prints response header values and appends given {@link StringBuilder} with the print result.
     * 
     * @param b
     *            response message
     * @param context
     *            context
     * @see RequestResponseLogger#printResponseHeaders(java.util.Map)
     */
    protected void printResponseHeaders(StringBuilder b, WriterInterceptorContext context) {
        b.append(requestResponseLogger.printResponseHeaders(context.getHeaders()));
    }

    /**
     * Prints response from {@link WriterInterceptorContext} and appends given {@link StringBuilder} with the print result.
     * 
     * @param b
     *            response message
     * @param context
     *            context
     * @param entityCopy
     *            entity
     * @see RequestResponseLogger#printResponseEntity(String, WriterInterceptorContext, byte[])
     */
    protected void printResponseEntity(StringBuilder b, WriterInterceptorContext context, byte[] entityCopy) {
        b.append(requestResponseLogger.printResponseEntity(uriInfo.getAbsolutePath().toASCIIString(), context, entityCopy));
    }
}
