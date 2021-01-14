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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.module.mp.restclient.RestClientPriority;
import hu.icellmobilsoft.coffee.rest.log.RequestResponseLogger;
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
public class DefaultLoggerClientRequestFilter implements ClientRequestFilter {

    @Inject
    @ThisLogger
    private AppLogger log;

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
        msg.append(logEntity(requestContext));
        LogProducer.getAppLogger(getClass()).get().info(msg.toString());
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

    /**
     * Logs entity, trimmed according to {@link LogSpecifierTarget#CLIENT_REQUEST}.
     *
     * @param requestContext
     *            context
     * @return entity {@link String}
     * @throws IOException
     *             exception
     */
    protected String logEntity(ClientRequestContext requestContext) throws IOException {
        StringBuilder msg = new StringBuilder();
        Object entity = requestContext.getEntity();
        MediaType mediaType = requestContext.getMediaType();
        if (entity != null) {
            msg.append(
                    requestResponseLogger.printEntity(entity, RestLoggerUtil.getMaxEntityLogSize(requestContext, LogSpecifierTarget.CLIENT_REQUEST),
                            RequestResponseLogger.REQUEST_PREFIX, true, mediaType));
        } else {
            OutputStreamCopier osc = new OutputStreamCopier(requestContext.getEntityStream());
            requestContext.setEntityStream(osc);
            String requestText = new String(osc.getCopy(), StandardCharsets.UTF_8);
            msg.append(requestResponseLogger.printEntity(requestText,
                    RestLoggerUtil.getMaxEntityLogSize(requestContext, LogSpecifierTarget.CLIENT_REQUEST), RequestResponseLogger.REQUEST_PREFIX, true,
                    mediaType));
        }
        return msg.toString();
    }
}
