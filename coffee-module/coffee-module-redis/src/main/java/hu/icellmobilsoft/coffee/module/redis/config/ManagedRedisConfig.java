/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.config;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

/**
 * Helper class for obtaining redis connection settings using microprofile config.<br>
 * General pattern is "{@code coffee.redis.${configKey}.${setting}}
 *
 * ie.:
 *
 * <pre>
 *  coffee:
 *    redis:
 *      auth:
 *        host: sample-sandbox.icellmobilsoft.hu
 *        port: 6380
 *        password: ****
 *        database: 1
 *        pool:
 *          default:
 *            maxtotal: 64
 *            maxidle: 16
 *          custom1:
 *            maxtotal: 128
 *            maxidle: 32
 *          custom2:
 *            maxtotal: 256
 *            maxidle: 64
 * </pre>
 *
 * The upper configuration is injectable with:
 *
 * <pre>
 * &#64;Inject
 * &#64;RedisConnection(configKey = "auth")
 * ManagedRedisConfig redisConfig;
 * </pre>
 *
 * or:
 *
 * <pre>
 * ManagedRedisConfig redisConfig = CDI.current().select(ManagedRedisConfig.class, new RedisConnection.Literal("auth")).get();
 * </pre>
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@Dependent
public class ManagedRedisConfig implements RedisConfig {

    /**
     * Constant <code>REDIS_PREFIX="coffee.redis"</code>
     */
    public static final String REDIS_PREFIX = "coffee.redis";

    /**
     * Constant <code>HOST="host"</code>
     */
    public static final String HOST = "host";
    /**
     * Constant <code>PORT="port"</code>
     */
    public static final String PORT = "port";
    /**
     * Constant <code>PASSWORD="password"</code>
     */
    public static final String PASSWORD = "password";
    /**
     * Constant <code>DATABASE="database"</code>
     */
    public static final String DATABASE = "database";
    /**
     * Constant <code>POOL_MAXTOTAL="POOL_CONFIG_KEY.maxtotal"</code>
     */
    public static final String POOL_MAXTOTAL = "maxtotal";
    /**
     * Constant <code>POOL_MAXIDLE="POOL_CONFIG_KEY.maxidle"</code>
     */
    public static final String POOL_MAXIDLE = "maxidle";
    /**
     * Constant <code>TIMEOUT="timeout"</code>
     */
    public static final String TIMEOUT = "timeout";
    /**
     * Constant <code>KEY_DELIMITER="."</code>
     */
    public static final String KEY_DELIMITER = ".";
    /**
     * Constant <code>POOL="pool"</code>
     */
    private static final String POOL = "pool";

    @Inject
    private Config config;

    private String configKey;

    private String poolConfigKey;

    /**
     * Default constructor, constructs a new object.
     */
    public ManagedRedisConfig() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * The host where the selected redis is available.
     */
    @Override
    public String getHost() {
        return config.getOptionalValue(joinKey(HOST), String.class).orElse("localhost");
    }

    /**
     * {@inheritDoc}
     *
     * The port where the selected redis is available.
     */
    @Override
    public Integer getPort() {
        return config.getOptionalValue(joinKey(PORT), Integer.class).orElse(6380);
    }

    /**
     * {@inheritDoc}
     *
     * The password of the selected redis to connect with.
     */
    @Override
    public String getPassword() {
        return config.getOptionalValue(joinKey(PASSWORD), String.class).orElse(null);
    }

    /**
     * {@inheritDoc}
     *
     * The number of the selected database to connect with.
     */
    @Override
    public Integer getDatabase() {
        return config.getOptionalValue(joinKey(DATABASE), Integer.class).orElse(0);
    }

    /**
     * {@inheritDoc}
     *
     * The maximum number of objects that can be allocated by the pool (checked out to clients, or idle awaiting checkout) at a given time. When
     * negative, there is no limit to the number of objects that can be managed by the pool at one time.
     */
    @Override
    public Integer getPoolMaxTotal() {
        return config.getOptionalValue(joinKey(POOL + KEY_DELIMITER + getPoolConfigKey() + "." + POOL_MAXTOTAL), Integer.class).orElse(64);
    }

    /**
     * {@inheritDoc}
     *
     * The cap on the number of "idle" instances in the pool. If maxIdle is set too low on heavily loaded systems it is possible you will see objects
     * being destroyed and almost immediately new objects being created. This is a result of the active threads momentarily returning objects faster
     * than they are requesting them, causing the number of idle objects to rise above maxIdle.
     */
    @Override
    public Integer getPoolMaxIdle() {
        return config.getOptionalValue(joinKey(POOL + KEY_DELIMITER + getPoolConfigKey() + "." + POOL_MAXIDLE), Integer.class).orElse(16);
    }

    /**
     * {@inheritDoc}
     *
     * Redis connection timout in millisec.
     */
    @Override
    public Integer getTimeout() {
        return config.getOptionalValue(joinKey(TIMEOUT), Integer.class).orElse(5000);
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
     *            configKey to set
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * Getter for the field {@code poolConfigKey}.
     *
     * @return poolConfigKey
     * @since 1.11.0
     */
    public String getPoolConfigKey() {
        return poolConfigKey;
    }

    /**
     * Setter for the field {@code poolConfigKey}.
     *
     * @param poolConfigKey
     *            poolConfigKey to set
     * @since 1.11.0
     */
    public void setPoolConfigKey(String poolConfigKey) {
        this.poolConfigKey = poolConfigKey;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, REDIS_PREFIX, configKey, key);
    }
}
