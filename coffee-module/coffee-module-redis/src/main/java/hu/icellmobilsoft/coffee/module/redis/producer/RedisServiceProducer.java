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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.service.RedisService;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import redis.clients.jedis.Jedis;

/**
 * Producer for RedisService
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@Dependent
public class RedisServiceProducer {

    @Inject
    private Logger log;

    /**
     * Produces {@link RedisService} for the redis connection specified by the given configKey.
     *
     * @param injectionPoint
     *            injection point metadata
     * @return Redis service
     * @throws BaseException
     *             if Redis service unavailable or cannot be created
     */
    @Dependent
    @Produces
    @RedisConnection(configKey = "")
    public RedisService getRedisService(InjectionPoint injectionPoint) throws BaseException {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);
        String configKey = annotation.map(RedisConnection::configKey).orElse(null);

        Jedis jedis = CDI.current().select(Jedis.class, new RedisConnection.Literal(configKey)).get();
        if (jedis != null) {
            log.trace("Creating RedisService...");
            RedisService redisService = CDI.current().select(RedisService.class).get();
            redisService.setJedis(jedis);
            return redisService;
        }
        throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED,
                MessageFormat.format("Error occured while creating RedisService for configKey: [{0}], Jedis is null!", configKey));
    }

    /**
     * Closes connection when jedis is disposed.
     *
     * @param redisService
     *            redis service
     */
    public void returnResource(@Disposes @RedisConnection(configKey = "") RedisService redisService) {
        if (redisService != null) {
            log.trace("Closing RedisService...");
            redisService.close();
        }
    }

}
