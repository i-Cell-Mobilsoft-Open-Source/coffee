package hu.icellmobilsoft.coffee.grpc.server.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * Qualifier for instantiating {@link GrpcServerConfig}
 *
 * @author czenczl
 * @since 2.1.0
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface GrpcServerConnection {

    /**
     * Config key of the desired gRPC connection. <br>
     * 
     * @return config key
     */
    @Nonbinding
    String configKey();

    /**
     * Supports inline instantiation of the {@link GrpcServerConnection} qualifier.
     *
     * @author czenczl
     *
     */
    final class Literal extends AnnotationLiteral<GrpcServerConnection> implements GrpcServerConnection {

        private static final long serialVersionUID = 1L;

        /**
         * gRPC server configuration key
         */
        private final String configKey;

        /**
         * Instantiates the literal with configKey
         *
         * @param configKey
         *            config key
         */
        public Literal(String configKey) {
            this.configKey = configKey;
        }

        @Override
        @Nonbinding
        public String configKey() {
            return configKey;
        }

    }
}
