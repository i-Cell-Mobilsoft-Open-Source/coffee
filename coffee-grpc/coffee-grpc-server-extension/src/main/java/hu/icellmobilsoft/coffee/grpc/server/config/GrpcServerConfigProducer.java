package hu.icellmobilsoft.coffee.grpc.server.config;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Producer for creating ManagedRedisConfig
 *
 * @author czenczl
 * @since 1.14.0
 */
@ApplicationScoped
public class GrpcServerConfigProducer {

    /**
     * Creates {@link GrpcServerConfig} for the injected config key
     *
     * @param injectionPoint
     *            injection metadata
     * @return GrpcServerConfig
     */
    @Produces
    @Dependent
    @GrpcServerConnection(configKey = "")
    public GrpcServerConfig produceGrpcServerConfig(InjectionPoint injectionPoint) {
        Optional<GrpcServerConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcServerConnection.class);
        String configKey = annotation.map(GrpcServerConnection::configKey).orElse(null);
        GrpcServerConfig grpcServerConfig = CDI.current().select(GrpcServerConfig.class).get();
        grpcServerConfig.setConfigKey(configKey);
        return grpcServerConfig;
    }

}
