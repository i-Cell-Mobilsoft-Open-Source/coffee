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
package hu.icellmobilsoft.coffee.module.redis.producer;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
import hu.icellmobilsoft.coffee.module.redis.config.RedisConfig;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Producer for creating or obtaining JedisPool
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@ApplicationScoped
public class JedisPoolProducer {

    /**
     * Delimiter fo inner cache key
     */
    public static final String DELIMITER = "_";

    @Inject
    private Logger log;

    private Map<String, JedisPool> jedisPoolInstances = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public JedisPoolProducer() {
        super();
    }

    /**
     * Creates or gets {@link JedisPool} for the given configKey
     *
     * @param injectionPoint
     *            injection metadata
     * @return Jedis pool
     */
    @Produces
    @Dependent
    @RedisConnection(configKey = "", poolConfigKey = "")
    public JedisPool getJedisPool(InjectionPoint injectionPoint) {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);
        String configKey = annotation.map(RedisConnection::configKey).orElse(null);
        String poolConfigKey = annotation.map(RedisConnection::poolConfigKey).orElse(RedisConfig.POOL_CONFIG_KEY_DEFAULT_VALUE);

        return getInstance(configKey, poolConfigKey);
    }

    /**
     * Returns the jedisPool for the given configKey and poolConfigKey. Returned pools are cached by the configKey + poolConfigKey. In case
     * poolConfigKey is null, default value will be used. Synchronized in order to prevent creating multiple pools for the same connection.
     *
     *
     * @param configKey
     *            config key
     * @param poolConfigKey
     *            config key for jedis pool
     * @return {@link JedisPool}
     */
    private synchronized JedisPool getInstance(String configKey, String poolConfigKey) {
        return jedisPoolInstances.computeIfAbsent(configKey + DELIMITER + poolConfigKey, v -> createJedisPool(configKey, poolConfigKey));
    }

    private JedisPool createJedisPool(String configKey, String poolConfigKey) {
        log.info("Creating JedisPool for configKey:[{0}]", configKey);
        Instance<ManagedRedisConfig> instance = CDI.current().select(ManagedRedisConfig.class, new RedisConnection.Literal(configKey, poolConfigKey));
        ManagedRedisConfig managedRedisConfig = instance.get();
        try {
            String host = managedRedisConfig.getHost();
            int port = managedRedisConfig.getPort();
            int database = managedRedisConfig.getDatabase();
            log.info("Redis host [{0}], port: [{1}], database: [{2}], poolConfigKey: [{3}]", host, port, database, poolConfigKey);
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(managedRedisConfig.getPoolMaxTotal());
            poolConfig.setMaxIdle(managedRedisConfig.getPoolMaxIdle());
            return new JedisPool(poolConfig, host, port, managedRedisConfig.getTimeout(), managedRedisConfig.getPassword(), database);
        } catch (Exception e) {
            log.error(MessageFormat.format("Exception on initializing JedisPool for configKey [{0}], poolConfigKey [{1}]: [{2}]", configKey,
                    poolConfigKey, e.getLocalizedMessage()), e);
            return null;
        } finally {
            instance.destroy(managedRedisConfig);
        }
    }

    /**
     * Destroys created jedisPools
     */
    @PreDestroy
    public void clear() {
        for (JedisPool jedisPool : jedisPoolInstances.values()) {
            if (!jedisPool.isClosed()) {
                jedisPool.destroy();
            }
        }
        jedisPoolInstances.clear();
    }

}
