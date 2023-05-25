package hu.icellmobilsoft.coffee.grpc.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.grpc.api.service.IGrpcService;
import hu.icellmobilsoft.coffee.grpc.server.config.GrpcServerConfig;
import hu.icellmobilsoft.coffee.grpc.server.config.GrpcServerConnection;
import hu.icellmobilsoft.coffee.grpc.server.config.IGrpcServerConfig;
import hu.icellmobilsoft.coffee.grpc.server.interceptor.ErrorHandlerInterceptor;
import hu.icellmobilsoft.coffee.grpc.server.interceptor.ServerRequestInterceptor;
import hu.icellmobilsoft.coffee.grpc.server.interceptor.ServerResponseInterceptor;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Sample gRPC server manager
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
@Dependent
public class GrpcServerManager {

    @Inject
    private Logger log;

    @Inject
    private BeanManager beanManager;

    @Inject
    @GrpcServerConnection(configKey = IGrpcServerConfig.DEFAULT_SERVER_KEY)
    private GrpcServerConfig serverConfig;

    @Resource
    private ManagedExecutorService managedExecutorService;

    private Server server;

    /**
     * Server initialization, port bind, adding services to the server, defining interceptors
     * 
     * @throws BaseException
     *             on error
     */
    public void init() throws BaseException {
        // grpc servicek gyujtese
        Set<Bean<?>> beans = beanManager.getBeans(IGrpcService.class);
        log.info("Found [{0}] grpc service", beans.size());
        // bind to port
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(serverConfig.getPort());
        // configure server threadpool
        configureServerPool(serverBuilder);
        // configure server
        configureServer(serverBuilder);
        // add interceptor
        addInterceptor(serverBuilder);
        // add services
        beans.forEach((i) -> addService(i, serverBuilder));
        // build server
        server = serverBuilder.build();
    }

    private void configureServer(ServerBuilder<?> serverBuilder) throws BaseException {
        // NettyServerBuilder
        // server config
        serverBuilder.maxConnectionAge(serverConfig.getMaxConnectionAge(), TimeUnit.SECONDS);
        serverBuilder.maxConnectionAgeGrace(serverConfig.getMaxConnectionAgeGrace(), TimeUnit.SECONDS);
        serverBuilder.keepAliveTime(serverConfig.getKeepAliveTime(), TimeUnit.MINUTES);
        serverBuilder.keepAliveTimeout(serverConfig.getKeepAliveTimeout(), TimeUnit.SECONDS);
        serverBuilder.maxConnectionIdle(serverConfig.getMaxConnectionIdle(), TimeUnit.SECONDS);
        serverBuilder.maxInboundMessageSize(serverConfig.getMaxInboundMessageSize());
        serverBuilder.maxInboundMetadataSize(serverConfig.getMaxInboundMetadataSize());
        serverBuilder.permitKeepAliveTime(serverConfig.getPermitKeepAliveTime(), TimeUnit.MINUTES);
        serverBuilder.permitKeepAliveWithoutCalls(serverConfig.isPermitKeepAliveWithoutCalls());
    }

    private void configureServerPool(ServerBuilder<?> serverBuilder) throws BaseException {
        if (serverConfig.isThreadPoolJakartaActive()) {
            serverBuilder.executor(managedExecutorService);
            log.info("gRPC server using Jakarta ManagedExecutorService.");
        } else {
            serverBuilder.executor(createThreadPool());
            log.info("gRPC server using default ThreadPoolExecutor.");
        }
    }

    // simple executor to control server threads
    private Executor createThreadPool() throws BaseException {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        return new ThreadPoolExecutor(serverConfig.getThreadPoolCorePoolSize(), serverConfig.getThreadPoolMaximumPoolSize(),
                serverConfig.getThreadPoolKeepAliveTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
    }

    /**
     * Adding services to the server, start grpc operations interception
     * 
     * @param bean
     *            gRPC service implementation bean
     * @param serverBuilder
     *            GRPC server builder
     */
    private void addService(Bean<?> bean, ServerBuilder<?> serverBuilder) {
        IGrpcService service = (IGrpcService) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
        Class<? extends BindableService> grpcImpl = service.bindableDelegator();
        Constructor<? extends BindableService> constructor = findConstructor(bean, grpcImpl);
        if (constructor != null) {
            try {
                BindableService bindableService = constructor.newInstance(service);
                serverBuilder.addService(bindableService);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.warn(MessageFormat.format(
                        "Could not call constructor of BindableService [{0}], it must have a public constructor with one parameter of [{1}]",
                        grpcImpl, bean.getBeanClass()), e);
            }
        } else {
            log.warn("Could not find constructor of BindableService [{0}], it must have a public constructor with one parameter of [{1}]", grpcImpl,
                    bean.getBeanClass());
        }

    }

    private Constructor<? extends BindableService> findConstructor(Bean<?> bean, Class<? extends BindableService> grpcImpl) {
        if (grpcImpl == null) {
            return null;
        }
        Constructor<?>[] constructors = grpcImpl.getConstructors();
        if (ArrayUtils.isEmpty(constructors)) {
            return null;
        }
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0] != null
                    && constructor.getParameterTypes()[0].isAssignableFrom(bean.getBeanClass())) {
                return (Constructor<? extends BindableService>) constructor;
            }
        }

        return null;
    }

    /**
     * Request/Response sample interceptor, you have to be careful with this, because the call chain is called in a strange way in reverse order, and
     * it can run here several times in one process
     * 
     * @param serverBuilder
     *            GRPC server builder
     */
    private void addInterceptor(ServerBuilder<?> serverBuilder) {
        serverBuilder.intercept(new ErrorHandlerInterceptor()); // 5
        serverBuilder.intercept(new ServerResponseInterceptor()); // 4
        serverBuilder.intercept(new ServerRequestInterceptor()); // 3
    }

    /**
     * Starting the server and waiting. If an error does not occur, it will not start again, because server is not fully managed.
     */
    public void startServer() {
        try {
            server.start();
            log.info("grpc server runnning");
            server.awaitTermination();
        } catch (InterruptedException | IOException e) {
            log.error("grpc server error", e);
        } finally {
            server.shutdownNow();
        }
    }

    /**
     * Stop server
     */
    public void stopServer() {
        server.shutdownNow();
    }
}
