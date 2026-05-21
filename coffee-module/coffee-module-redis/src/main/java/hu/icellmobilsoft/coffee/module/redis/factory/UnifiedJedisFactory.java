/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.factory;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.RedisClusterClient;
import redis.clients.jedis.RedisSentinelClient;
import redis.clients.jedis.UnifiedJedis;

/**
 * Factory class for creating {@link UnifiedJedis} instances
 * 
 * @author janos.boroczki
 * @since 2.12.0
 */
public class UnifiedJedisFactory {

    private UnifiedJedisFactory() {
    }

    /**
     * Creates a {@link UnifiedJedis} instance based on managedRedisConfig. {@link RedisClusterClient} instance will be created in case of
     * {@link ManagedRedisConfig#getClusterHostAndPortSet()} set is not empty, otherwise {@link RedisClient} instance will be created.
     * 
     * @param managedRedisConfig
     *            Configuration object for redis connection.
     * @return Created {@link UnifiedJedis} object
     */
    public static UnifiedJedis create(ManagedRedisConfig managedRedisConfig) {
        return create(managedRedisConfig, managedRedisConfig.getTimeout());
    }

    /**
     * Creates a {@link UnifiedJedis} instance based on managedRedisConfig.
     * {@link RedisClusterClient} instance will be created in case of {@link ManagedRedisConfig#getClusterHostAndPortSet()} set is not empty,
     * {@link RedisSentinelClient} instance will be created in case of {@link ManagedRedisConfig#getSentinelHostAndPortSet()} set is not empty
     * and {@link ManagedRedisConfig#getSentinelMaster()} is set,
     * otherwise {@link RedisClient} instance will be created.
     *
     * @param managedRedisConfig
     *            Configuration object for redis connection.
     * @param timeout
     *            redis connection timeout
     * @return Created {@link UnifiedJedis} object
     */
    public static UnifiedJedis create(ManagedRedisConfig managedRedisConfig, int timeout) {
        Set<HostAndPort> clusterHostAndPortList = managedRedisConfig.getClusterHostAndPortSet();
        Set<HostAndPort> sentinelHostAndPortList = managedRedisConfig.getSentinelHostAndPortSet();
        String sentinelMaster = managedRedisConfig.getSentinelMaster();
        ConnectionPoolConfig poolConfig = createPoolConfig(managedRedisConfig);
        DefaultJedisClientConfig clientConfig = createClientConfig(managedRedisConfig, managedRedisConfig.getDatabase(), timeout);

        if (!clusterHostAndPortList.isEmpty()) {
            return RedisClusterClient.builder().clientConfig(clientConfig).poolConfig(poolConfig).nodes(clusterHostAndPortList).maxAttempts(3).build();
        } else if (!sentinelHostAndPortList.isEmpty() && StringUtils.isNoneBlank(sentinelMaster)) {
            return RedisSentinelClient.builder().clientConfig(clientConfig).poolConfig(poolConfig).masterName(sentinelMaster).sentinels(sentinelHostAndPortList).build();
        }

        return RedisClient.builder()
                .clientConfig(clientConfig)
                .poolConfig(poolConfig)
                .hostAndPort(managedRedisConfig.getHost(), managedRedisConfig.getPort())
                .build();
    }

    private static DefaultJedisClientConfig createClientConfig(ManagedRedisConfig managedRedisConfig, int database, int timeout) {
        return DefaultJedisClientConfig.builder().timeoutMillis(timeout).password(managedRedisConfig.getPassword()).database(database).build();
    }

    private static ConnectionPoolConfig createPoolConfig(ManagedRedisConfig managedRedisConfig) {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(managedRedisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(managedRedisConfig.getPoolMaxIdle());
        return poolConfig;
    }

}
