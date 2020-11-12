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
package hu.icellmobilsoft.coffee.module.redisstream.bootstrap;

import java.util.Set;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamConsumer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.IRedisStreamConsumer;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Create Redis stream consumer in separate threads
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@ApplicationScoped
public class ConsumerStarter {

    @Inject
    private Logger log;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Inject
    private StreamGroupConfig config;

    @Inject
    private BeanManager beanManager;

    /**
     * After initialized all ApplicationScoped beans
     * 
     * @param init
     *            object, not relevant
     */
    public void begin(@Observes @Initialized(ApplicationScoped.class) Object init) {
        // kiszedjuk az osszes olyan osztalyt, ami a IRedisStreamConsumer interfeszt implementalja
        Set<Bean<?>> beans = beanManager.getBeans(IRedisStreamConsumer.class, RedisStreamConsumer.LITERAL);
        beans.stream().forEach(this::handleConsumerBean);
    }

    private void handleConsumerBean(Bean<?> bean) {
        log.info("Found consumer: [{0}]", bean.getBeanClass());
        RedisStreamConsumer redisStreamConsumerAnnotation = AnnotationUtil.getAnnotation(bean.getBeanClass(), RedisStreamConsumer.class);
        // a coffee.redisstream beallitasok kellenek, annal a group a kulcs
        config.setConfigKey(redisStreamConsumerAnnotation.group());
        int threads = config.getConsumerThreadsCount().orElse(redisStreamConsumerAnnotation.consumerThreadsCount());
        // Letrehozunk annyi onnalo instance-t (dependent) amennyi a konfigban van megadva
        for (int i = 0; i < threads; i++) {
            IRedisStreamConsumer consumer = (IRedisStreamConsumer) bean.create(beanManager.createCreationalContext(null));
            // kulon onnalo szalban inditjuk a vegtelen hallgatozo ciklust
            startThread(consumer, redisStreamConsumerAnnotation);
        }
    }

    private void startThread(IRedisStreamConsumer consumer, RedisStreamConsumer redisStreamConsumerAnnotation) {
        consumer.init(redisStreamConsumerAnnotation.configKey(), redisStreamConsumerAnnotation.group());
        log.info("Starting Redis stream consumer class [{0}] for configKey [{1}], group [{2}]...", consumer,
                redisStreamConsumerAnnotation.configKey(), redisStreamConsumerAnnotation.group());
        managedExecutorService.submit(consumer);
        log.info("consumer class [{0}] started.", consumer);
    }
}
