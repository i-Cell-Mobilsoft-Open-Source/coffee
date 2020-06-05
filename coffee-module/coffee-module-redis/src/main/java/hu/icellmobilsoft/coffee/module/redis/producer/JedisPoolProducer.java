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

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
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

    @Inject
    @ThisLogger
    private AppLogger log;

    private Map<String, JedisPool> jedisPoolInstances = new HashMap<>();

    /**
     * Creates or gets JedisPool for the given configKey
     */
    @Produces
    @Dependent
    @RedisConnection(configKey = "")
    public JedisPool getJedisPool(InjectionPoint injectionPoint) {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);
        String configKey = annotation.map(RedisConnection::configKey).orElse(null);
        return getInstance(configKey);
    }

    /**
     * Returns the jedisPool for the given configKey. Returned pools are cached by the configKey. Synchronized in order to prevent creating multiple
     * pools for the same connection.
     * 
     * @param configKey
     * @return
     */
    private synchronized JedisPool getInstance(String configKey) {
        return jedisPoolInstances.computeIfAbsent(configKey, this::createJedisPool);
    }

    private JedisPool createJedisPool(String configKey) {
        try {
            log.info("Creating JedisPool for configKey:[{0}]", configKey);
            ManagedRedisConfig managedRedisConfig = CDI.current().select(ManagedRedisConfig.class, new RedisConnection.Literal(configKey)).get();
            String host = managedRedisConfig.getHost();
            int port = managedRedisConfig.getPort();
            int database = managedRedisConfig.getDatabase();
            log.info("Redis host [{0}], port: [{1}], database: [{2}]", host, port, database);
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(managedRedisConfig.getPoolMaxTotal());
            poolConfig.setMaxIdle(managedRedisConfig.getPoolMaxIdle());
            return new JedisPool(poolConfig, host, port, managedRedisConfig.getTimeout(), managedRedisConfig.getPassword(), database);
        } catch (Exception e) {
            log.error(MessageFormat.format("Exception on initializing JedisPool for configKey:[{0}], [{1}]", configKey, e.getLocalizedMessage()), e);
            return null;
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
