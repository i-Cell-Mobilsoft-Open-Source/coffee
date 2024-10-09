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
package hu.icellmobilsoft.coffee.grpc.client.interceptor;

import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.grpc.client.config.GrpcClientConfig;
import hu.icellmobilsoft.coffee.grpc.client.extension.GrpcClientProducerFactory;
import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.MethodDescriptor;

/**
 * gRPC client request interceptor
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
public class ClientRequestInterceptor implements ClientInterceptor {

    private static final Logger LOGGER = DefaultLogger.getLogger(ClientRequestInterceptor.class);

    /**
     * Default constructor, constructs a new object.
     */
    public ClientRequestInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        final String configKey = callOptions.getOption(GrpcClientProducerFactory.CONFIG_VALUE_KEY);

        return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            int logSize = 0;
            int count = 0;

            @Override
            public void sendMessage(ReqT message) {

                int requestLogSize = CDI.current().select(GrpcClientConfig.class, new GrpcClient.Literal(configKey)).get().getRequestLogSize();

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Sending request message part [{0}]: [{1}]", count++, message.toString());
                } else {
                    StringBuilder messageToPrint = new StringBuilder();

                    String messageString = message.toString();
                    if (messageString.length() > requestLogSize - logSize) {
                        if (requestLogSize - logSize > 0) {
                            messageToPrint.append(StringUtils.truncate(messageString, requestLogSize - logSize));
                            logSize += messageToPrint.length();
                            messageToPrint.append("...<truncated>");
                        }
                    } else {
                        messageToPrint.append(messageString);
                        logSize += messageToPrint.length();
                    }

                    if (!messageToPrint.isEmpty()) {
                        LOGGER.info("Sending request message part [{0}]: [{1}]", count++, messageToPrint.toString());
                    }
                }

                super.sendMessage(message);
            }

        };
    }

}
