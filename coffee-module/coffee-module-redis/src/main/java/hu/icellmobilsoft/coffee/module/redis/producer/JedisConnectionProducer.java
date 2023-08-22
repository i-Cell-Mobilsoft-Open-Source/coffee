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
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Tag;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.config.RedisConfig;
import hu.icellmobilsoft.coffee.module.redis.metrics.JedisMetricsConstants;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Producer for creating jedis resouce
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@ApplicationScoped
public class JedisConnectionProducer {

    @Inject
    private Logger log;

    @Inject
    private MetricRegistry metricRegistry;

    /**
     * Creates or returns {@link Jedis} resource for the given configKey.
     * 
     * @param injectionPoint
     *            injection metadata
     * @return {@code Jedis}
     * @throws BaseException
     *             if Jedis unavailable or cannot be created
     */
    @Produces
    @Dependent
    @RedisConnection(configKey = "", poolConfigKey = "")
    public Jedis getJedis(InjectionPoint injectionPoint) throws BaseException {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);

        String configKey = annotation.map(RedisConnection::configKey).orElse(null);
        String poolConfigKey = annotation.map(RedisConnection::poolConfigKey).orElse(RedisConfig.POOL_CONFIG_KEY_DEFAULT_VALUE);

        JedisPool jedisPool;
        Instance<JedisPool> jedisPoolInstance;
        jedisPoolInstance = CDI.current().select(JedisPool.class, new RedisConnection.Literal(configKey, poolConfigKey));
        jedisPool = jedisPoolInstance.get();

        if (jedisPool != null) {
            try {
                addMetric(configKey, poolConfigKey, jedisPool);

                return jedisPool.getResource();
            } catch (JedisConnectionException ex) {
                String msg = MessageFormat
                        .format("Problems trying to get the Redis connection for the configKey:[{0}], poolConfigKey:[{1}]", configKey, poolConfigKey);
                log.error(msg, ex);
                throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, ex);
            } finally {
                jedisPoolInstance.destroy(jedisPool);
            }
        }
        String msg = MessageFormat.format(
                "Could not create Redis connection for the configKey:[{0}], poolConfigKey:[{1}]! Jedis pool is null",
                configKey,
                poolConfigKey);
        throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg);
    }

    private void addMetric(String configKey, String poolConfigKey, JedisPool jedisPool) {
        // config
        Tag configKeyTag = new Tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_CONFIG_KEY, configKey);
        Tag poolConfigKeyTag = new Tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_POOL_CONFIG_KEY, poolConfigKey);

        Metadata metadataActive = Metadata.builder()
                .withName(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_ACTIVE)
                .withDescription(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_ACTIVE_DESCRIPTION)
                .withType(MetricType.GAUGE)
                .build();
        metricRegistry.gauge(metadataActive, jedisPool::getNumActive, configKeyTag, poolConfigKeyTag);

        Metadata metadataIdle = Metadata.builder()
                .withName(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_IDLE)
                .withDescription(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_IDLE_DESCRIPTION)
                .withType(MetricType.GAUGE)
                .build();
        metricRegistry.gauge(metadataIdle, jedisPool::getNumIdle, configKeyTag, poolConfigKeyTag);
    }

    /**
     * Close connection when jedis is disposed
     * 
     * @param jedis
     *            {@link Jedis} to close
     */
    public void returnResource(@Disposes @RedisConnection(configKey = "", poolConfigKey = "") Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
