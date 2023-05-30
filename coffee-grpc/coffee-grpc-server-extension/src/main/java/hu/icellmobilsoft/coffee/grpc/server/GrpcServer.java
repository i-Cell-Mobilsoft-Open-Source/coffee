package hu.icellmobilsoft.coffee.grpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * Qualifier for instantiating {@link GrpcServerManager}
 *
 * @author czenczl
 * @since 2.1.0
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface GrpcServer {

    /**
     * dummy key
     * 
     * @return config key
     */
    @Nonbinding
    String configKey();

    /**
     * Supports inline instantiation of the {@link GrpcServer} qualifier.
     *
     * @author czenczl
     *
     */
    final class Literal extends AnnotationLiteral<GrpcServer> implements GrpcServer {

        private static final long serialVersionUID = 1L;

        /**
         * config key
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

        @Nonbinding
        public String configKey() {
            return configKey;
        }

    }

}
