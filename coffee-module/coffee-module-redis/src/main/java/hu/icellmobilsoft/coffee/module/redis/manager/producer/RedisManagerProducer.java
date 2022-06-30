/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.manager.producer;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * 
 * Producer for RedisManagerProducer
 * 
 * @author czenczl
 * @since 1.7.0
 *
 */
@ApplicationScoped
public class RedisManagerProducer {

    @Inject
    private Logger log;

    /**
     * Produces {@link RedisManager} with the redis connection configKey.
     *
     * @param injectionPoint
     *            injection point metadata
     * @return Redis service
     * @throws BaseException
     *             if Redis manager unavailable or cannot be created
     */
    @Dependent
    @Produces
    @RedisConnection(configKey = "", poolConfigKey = "")
    public RedisManager getRedisService(InjectionPoint injectionPoint) throws BaseException {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);

        String configKey = annotation.map(RedisConnection::configKey).orElse(null);
        String poolConfigKey = annotation.map(RedisConnection::poolConfigKey).orElse("default");

        if (StringUtils.isBlank(configKey)) {
            throw new IllegalStateException("configKey is required for redis");
        }

        log.trace("Creating RedisManager with configKey: [{0}]", configKey);
        RedisManager redisManager = CDI.current().select(RedisManager.class).get();
        redisManager.setConfigKey(configKey);
        redisManager.setPoolConfigKey(poolConfigKey);
        return redisManager;
    }

    /**
     * Destroys {@link RedisManager} instances
     *
     * @param redisManager
     *            redis manager
     */
    public void returnResource(@Disposes @RedisConnection(configKey = "", poolConfigKey="") RedisManager redisManager) {
        if (Objects.nonNull(redisManager)) {
            CDI.current().destroy(redisManager);
        }
    }
}
