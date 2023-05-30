/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.server.interceptor;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * gRPC request interceptor example
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
public class ServerRequestInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = DefaultLogger.getLogger(ServerRequestInterceptor.class);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        String extSessionIdHeader = headers.get(Metadata.Key.of(LogConstants.LOG_SESSION_ID, Metadata.ASCII_STRING_MARSHALLER));
        String extSessionId = StringUtils.isNotBlank(extSessionIdHeader) ? extSessionIdHeader : RandomUtil.generateId();
        Context context = Context.current();
        Listener<ReqT> ctxlistener = Contexts.interceptCall(context, serverCall, headers, next);

        // intercept request, log sent message, handle MDC
        return new SimpleForwardingServerCallListener<>(ctxlistener) {

            @Override
            public void onMessage(ReqT message) {
                MDC.put(LogConstants.LOG_SESSION_ID, extSessionId);
                LOGGER.info("Request message: [{0}]", message);
                super.onMessage(message);
            }

        };

    }

}
