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

    /**
     * Default constructor, constructs a new object.
     */
    public BaseRestLogger() {
        super();
    }

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
     * @return HTTP request message or null if logging is disabled
     */
    protected String processRequest(ContainerRequestContext requestContext) {
        if (RestLoggerUtil.logDisabled(requestContext, LogSpecifierTarget.REQUEST)) {
            return null;
        }

        StringBuffer message = new StringBuffer();
        printRequestLine(message, requestContext);
        printRequestHeaders(message, requestContext);
        printRequestEntity(message, requestContext);

        String messageString = message.toString();
        log.info(message.toString());
        return messageString;
    }

    /**
     * Processes HTTP response.
     * 
     * @param context
     *            context
     * @return HTTP response message or null if logging is disabled
     * @throws IOException
     *             if response cannot be processed.
     */
    protected String processResponse(WriterInterceptorContext context) throws IOException {
        if (RestLoggerUtil.logDisabled(context, LogSpecifierTarget.RESPONSE)) {
            context.proceed();
            return null;
        }

        StringBuffer message = new StringBuffer();
        try {

            printResponseLine(message, context);
            printResponseHeaders(message, context);

            OutputStream originalStream = context.getOutputStream();
            byte[] entityCopy = new byte[0];
            int maxResponseEntityLogSize = RestLoggerUtil.getMaxEntityLogSize(context, LogSpecifierTarget.RESPONSE);
            if (maxResponseEntityLogSize != LogSpecifier.NO_LOG) {
                hu.icellmobilsoft.coffee.tool.utils.stream.OutputStreamCopier osc = new hu.icellmobilsoft.coffee.tool.utils.stream.OutputStreamCopier(
                        originalStream);
                context.setOutputStream(osc);
                // Let's consume the stream while copying its content
                try {
                    context.proceed();
                } finally {
                    // IS: Question whether it's worth writing back the original stream...
                    context.setOutputStream(originalStream);
                }
                entityCopy = osc.getCopy();
            } else {
                context.proceed();
            }

            printResponseEntity(message, context, entityCopy);
        } finally {
            log.info(message.toString());
        }
        return message.toString();
    }

    /**
     * The name of the session key appearing in the HTTP headers. 
     * The logger will search for this key in the HTTP headers and use its value in the <code>MDC.put(LogConstants.LOG_SESSION_ID, value)</code> section.<br>
     * <br>
     * Process identification is highly meaningful in Graylog logging.
     * 
     * @return session key
     */
    public abstract String sessionKey();

    /**
     * Prints request headers from {@link ContainerRequestContext} and appends given {@link StringBuffer} with the print result.
     *
     * @param b
     *            request message
     * @param requestContext
     *            context
     * @see RequestResponseLogger#printRequestHeaders(java.util.Map)
     */
    protected void printRequestHeaders(StringBuffer b, ContainerRequestContext requestContext) {
        b.append(requestResponseLogger.printRequestHeaders(requestContext.getHeaders()));
        String sessionId = null;
        if (requestContext.getHeaders().containsKey(sessionKey())) {
            sessionId = requestContext.getHeaders().get(sessionKey()).get(0);
        }
        MDC.put(LogConstants.LOG_SESSION_ID, StringUtils.defaultIfBlank(sessionId, RandomUtil.generateId()));
    }

    /**
     * Prints http path info from {@link ContainerRequestContext} and appends given {@link StringBuffer} with the print result.
     *
     * @param b
     *            request message
     * @param requestContext
     *            context
     * @see RequestResponseLogger#printRequestLine(ContainerRequestContext)
     */
    protected void printRequestLine(StringBuffer b, ContainerRequestContext requestContext) {
        b.append(requestResponseLogger.printRequestLine(requestContext));
    }

    /**
     * Prints http entity from {@link ContainerRequestContext} and appends given {@link StringBuffer} with the print result.
     * 
     * @param b
     *            request message
     * @param requestContext
     *            context
     * @see RequestResponseLogger#printRequestEntity(ContainerRequestContext)
     */
    protected void printRequestEntity(StringBuffer b, ContainerRequestContext requestContext) {
        b.append(requestResponseLogger.printRequestEntity(requestContext));
    }

    /**
     * Prints response URL line and appends given {@link StringBuffer} with the print result.
     * 
     * @param b
     *            response message
     * @param context
     *            context
     * @see RequestResponseLogger#printResponseLine(String, int, String, String)
     */
    protected void printResponseLine(StringBuffer b, WriterInterceptorContext context) {
        String fullPath = uriInfo.getAbsolutePath().toASCIIString();
        int status = httpServletResponse.getStatus();
        Status statusEnum = Status.fromStatusCode(status);
        String statusInfo = statusEnum != null ? statusEnum.getReasonPhrase() : null;
        MediaType mediaType = context.getMediaType();
        b.append(requestResponseLogger.printResponseLine(fullPath, status, String.valueOf(statusInfo), String.valueOf(mediaType)));
    }

    /**
     * Prints response header values and appends given {@link StringBuffer} with the print result.
     * 
     * @param b
     *            response message
     * @param context
     *            context
     * @see RequestResponseLogger#printResponseHeaders(java.util.Map)
     */
    protected void printResponseHeaders(StringBuffer b, WriterInterceptorContext context) {
        b.append(requestResponseLogger.printResponseHeaders(context.getHeaders()));
    }

    /**
     * Prints response from {@link WriterInterceptorContext} and appends given {@link StringBuffer} with the print result.
     * 
     * @param b
     *            response message
     * @param context
     *            context
     * @param entityCopy
     *            entity
     * @see RequestResponseLogger#printResponseEntity(String, WriterInterceptorContext, byte[])
     */
    protected void printResponseEntity(StringBuffer b, WriterInterceptorContext context, byte[] entityCopy) {
        b.append(requestResponseLogger.printResponseEntity(uriInfo.getAbsolutePath().toASCIIString(), context, entityCopy));
    }
}
