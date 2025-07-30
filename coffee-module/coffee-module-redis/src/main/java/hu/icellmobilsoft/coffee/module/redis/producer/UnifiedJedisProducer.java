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
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.metric.spi.IJedisMetricsHandler;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
import hu.icellmobilsoft.coffee.module.redis.config.RedisConfig;
import hu.icellmobilsoft.coffee.module.redis.factory.UnifiedJedisFactory;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import redis.clients.jedis.ConnectionPool;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

/**
 * Producer for creating or obtaining UnifiedJedis
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@ApplicationScoped
public class UnifiedJedisProducer {

    /**
     * Delimiter fo inner cache key
     */
    public static final String DELIMITER = "_";

    private static final String NOT_SUPPORTED_UNIFIED_JEDIS = "[{0}] is not a supported UnifiedJedis descendant";

    @Inject
    private Logger log;

    @Inject
    private IJedisMetricsHandler jedisMetricsHandler;

    private Map<String, UnifiedJedis> unifiedJedisInstances = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public UnifiedJedisProducer() {
        super();
    }

    /**
     * Creates or gets {@link JedisPooled} for the given configKey
     *
     * @param injectionPoint
     *            injection metadata
     * @return Jedis pool
     */
    @Produces
    @Dependent
    @RedisConnection(configKey = "", poolConfigKey = "")
    public UnifiedJedis getUnifiedJedis(InjectionPoint injectionPoint) {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);
        String configKey = annotation.map(RedisConnection::configKey).orElse(null);
        String poolConfigKey = annotation.map(RedisConnection::poolConfigKey).orElse(RedisConfig.POOL_CONFIG_KEY_DEFAULT_VALUE);

        UnifiedJedis unifiedJedis = getInstance(configKey, poolConfigKey);

        jedisMetricsHandler.addMetric(configKey, poolConfigKey, getNumActiveSupplier(unifiedJedis), getNumIdleSupplier(unifiedJedis));
        return unifiedJedis;
    }

    private Supplier<Number> getNumActiveSupplier(UnifiedJedis unifiedJedis) {
        if (unifiedJedis instanceof JedisPooled jedisPooled) {
            return jedisPooled.getPool()::getNumActive;
        }

        if (unifiedJedis instanceof JedisCluster jedisCluster) {
            return jedisCluster.getClusterNodes().values().stream().mapToInt(ConnectionPool::getNumActive)::sum;
        }

        throw new IllegalArgumentException(MessageFormat.format(NOT_SUPPORTED_UNIFIED_JEDIS, unifiedJedis.getClass()));
    }

    private Supplier<Number> getNumIdleSupplier(UnifiedJedis unifiedJedis) {
        if (unifiedJedis instanceof JedisPooled jedisPooled) {
            return jedisPooled.getPool()::getNumIdle;
        }

        if (unifiedJedis instanceof JedisCluster jedisCluster) {
            return jedisCluster.getClusterNodes().values().stream().mapToInt(ConnectionPool::getNumIdle)::sum;
        }

        throw new IllegalArgumentException(MessageFormat.format(NOT_SUPPORTED_UNIFIED_JEDIS, unifiedJedis.getClass()));
    }

    /**
     * Returns the jedisPooled for the given configKey and poolConfigKey. Returned pools are cached by the configKey + poolConfigKey. In case
     * poolConfigKey is null, default value will be used. Synchronized in order to prevent creating multiple pools for the same connection.
     *
     *
     * @param configKey
     *            config key
     * @param poolConfigKey
     *            config key for jedis pool
     * @return {@link JedisPooled}
     */
    private synchronized UnifiedJedis getInstance(String configKey, String poolConfigKey) {
        return unifiedJedisInstances.computeIfAbsent(configKey + DELIMITER + poolConfigKey, v -> createUnifiedJedis(configKey, poolConfigKey));
    }

    private UnifiedJedis createUnifiedJedis(String configKey, String poolConfigKey) {
        log.info("Creating JedisPooled for configKey:[{0}]", configKey);
        Instance<ManagedRedisConfig> instance = CDI.current().select(ManagedRedisConfig.class, new RedisConnection.Literal(configKey, poolConfigKey));
        ManagedRedisConfig managedRedisConfig = instance.get();
        try {
            String host = managedRedisConfig.getHost();
            int port = managedRedisConfig.getPort();
            int database = managedRedisConfig.getDatabase();
            Set<HostAndPort> clusterHostAndPortSet = managedRedisConfig.getClusterHostAndPortSet();
            log.info("Redis host [{0}], port: [{1}], database: [{2}], poolConfigKey: [{3}], cluster: [{4}]", host, port, database, poolConfigKey, clusterHostAndPortSet);
            return UnifiedJedisFactory.create(managedRedisConfig);
        } catch (Exception e) {
            log.error(
                    MessageFormat.format(
                            "Exception on initializing JedisPooled for configKey [{0}], poolConfigKey [{1}]: [{2}]",
                            configKey,
                            poolConfigKey,
                            e.getLocalizedMessage()),
                    e);
            return null;
        } finally {
            instance.destroy(managedRedisConfig);
        }
    }

    /**
     * Destroys created unifiedJedisInstances
     */
    @PreDestroy
    public void clear() {
        for (UnifiedJedis unifiedJedis : unifiedJedisInstances.values()) {
            unifiedJedis.close();
        }
        unifiedJedisInstances.clear();
    }

}
