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
import org.apache.commons.lang3.reflect.MethodUtils;

import hu.icellmobilsoft.coffee.grpc.api.metadata.IGrpcHeader;
import hu.icellmobilsoft.coffee.grpc.server.log.GrpcLogging;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifiers;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;
import hu.icellmobilsoft.coffee.rest.utils.RestLoggerUtil;
import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * gRPC request interceptor example
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.1.0
 *
 */
public class ServerRequestInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = DefaultLogger.getLogger(ServerRequestInterceptor.class);

    /**
     * Default constructor, constructs a new object.
     */
    public ServerRequestInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        String extSessionIdHeader = headers.get(IGrpcHeader.HEADER_SID);
        String extSessionId = StringUtils.isNotBlank(extSessionIdHeader) ? extSessionIdHeader : RandomUtil.generateId();
        Context context = Context.current().withValue(GrpcLogging.CONTEXT_KEY_SESSIONID, extSessionId);

        // it's fix on startup, maybe it should be cached
        int requestLogSize = getRequestLogSize(serverCall.getMethodDescriptor());

        Listener<ReqT> ctxlistener = Contexts.interceptCall(context, serverCall, headers, next);
        // intercept request, log sent message, handle MDC
        return new SimpleForwardingServerCallListener<>(ctxlistener) {

            StringBuilder messageToPrint = new StringBuilder();
            int count = 0;

            @Override
            public void onCancel() {
                GrpcLogging.handleMdc(extSessionId);
                super.onCancel();
                String serviceName = serverCall.getMethodDescriptor().getServiceName();
                String methodName = serverCall.getMethodDescriptor().getBareMethodName();
                LOGGER.info("Call [{0}].[{1}] request message onCancel in [{2}] parts: [\n{3}]", serviceName, methodName, count, messageToPrint);
            }

            @Override
            public void onComplete() {
                GrpcLogging.handleMdc(extSessionId);
                super.onComplete();
                String serviceName = serverCall.getMethodDescriptor().getServiceName();
                String methodName = serverCall.getMethodDescriptor().getBareMethodName();
                LOGGER.info("Call [{0}].[{1}] request message onComplete in [{2}] parts: [\n{3}]", serviceName, methodName, count, messageToPrint);
            }

            @Override
            public void onMessage(ReqT message) {
                GrpcLogging.handleMdc(extSessionId);
                String part = "[#" + ++count + "#]\n";
                // reduce logging on multiple onNext messaging (e.g file upload)
                // logging first 4 part and every 1K multiplier
                boolean logging = count < 5 || count % 1000 == 0;
                if (LOGGER.isTraceEnabled() && logging) {
                    String serviceName = serverCall.getMethodDescriptor().getServiceName();
                    String methodName = serverCall.getMethodDescriptor().getBareMethodName();
                    LOGGER.trace("Call [{0}].[{1}] onMessage part [{2}]", serviceName, methodName, count);
                }
                if (requestLogSize > LogSpecifier.UNLIMIT) {
                    if (messageToPrint.length() < requestLogSize) {
                        messageToPrint.append(part).append(StringUtils.truncate(message.toString(), requestLogSize - messageToPrint.length()));
                        if (messageToPrint.length() >= requestLogSize) {
                            messageToPrint.append("...<truncated>");
                        }
                    }
                } else {
                    messageToPrint.append(part).append(message.toString());
                }
                super.onMessage(message);
            }
        };
    }

    /**
     * Getting defined max logging size value for request
     * 
     * @param <ReqT>
     *            GRPC request message type
     * @param <RespT>
     *            GRPC response message type
     * @param methodDescriptor
     *            Triggered GRPC method on call
     * @return Defined (or not) request body log size. If not defined then {@link LogSpecifier#UNLIMIT}
     */
    protected <ReqT, RespT> int getRequestLogSize(MethodDescriptor<ReqT, RespT> methodDescriptor) {
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
        return getMaxEntityLogSize(oMethod.get(), LogSpecifierTarget.REQUEST);
    }

    /**
     * Returns max target entity log size by {@code LogSpecifiers} and {@code LogSpecifier}
     *
     * @param method
     *            Annotated method with {@code LogSpecifiers} or {@code LogSpecifier}
     * @param target
     *            Log target to search
     * @return Max entity log size. If not defined then {@link LogSpecifier#UNLIMIT}
     */
    public static int getMaxEntityLogSize(Method method, LogSpecifierTarget target) {
        if (method == null) {
            return LogSpecifier.UNLIMIT;
        }
        LogSpecifiers logSpecifiers = MethodUtils.getAnnotation(method, LogSpecifiers.class, false, false);
        if (logSpecifiers != null) {
            return RestLoggerUtil.getMaxEntityLogSize(target, logSpecifiers.value());
        } else {
            LogSpecifier logSpecifier = MethodUtils.getAnnotation(method, LogSpecifier.class, false, false);
            return RestLoggerUtil.getMaxEntityLogSize(target, logSpecifier);
        }
    }
}
