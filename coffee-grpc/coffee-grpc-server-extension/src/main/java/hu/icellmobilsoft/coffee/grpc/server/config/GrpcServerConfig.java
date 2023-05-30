package hu.icellmobilsoft.coffee.grpc.server.config;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * gRPC server configuration
 * 
 * <pre>
 * coffee:
 *   grpc:
 *     server:
 *       port: 8199
 *       maxConnectionAge: 60
 *       maxConnectionAgeGrace: 60
 *       maxInboundMessageSize: 4 * 1024 * 1024
 *       maxInboundMetadataSize: 8192
 *       keepAliveTime: 5
 *       keepAliveTimout: 20
 *       permitKeepAliveTime: 5
 *       permitKeepAliveWithoutCalls: false
 *       threadpool.default.corePoolSize: 32
 *       threadpool.default.maximumPoolSize: 32
 *       threadpool.default.keepAliveTime: 0 #milliseconds
 *       threadpool.jakarta.active: true  #default false
 * </pre>
 * 
 * @author czenczl
 * @since 2.1.0
 */
@Dependent
public class GrpcServerConfig implements IGrpcServerConfig {

    /**
     * Config delimiter
     */
    public static final String KEY_DELIMITER = ".";

    /**
     * Prefix for all configs
     */
    public static final String GRPC_SERVER_PREFIX = "coffee.grpc";

    /**
     * gRPC server {@value} config
     */
    public static final String PORT = "port";
    /**
     * gRPC server {@value} config
     */
    public static final String MAX_CONNECTION_AGE = "maxConnectionAge";
    /**
     * gRPC server {@value} config
     */
    public static final String MAX_CONNECTION_AGE_GRACE = "maxConnectionAgeGrace";
    /**
     * gRPC server {@value} config
     */
    public static final String MAX_INBOUND_MESSAGE_SIZE = "maxInboundMessageSize";
    /**
     * gRPC server {@value} config
     */
    public static final String MAX_INBOUND_METADATA_SIZE = "maxInboundMetadataSize";
    /**
     * gRPC server {@value} config
     */
    public static final String MAX_CONNECTION_IDLE = "maxConnectionIdle";
    /**
     * gRPC server {@value} config
     */
    public static final String KEEP_ALIVE_TIME = "keepAliveTime";
    /**
     * gRPC server {@value} config
     */
    public static final String KEEP_ALIVE_TIMEOUT = "keepAliveTimout";
    /**
     * gRPC server {@value} config
     */
    public static final String PERMIT_KEEP_ALIVE_TIME = "permitKeepAliveTime";
    /**
     * gRPC server {@value} config
     */
    public static final String PERMIT_KEEP_ALIVE_WITHOUT_CALLS = "permitKeepAliveWithoutCalls";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_CORE_POOL_SIZE = "threadpool.default.corePoolSize";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_MAXIMUM_POOL_SIZE = "threadpool.default.maximumPoolSize";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_KEEP_ALIVE_TIME = "threadpool.default.keepAliveTime";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_JAKARTA_ACTIVE = "threadpool.jakarta.active";

    @Inject
    private Config config;

    private String configKey;

    @Override
    public Integer getPort() throws BaseException {
        return config.getOptionalValue(joinKey(PORT), Integer.class).orElse(8199);
    }

    @Override
    public Long getMaxConnectionAge() throws BaseException {
        return config.getOptionalValue(joinKey(MAX_CONNECTION_AGE), Long.class).orElse(Long.MAX_VALUE);
    }

    @Override
    public Long getMaxConnectionAgeGrace() throws BaseException {
        return config.getOptionalValue(joinKey(MAX_CONNECTION_AGE_GRACE), Long.class).orElse(Long.MAX_VALUE);
    }

    @Override
    public Long getKeepAliveTime() throws BaseException {
        return config.getOptionalValue(joinKey(KEEP_ALIVE_TIME), Long.class).orElse(5L);
    }

    @Override
    public Long getKeepAliveTimeout() throws BaseException {
        return config.getOptionalValue(joinKey(KEEP_ALIVE_TIMEOUT), Long.class).orElse(20L);
    }

    @Override
    public Long getMaxConnectionIdle() throws BaseException {
        return config.getOptionalValue(joinKey(MAX_CONNECTION_IDLE), Long.class).orElse(Long.MAX_VALUE);
    }

    @Override
    public Integer getMaxInboundMessageSize() throws BaseException {
        return config.getOptionalValue(joinKey(MAX_INBOUND_MESSAGE_SIZE), Integer.class).orElse(4 * 1024 * 1024);
    }

    @Override
    public Integer getMaxInboundMetadataSize() throws BaseException {
        return config.getOptionalValue(joinKey(MAX_INBOUND_METADATA_SIZE), Integer.class).orElse(8192);
    }

    @Override
    public Long getPermitKeepAliveTime() throws BaseException {
        return config.getOptionalValue(joinKey(PERMIT_KEEP_ALIVE_TIME), Long.class).orElse(5L);
    }

    @Override
    public boolean isPermitKeepAliveWithoutCalls() throws BaseException {
        return config.getOptionalValue(joinKey(PERMIT_KEEP_ALIVE_WITHOUT_CALLS), Boolean.class).orElse(false);
    }

    @Override
    public Integer getThreadPoolCorePoolSize() throws BaseException {
        return config.getOptionalValue(joinKey(THREAD_POOL_CORE_POOL_SIZE), Integer.class).orElse(32);
    }

    @Override
    public Integer getThreadPoolMaximumPoolSize() throws BaseException {
        return config.getOptionalValue(joinKey(THREAD_POOL_MAXIMUM_POOL_SIZE), Integer.class).orElse(32);
    }

    @Override
    public Long getThreadPoolKeepAliveTime() throws BaseException {
        return config.getOptionalValue(joinKey(THREAD_POOL_KEEP_ALIVE_TIME), Long.class).orElse(0L);
    }

    @Override
    public boolean isThreadPoolJakartaActive() throws BaseException {
        return config.getOptionalValue(joinKey(THREAD_POOL_JAKARTA_ACTIVE), Boolean.class).orElse(false);
    }

    /**
     * Getter for the field {@code configKey}.
     *
     * @return configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Setter for the field {@code configKey}.
     *
     * @param configKey
     *            configKey
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, GRPC_SERVER_PREFIX, getConfigKey(), key);
    }

}
