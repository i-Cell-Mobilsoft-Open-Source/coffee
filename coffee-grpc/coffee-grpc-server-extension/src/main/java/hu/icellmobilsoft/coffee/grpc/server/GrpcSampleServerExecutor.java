package hu.icellmobilsoft.coffee.grpc.server;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Sample gRPC server executor
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@Dependent
public class GrpcSampleServerExecutor implements IGrpcServerExecutor {

    @Inject
    private GrpcServerManager grpcServerManager;

    @Override
    public void run() {

        try {
            grpcServerManager.init();
        } catch (BaseException e) {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
        // own server management implementation, this is being managed under JakartaEE,
        // a sample server management manager similar to ours, until there is no progress in Jakarta
        // https://projects.eclipse.org/projects/ee4j.rpc/reviews/creation-review
        grpcServerManager.startServer();

    }

}
