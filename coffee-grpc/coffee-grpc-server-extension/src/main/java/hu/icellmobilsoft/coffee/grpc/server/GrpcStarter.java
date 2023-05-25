package hu.icellmobilsoft.coffee.grpc.server;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Start/Stop gRPC server
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
@ApplicationScoped
public class GrpcStarter {

    @Inject
    private Logger log;

    @Resource
    private ManagedExecutorService executorService;

    /**
     * Start gRPC server in unmanaged mode
     * 
     * @param init
     *            observe object
     */
    public void begin(@Observes @Initialized(ApplicationScoped.class) Object init) {
        // run in own thread
        start();
    }

    /**
     * Start gRPC server in unmanaged mode
     * 
     * @param init
     *            observe object
     */
    public void end(@Observes @Destroyed(ApplicationScoped.class) Object payload) {
        stop();
    }

    /**
     * Start gRPC server in unmanaged mode
     */
    protected void start() {
        log.info("Starting grpc server service...");

        IGrpcServerExecutor executor = CDI.current().select(GrpcSampleServerExecutor.class).get();
        executorService.execute(executor);

        log.info("Grpc server started");
    }

    /**
     * Stop all gRPC server
     */
    protected void stop() {
        log.info("Stopping grpc server services...");

        Instance<GrpcServerManager> grpcServerManagerInstance = CDI.current().select(GrpcServerManager.class);
        grpcServerManagerInstance.forEach(GrpcServerManager::stopServer);

        log.info("Grpc server stopped");
    }

}
