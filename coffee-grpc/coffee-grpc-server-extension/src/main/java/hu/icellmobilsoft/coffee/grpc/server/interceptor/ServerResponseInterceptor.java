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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.grpc.server.log.GrpcLogging;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;
import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * gRPC response interceptor example
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.1.0
 *
 */
public class ServerResponseInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = DefaultLogger.getLogger(ServerResponseInterceptor.class);

    /**
     * Default constructor, constructs a new object.
     */
    public ServerResponseInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {

        // ez felfutasnal fix, lehet cachelni kellene
        int responseLogSize = getResponseLogSize(serverCall.getMethodDescriptor());

        ServerCall<ReqT, RespT> forwardingServerCall = new SimpleForwardingServerCall<>(serverCall) {

            StringBuilder messageToPrint = new StringBuilder();
            int count = 0;

            @Override
            public void sendMessage(RespT message) {
                GrpcLogging.handleMdc();
                String part = "[#" + ++count + "#]\n";
                if (responseLogSize > LogSpecifier.UNLIMIT) {
                    if (messageToPrint.length() < responseLogSize) {
                        messageToPrint.append(part).append(StringUtils.truncate(message.toString(), responseLogSize - messageToPrint.length()));
                        if (messageToPrint.length() >= responseLogSize) {
                            messageToPrint.append("...<truncated>");
                        }
                    }
                } else {
                    messageToPrint.append(part).append(message.toString());
                }
                super.sendMessage(message);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                GrpcLogging.handleMdc();
                String serviceName = serverCall.getMethodDescriptor().getServiceName();
                String methodName = serverCall.getMethodDescriptor().getBareMethodName();
                if (status != Status.OK) {
                    LOGGER.error("Error in processing GRPC call [{0}].[{1}], status: [{2}], ", serviceName, methodName, status);
                } else {
                    LOGGER.info("Call [{0}].[{1}] response message close in [{2}] parts:[\n{3}]", serviceName, methodName, count, messageToPrint);
                }
                super.close(status, trailers);
            }

        };

        // intercept response, log sent message, handle MDC
        return next.startCall(forwardingServerCall, metadata);
    }

    /**
     * Getting defined max logging size value for response
     * 
     * @param <ReqT>
     *            GRPC request message type
     * @param <RespT>
     *            GRPC response message type
     * @param methodDescriptor
     *            Triggered GRPC method on call
     * @return Defined (or not) response body log size. If not defined then {@link LogSpecifier#UNLIMIT}
     */
    protected <ReqT, RespT> int getResponseLogSize(MethodDescriptor<ReqT, RespT> methodDescriptor) {
        String serviceName = methodDescriptor.getServiceName();
        String methodName = methodDescriptor.getBareMethodName();
        Class<?> serviceClass;
        try {
            Set<Bean<?>> services = CDI.current().getBeanManager().getBeans(Class.forName(serviceName));
            if (services.isEmpty()) {
                return LogSpecifier.UNLIMIT;
            }
            serviceClass = services.iterator().next().getBeanClass();
        } catch (ClassNotFoundException e) {
            LOGGER.debug(MessageFormat.format("Error on getting Request logging size: [{0}]", e.getLocalizedMessage()), e);
            return LogSpecifier.UNLIMIT;
        }
        Optional<Method> oMethod = Arrays.stream(serviceClass.getDeclaredMethods()).filter(method -> method.getName().equalsIgnoreCase(methodName))
                .findFirst();
        return ServerRequestInterceptor.getMaxEntityLogSize(oMethod.get(), LogSpecifierTarget.RESPONSE);
    }
}
