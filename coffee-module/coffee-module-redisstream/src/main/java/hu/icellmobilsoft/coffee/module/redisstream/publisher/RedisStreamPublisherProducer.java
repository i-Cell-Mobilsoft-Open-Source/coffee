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
package hu.icellmobilsoft.coffee.module.redisstream.publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamProducer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * {@link RedisStreamPublisher} producer for easy usage
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
@ApplicationScoped
public class RedisStreamPublisherProducer {

    @Inject
    private StreamGroupConfig config;

    /**
     * Producer for initializing RedisStreamHandler
     * 
     * @param injectionPoint
     *            cdi injection point
     * @return Initialized RedisStreamHandler
     */
    @Produces
    @Dependent
    @RedisStreamProducer(configKey = "", group = "")
    public RedisStreamPublisher produce(InjectionPoint injectionPoint) {
        RedisStreamProducer annotation = AnnotationUtil.getAnnotation(injectionPoint, RedisStreamProducer.class).get();

        CDI<Object> cdi = CDI.current();
        RedisStreamPublisher redisStreamPublisher = cdi.select(RedisStreamPublisher.class).get();
        config.setConfigKey(annotation.group());
        String configKey = StringUtils.isEmpty(config.getConnectionKey()) ? annotation.configKey() : config.getConnectionKey();
        Instance<RedisManager> redisManagerInstance = cdi.select(RedisManager.class, new RedisConnection.Literal(configKey, config.getProducerPool()));
        RedisManager redisManager = redisManagerInstance.get();
        redisStreamPublisher.init(redisManager, annotation.group());
        redisManagerInstance.destroy(redisManager);
        return redisStreamPublisher;
    }
}
