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

import hu.icellmobilsoft.coffee.grpc.base.exception.ExceptionHandler;
import hu.icellmobilsoft.coffee.grpc.base.exception.StatusResponse;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * 
 * The {@code ErrorHandlerInterceptor} class is a gRPC error interceptor that intercepts incoming requests and handles any exceptions thrown during
 * the request handling process.
 * <p>
 * The interceptor uses the {@link ExceptionHandler} singleton to handle exceptions thrown during the request handling process. If an exception is
 * thrown, the exception handler returns a {@code StatusResponse} object containing a gRPC {@code Status} and metadata. The interceptor then closes
 * the call with the returned status and metadata.
 * 
 * </p>
 * 
 * @author mark.petrenyi
 * @since 2.1.0
 */
public class ErrorHandlerInterceptor implements ServerInterceptor {

    /**
     * Default constructor, constructs a new object.
     */
    public ErrorHandlerInterceptor() {
        super();
    }

    /**
     * Intercepts incoming requests and handles any exceptions thrown during the request handling process.
     *
     * <p>
     * This method creates a {@link SimpleForwardingServerCallListener} which wraps the context listener, and overrides its
     * {@link SimpleForwardingServerCallListener#onMessage(Object)} and {@link SimpleForwardingServerCallListener#onHalfClose()} methods to handle
     * exceptions thrown during the request handling process.
     * </p>
     *
     * @param serverCall
     *            the incoming gRPC call
     * @param headers
     *            the metadata headers of the incoming call
     * @param next
     *            the next call handler in the chain
     * @return the context listener with exception handling capabilities
     */
    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        Context context = Context.current();
        Listener<ReqT> ctxlistener = Contexts.interceptCall(context, serverCall, headers, next);

        // intercept request, log sent message, handle MDC
        return new SimpleForwardingServerCallListener<>(ctxlistener) {
            @Override
            public void onMessage(ReqT message) {
                try {
                    super.onMessage(message);
                } catch (Throwable e) {
                    StatusResponse status = ExceptionHandler.getInstance().handle(e);
                    serverCall.close(status.getStatus(), status.getMetadata());
                }
            }

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Throwable e) {
                    StatusResponse status = ExceptionHandler.getInstance().handle(e);
                    serverCall.close(status.getStatus(), status.getMetadata());
                }
            }
        };

    }

}
