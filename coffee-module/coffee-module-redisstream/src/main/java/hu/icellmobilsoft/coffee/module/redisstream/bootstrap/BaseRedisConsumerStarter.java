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

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamConsumer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.IRedisStreamBaseConsumer;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.IRedisStreamConsumerExecutor;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.RedisStreamConsumerExecutor;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Base Redis consumer starter algorithm. Must be activated on implemented project. This logic can be started with
 * <ul>
 * <li>CDI:
 * 
 * <pre>
 * import javax.enterprise.context.ApplicationScoped;
 * import javax.enterprise.context.Initialized;
 * import javax.enterprise.event.Observes;
 * 
 * &#64;ApplicationScoped
 * public class ConsumerStarterCdi extends BaseRedisConsumerStarter {
 * 
 *     public void begin(@Observes @Initialized(ApplicationScoped.class) Object init) {
 *         start();
 *     }
 * }
 * </pre>
 * 
 * </li>
 * <li>EJB:
 * 
 * <pre>
 * import javax.annotation.PostConstruct;
 * import javax.ejb.Singleton;
 * import javax.ejb.Startup;
 * 
 * &#64;Startup
 * &#64;Singleton
 * public class ConsumerStarterEjb extends BaseRedisConsumerStarter {
 * 
 *     &#64;PostConstruct
 *     public void init() {
 *         start();
 *     }
 * }
 * </pre>
 * 
 * </li>
 * <li>CDI Extension need little changes because cant use Inject annotations and JNDI handling is limited</li>
 * <li>... or other way</li>
 * 
 * </ul>
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public class BaseRedisConsumerStarter {

    @Inject
    private Logger log;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Inject
    private StreamGroupConfig config;

    @Inject
    private BeanManager beanManager;

    /**
     * Default constructor, constructs a new object.
     */
    public BaseRedisConsumerStarter() {
        super();
    }

    /**
     * Start Redis consumers in separate long running managed threads
     */
    public void start() {
        // kiszedjuk az osszes olyan osztalyt, ami a IRedisStreamConsumer interfeszt implementalja
        Set<Bean<?>> beans = beanManager.getBeans(IRedisStreamBaseConsumer.class, RedisStreamConsumer.LITERAL);
        beans.forEach(this::handleConsumerBean);
    }

    /**
     * Starts multiple redis stream consumer threads
     * 
     * @param bean
     *            the redis stream consumer callback bean
     */
    protected void handleConsumerBean(Bean<?> bean) {
        log.info("Found consumer: [{0}]", bean.getBeanClass());
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

    /**
     * Starts the redis stream consumer thread
     * 
     * @param executor
     *            the consumer executor
     * @param redisStreamConsumerAnnotation
     *            the redis stream consumer annotation with configuration data
     * @param bean
     *            the redis stream consumer callback bean
     */
    @SuppressWarnings("unchecked")
    protected void startThread(IRedisStreamConsumerExecutor executor, RedisStreamConsumer redisStreamConsumerAnnotation, Bean<?> bean) {
        executor.init(redisStreamConsumerAnnotation.configKey(), redisStreamConsumerAnnotation.group(),
                (Bean<? super IRedisStreamBaseConsumer>) bean);
        log.info("Starting Redis stream consumer with executor, class [{0}] for configKey [{1}], group [{2}]...", bean.getBeanClass(),
                redisStreamConsumerAnnotation.configKey(), redisStreamConsumerAnnotation.group());

        managedExecutorService.submit(executor);
        log.info("consumer class [{0}] started.", bean.getBeanClass());
    }
}
