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
package hu.icellmobilsoft.coffee.module.redisstream.common;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisStreamConnection;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamProducer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamProducerGroupConfig;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import redis.clients.jedis.Jedis;

/**
 * RedisStreamHandler producer for easy usage
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
@ApplicationScoped
public class RedisStreamHandlerProducer {

    /**
     * Producer for initializing RedisStreamHandler
     * 
     * @param injectionPoint
     *            cdi injection point
     * @return Initialized RedisStreamHandler
     */
    @Produces
    @Dependent
    public RedisStreamHandler produce(InjectionPoint injectionPoint) {
        RedisStreamProducer annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisStreamProducer.class).get();
        Instance<StreamProducerGroupConfig> configInstance =  CDI.current().select(StreamProducerGroupConfig.class, new RedisStreamProducer.Literal(annotation.configKey(), annotation.group()));
        StreamProducerGroupConfig config = configInstance.get();
        CDI<Object> cdi = CDI.current();
        RedisStreamHandler redisStreamHandler = cdi.select(RedisStreamHandler.class).get();
        Instance<Jedis> jedisInstance = cdi.select(Jedis.class, new RedisStreamConnection.Literal(annotation.configKey(), config.getPool(), config.getConnectionKeyReference().orElse(annotation.configKey())));
        redisStreamHandler.init(jedisInstance, annotation.group());
        return redisStreamHandler;
    }
}
