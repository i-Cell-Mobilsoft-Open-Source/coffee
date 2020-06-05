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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
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
    @ThisLogger
    private AppLogger log;

    /**
     * Creates or gets jedis resource for the given configKey
     */
    @Produces
    @Dependent
    @RedisConnection(configKey = "")
    public Jedis getJedis(InjectionPoint injectionPoint) throws BaseException {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);
        String configKey = annotation.map(RedisConnection::configKey).orElse(null);

        JedisPool jedisPool = CDI.current().select(JedisPool.class, new RedisConnection.Literal(configKey)).get();
        if (jedisPool != null) {
            try {
                return jedisPool.getResource();
            } catch (JedisConnectionException ex) {
                String msg = MessageFormat.format("Problems trying to get the Redis connection for the configKey:[{0}]", configKey);
                log.error(msg, ex);
                throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, ex);
            }
        }
        String msg = MessageFormat.format("Could not create Redis connection for the configKey:[{0}]! Jedis pool is null", configKey);
        throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg);
    }

    /**
     * Close connection when jedis is disposed
     */
    public void returnResource(@Disposes @RedisConnection(configKey = "") Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
