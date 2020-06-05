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

import java.util.Optional;

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

/**
 * Producer for creating ManagedRedisConfig
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@ApplicationScoped
public class RedisConfigProducer {

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Creates ManagedRedisConfig for the injected configkey
     */
    @Produces
    @Dependent
    @RedisConnection(configKey = "")
    public ManagedRedisConfig getRedisConfig(InjectionPoint injectionPoint) {
        Optional<RedisConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisConnection.class);
        String configKey = annotation.map(RedisConnection::configKey).orElse(null);
        ManagedRedisConfig redisConfig = CDI.current().select(ManagedRedisConfig.class).get();
        redisConfig.setConfigKey(configKey);
        return redisConfig;
    }

}
