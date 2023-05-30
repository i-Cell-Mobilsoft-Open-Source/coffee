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
