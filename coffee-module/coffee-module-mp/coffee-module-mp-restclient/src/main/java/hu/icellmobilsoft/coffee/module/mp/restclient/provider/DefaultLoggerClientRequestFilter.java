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
package hu.icellmobilsoft.coffee.module.mp.restclient.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.module.mp.restclient.RestClientPriority;
import hu.icellmobilsoft.coffee.rest.log.RequestResponseLogger;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;
import hu.icellmobilsoft.coffee.rest.utils.RestLoggerUtil;
import hu.icellmobilsoft.coffee.tool.utils.stream.OutputStreamCopier;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;

/**
 * Rest Client default request logger filter
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Priority(value = RestClientPriority.REQUEST_LOG)
@Dependent
public class DefaultLoggerClientRequestFilter implements ClientRequestFilter, WriterInterceptor {

    @Inject
    private RequestResponseLogger requestResponseLogger;

    /** {@inheritDoc} */
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (RestLoggerUtil.logDisabled(requestContext, LogSpecifierTarget.CLIENT_REQUEST)) {
            return;
        }
        StringBuilder msg = new StringBuilder();
        msg.append(">> ").append(getClass().getName()).append(" request ->\n");
        msg.append(logUrl(requestContext));
        msg.append(logHeader(requestContext));
        LogProducer.logToAppLogger((AppLogger appLogger) -> appLogger.info(msg.toString()), DefaultLoggerClientRequestFilter.class);
    }

    /** {@inheritDoc} */
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        int maxResponseEntityLogSize = RestLoggerUtil.getMaxEntityLogSize(context, LogSpecifierTarget.CLIENT_REQUEST);
        if (maxResponseEntityLogSize == LogSpecifier.NO_LOG) {
            context.proceed();
            return;
        }

        OutputStream originalStream = context.getOutputStream();
        byte[] entityCopy = new byte[0];
        OutputStreamCopier osc = new OutputStreamCopier(originalStream);
        context.setOutputStream(osc);
        // elegessuk a stream-et, kozben masoljuk a tartalmat
        try {
            context.proceed();
        } finally {
            // IS: kerdeses erdemes-e vissza irni az eredeti stream-et...
            context.setOutputStream(originalStream);
        }
        entityCopy = osc.getCopy();
        MediaType mediaType = context.getMediaType();
        String requestText = new String(entityCopy, StandardCharsets.UTF_8);
        String requestEntity = requestResponseLogger
                .printEntity(requestText, maxResponseEntityLogSize, RequestResponseLogger.REQUEST_PREFIX, true, mediaType);
        LogProducer.logToAppLogger((AppLogger appLogger) -> appLogger.info(requestEntity.toString()), DefaultLoggerClientRequestFilter.class);
    }

    /**
     * Logs HTTP method and URL.
     *
     * @param requestContext
     *            context
     * @return HTTP method and URL {@link String}
     * @throws IOException
     *             exception
     */
    protected String logUrl(ClientRequestContext requestContext) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append("> url: [").append(requestContext.getMethod()).append(" ").append(requestContext.getUri()).append("]\n");
        return msg.toString();
    }

    /**
     * Logs HTTP header data.
     *
     * @param requestContext
     *            context
     * @return header {@link String}
     * @throws IOException
     *             exception
     */
    protected String logHeader(ClientRequestContext requestContext) throws IOException {
        StringBuilder msg = new StringBuilder();
        msg.append("> headers: [");
        for (Entry<String, List<Object>> header : requestContext.getHeaders().entrySet()) {
            msg.append("\n>    ").append(header.getKey()).append(":").append(StringHelper.maskPropertyValue(header.getKey(), header.getValue()));
        }
        msg.append("]\n");
        msg.append("> cookies: [");
        for (Entry<String, Cookie> cookie : requestContext.getCookies().entrySet()) {
            msg.append("\n>    ").append(cookie.getKey()).append(":").append(StringHelper.maskPropertyValue(cookie.getKey(), cookie.getValue()));
        }
        msg.append("]\n");
        return msg.toString();
    }

}
