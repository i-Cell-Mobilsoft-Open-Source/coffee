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
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

/**
 * gRPC client response interceptor
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
public class ClientResponseInterceptor implements ClientInterceptor {

    private static final Logger LOGGER = DefaultLogger.getLogger(ClientResponseInterceptor.class);

    /**
     * Default constructor, constructs a new object.
     */
    public ClientResponseInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        final String configKey = callOptions.getOption(GrpcClientProducerFactory.CONFIG_VALUE_KEY);

        return new SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {

                Listener<RespT> listener = new SimpleForwardingClientCallListener<>(responseListener) {

                    int logSize = 0;
                    int count = 0;

                    @Override
                    public void onMessage(RespT message) {
                        int responseLogSize = CDI.current()
                                .select(GrpcClientConfig.class, new GrpcClient.Literal(configKey))
                                .get()
                                .getResponseLogSize();

                        if (LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Received response message part [{0}]: [{1}]", count++,String.valueOf(message));
                        } else {
                            StringBuilder messageToPrint = new StringBuilder();

                            String messageString = String.valueOf(message);
                            if (messageString.length() > responseLogSize - logSize) {
                                if (responseLogSize - logSize > 0) {
                                    messageToPrint.append(StringUtils.truncate(messageString, responseLogSize - logSize));
                                    logSize += messageToPrint.length();
                                    messageToPrint.append("...<truncated>");
                                }
                            } else {
                                messageToPrint.append(messageString);
                                logSize += messageToPrint.length();
                            }

                            if (messageToPrint.length() > 0) {
                                LOGGER.info("Received response message part [{0}]: [{1}]", count++, messageToPrint.toString());
                            }
                        }

                        super.onMessage(message);
                    }

                    @Override
                    public void onClose(Status status, Metadata trailers) {
                        LOGGER.info("Received status on close: [{0}]", status);
                        super.onClose(status, trailers);
                    }

                };
                super.start(listener, headers);
            }
        };
    }
}
