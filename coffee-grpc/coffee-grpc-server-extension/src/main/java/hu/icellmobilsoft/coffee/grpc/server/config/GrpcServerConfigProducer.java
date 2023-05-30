package hu.icellmobilsoft.coffee.grpc.server.config;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Producer for creating ManagedRedisConfig
 *
 * @author czenczl
 * @since 2.1.0
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
