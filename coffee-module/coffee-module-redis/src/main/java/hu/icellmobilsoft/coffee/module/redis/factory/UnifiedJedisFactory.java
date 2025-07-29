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

import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
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
     * Creates a {@link UnifiedJedis} instance based on managedRedisConfig. {@link JedisCluster} instance will be created in case of
     * {@link ManagedRedisConfig#getClusterHostAndPortSet()} set is not empty, otherwise {@link JedisPooled} instance will be created.
     * 
     * @param managedRedisConfig
     *            Configuration object for redis connection.
     * @return Created {@link UnifiedJedis} object
     */
    public static UnifiedJedis create(ManagedRedisConfig managedRedisConfig) {
        return create(managedRedisConfig, managedRedisConfig.getTimeout());
    }

    /**
     * Creates a {@link UnifiedJedis} instance based on managedRedisConfig. {@link JedisCluster} instance will be created in case of
     * {@link ManagedRedisConfig#getClusterHostAndPortSet()} set is not empty, otherwise {@link JedisPooled} instance will be created.
     *
     * @param managedRedisConfig
     *            Configuration object for redis connection.
     * @param timeout
     *            redis connection timeout
     * @return Created {@link UnifiedJedis} object
     */
    public static UnifiedJedis create(ManagedRedisConfig managedRedisConfig, int timeout) {
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(managedRedisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(managedRedisConfig.getPoolMaxIdle());
        Set<HostAndPort> clusterHostAndPortList = managedRedisConfig.getClusterHostAndPortSet();

        if (clusterHostAndPortList.isEmpty()) {
            return new JedisPooled(
                    poolConfig,
                    managedRedisConfig.getHost(),
                    managedRedisConfig.getPort(),
                    timeout,
                    managedRedisConfig.getPassword(),
                    managedRedisConfig.getDatabase());
        }

        return new JedisCluster(clusterHostAndPortList, timeout, timeout, 3, managedRedisConfig.getPassword(), poolConfig);
    }

}
