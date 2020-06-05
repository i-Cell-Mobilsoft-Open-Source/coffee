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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.MDC;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;
import hu.icellmobilsoft.coffee.rest.utils.RestLoggerUtil;
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

    @Resource(lookup = "java:app/AppName")
    private String applicationName;

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
        MDC.put(LogConstants.LOG_SERVICE_NAME, applicationName);
        processRequest(requestContext);
    }

    /** {@inheritDoc} */
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException {
        processResponse(context);
    }

    /**
     * <p>processRequest.</p>
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
     * <p>processResponse.</p>
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
                hu.icellmobilsoft.coffee.tool.utils.stream.OutputStreamCopier osc = new hu.icellmobilsoft.coffee.tool.utils.stream.OutputStreamCopier(originalStream);
                context.setOutputStream(osc);
                // elegessuk a stream-et, kozben masoljuk a tartalmat
                try {
                    context.proceed();
                } finally {
                    // IS: kerdeses erdemes-e vissza irni az eredeti stream-et...
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
     * HTTP headerben szereplo session kulcs neve. Ezt a kulcsot fogja a logger keresni a http headerekből, aminek az értékét fel használja a
     * <code>MDC.put(LogConstants.LOG_SESSION_ID, ertek)</code> részben.<br>
     * <br>
     * Folyamat azonosítás, Graylog loggolásban van nagy értelme
     */
    public abstract String sessionKey();

    /**
     * <p>printRequestHeaders.</p>
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
     * <p>printRequestLine.</p>
     */
    protected void printRequestLine(StringBuffer b, ContainerRequestContext requestContext) {
        b.append(requestResponseLogger.printRequestLine(requestContext));
    }

    /**
     * <p>printRequestEntity.</p>
     */
    protected void printRequestEntity(StringBuffer b, ContainerRequestContext requestContext) {
        b.append(requestResponseLogger.printRequestEntity(requestContext));
    }

    /**
     * <p>printResponseLine.</p>
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
     * <p>printResponseHeaders.</p>
     */
    protected void printResponseHeaders(StringBuffer b, WriterInterceptorContext context) {
        b.append(requestResponseLogger.printResponseHeaders(context.getHeaders()));
    }

    /**
     * <p>printResponseEntity.</p>
     */
    protected void printResponseEntity(StringBuffer b, WriterInterceptorContext context, byte[] entityCopy) {
        b.append(requestResponseLogger.printResponseEntity(uriInfo.getAbsolutePath().toASCIIString(), context, entityCopy));
    }
}
