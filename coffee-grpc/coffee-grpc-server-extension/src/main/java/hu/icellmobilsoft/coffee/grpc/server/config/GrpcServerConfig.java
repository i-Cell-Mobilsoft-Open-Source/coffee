/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
 *       port: 8199 # default 8199
 *       maxConnectionAge: 60000000 # nano seconds, default Long.MAX_VALUE
 *       maxConnectionAgeGrace: 60000000 # nano seconds, default Long.MAX_VALUE
 *       maxInboundMessageSize: 4194304 # Byte, defult 4 * 1024 * 1024 (4MiB)
 *       maxInboundMetadataSize: 8192 # Byte, default 8192 (8KiB)
 *       maxConnectionIdle: 60000000 # nano seconds, default Long.MAX_VALUE
 *       keepAliveTime: 5 # minutes, default 5
 *       keepAliveTimeout: 20 # seconds, default 20
 *       permitKeepAliveTime: 5 # minutes, default 5
 *       permitKeepAliveWithoutCalls: false # default false
 *       threadPool:
 *         jakarta:
 *           active: true  # default false, if true then coffee.grpc.server.threadpool.default is ignored
 *         default:
 *           corePoolSize: 32 # default 32
 *           maximumPoolSize: 32 # default 32
 *           keepAliveTime: 0 # milliseconds, default 0
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
    public static final String KEEP_ALIVE_TIMEOUT = "keepAliveTimeout";
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
    public static final String THREAD_POOL_CORE_POOL_SIZE = "threadPool.default.corePoolSize";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_MAXIMUM_POOL_SIZE = "threadPool.default.maximumPoolSize";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_KEEP_ALIVE_TIME = "threadPool.default.keepAliveTime";

    /**
     * gRPC server thread pool {@value} config
     */
    public static final String THREAD_POOL_JAKARTA_ACTIVE = "threadPool.jakarta.active";

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
