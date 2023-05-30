package hu.icellmobilsoft.coffee.grpc.server.interceptor;

import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/**
 * gRPC response interceptor example
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
public class ServerResponseInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = DefaultLogger.getLogger(ServerResponseInterceptor.class);

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> next) {

        // intercept response, log sent message, handle MDC
        return next.startCall(new SimpleForwardingServerCall<>(serverCall) {
            @Override
            public void sendMessage(RespT message) {
                LOGGER.info("Sending response message to client: [{0}]", message);
                super.sendMessage(message);
            }

            @Override
            public void close(Status status, Metadata trailers) {
                LOGGER.info("Status on close: [{0}]", status);
                super.close(status, trailers);
            }

        }, metadata);
    }

}
