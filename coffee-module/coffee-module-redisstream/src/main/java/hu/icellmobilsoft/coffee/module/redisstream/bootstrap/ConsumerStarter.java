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

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamConsumer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.IRedisStreamConsumer;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.IRedisStreamConsumerExecutor;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.RedisStreamConsumerExecutor;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Create Redis stream consumer in separate threads
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
public class ConsumerStarter implements Extension {

    private static final Logger LOG = Logger.getLogger(ConsumerStarter.class);

    private ManagedExecutorService managedExecutorService;

    /**
     * After deployment validation
     * 
     * @param adv
     *            object, not relevant
     */
    void validate(@Observes AfterDeploymentValidation adv, BeanManager beanManager) {
        LOG.debug("Checking consumer for RedisStreamConsumer...");
        // kiszedjuk az osszes olyan osztalyt, ami a IRedisStreamConsumer interfeszt implementalja
        Set<Bean<?>> beans = beanManager.getBeans(IRedisStreamConsumer.class, RedisStreamConsumer.LITERAL);
        LOG.info("Found [{0}] RedisStreamConsumer bean...", beans.size());
        if (!beans.isEmpty()) {
            Instance<StreamGroupConfig> iconfig = beanManager.createInstance().select(StreamGroupConfig.class);
            StreamGroupConfig config = iconfig.get();
            initManagedExecutorService(adv);
            if (managedExecutorService == null) {
                return;
            }
            beans.stream().forEach((bean) -> handleConsumerBean(bean, config));
            iconfig.destroy(config);
        }
    }

    private void initManagedExecutorService(AfterDeploymentValidation adv) {
        try {
            managedExecutorService = (ManagedExecutorService) InitialContext.doLookup("java:jboss/ee/concurrency/executor/default");
        } catch (NamingException e) {
            adv.addDeploymentProblem(new BaseException("Can't get ManagedExecutorService", e));
        }
    }

    private void handleConsumerBean(Bean<?> bean, StreamGroupConfig config) {
        LOG.debug("Handling [{0}] bean", bean.getBeanClass());
        RedisStreamConsumer redisStreamConsumerAnnotation = AnnotationUtil.getAnnotation(bean.getBeanClass(), RedisStreamConsumer.class);
        // a coffee.redisstream beallitasok kellenek, annal a group a kulcs
        config.setConfigKey(redisStreamConsumerAnnotation.group());
        int threads = config.getConsumerThreadsCount().orElse(redisStreamConsumerAnnotation.consumerThreadsCount());
        // Letrehozunk annyi onnalo instance-t (dependent) amennyi a konfigban van megadva
        Instance<RedisStreamConsumerExecutor> consumerExecutor = CDI.current().select(RedisStreamConsumerExecutor.class);
        for (int i = 0; i < threads; i++) {
            IRedisStreamConsumerExecutor executor = consumerExecutor.get();

            // kulon onnalo szalban inditjuk a vegtelen hallgatozo ciklust
            startThread(executor, redisStreamConsumerAnnotation, bean);
        }
    }

    @SuppressWarnings("unchecked")
    private void startThread(IRedisStreamConsumerExecutor executor, RedisStreamConsumer redisStreamConsumerAnnotation, Bean<?> bean) {
        executor.init(redisStreamConsumerAnnotation.configKey(), redisStreamConsumerAnnotation.group(), (Bean<? super IRedisStreamConsumer>) bean);
        LOG.info("Starting Redis stream consumer with executor, class [{0}] for configKey [{1}], group [{2}]...", bean.getBeanClass(),
                redisStreamConsumerAnnotation.configKey(), redisStreamConsumerAnnotation.group());

        managedExecutorService.submit(executor);
        LOG.info("consumer class [{0}] started.", bean.getBeanClass());
    }
}
