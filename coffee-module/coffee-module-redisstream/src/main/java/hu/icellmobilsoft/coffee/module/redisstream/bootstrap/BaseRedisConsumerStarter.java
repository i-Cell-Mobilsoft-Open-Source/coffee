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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
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

    protected static final String MANAGED_EXECUTOR_SERVICE_CORE_THREADS = "MANAGED_EXECUTOR_SERVICE_CORE_THREADS";
    private static final int DEFAULT_MAX_THREAD_COUNT = 16;
    public static final int SAFETY_BUFFER = 10;
    public static final String POOL_DEFAULT_MAX_TOTAL = "pool.default.maxtotal";
    private static final String REDIS_PREFIX = "coffee.redis";

    @Inject
    private Logger log;

    @Resource
    private ManagedExecutorService managedExecutorService;

    @Inject
    private StreamGroupConfig streamGroupConfig;

    @Inject
    private Config config;

    @Inject
    private BeanManager beanManager;

    @Inject
    private ManagedRedisConfig managedRedisConfig;

    @Inject
    private Event<RedisStreamMetricEventMessage> metricsEvent;
    
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
        validateConfig();

        // get every classes implementing IRedisStreamConsumer
        Set<Bean<?>> beans = beanManager.getBeans(IRedisStreamBaseConsumer.class, RedisStreamConsumer.LITERAL);
        beans.forEach(this::handleConsumerBean);

        log.info("Redis consumers started");
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
        // We need the settings for coffee.redisstream, where group is the key.
        streamGroupConfig.setConfigKey(redisStreamConsumerAnnotation.group());
        int threads = streamGroupConfig.getConsumerThreadsCount().orElse(redisStreamConsumerAnnotation.consumerThreadsCount());
        // We create as many independent instances (dependent scoped) as specified in the configuration.
        Instance<RedisStreamConsumerExecutor> consumerExecutor = CDI.current().select(RedisStreamConsumerExecutor.class);
        for (int i = 0; i < threads; i++) {
            IRedisStreamConsumerExecutor executor = consumerExecutor.get();

            // We start the infinite listener loop in a separate independent thread.
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
        executor.init(
                redisStreamConsumerAnnotation.configKey(),
                redisStreamConsumerAnnotation.group(),
                (Bean<? super IRedisStreamBaseConsumer>) bean);
        log.info(
                "Starting Redis stream consumer with executor, class [{0}] for configKey [{1}], group [{2}]...",
                bean.getBeanClass(),
                redisStreamConsumerAnnotation.configKey(),
                redisStreamConsumerAnnotation.group());

        managedExecutorService.submit(executor);
        log.info("consumer class [{0}] started.", bean.getBeanClass());
    }

    private void validateConfig() {
        Integer maxThreadCount = getMaxThreadCount();
        Collection<Bean<?>> consumerBeans = beanManager.getBeans(IRedisStreamBaseConsumer.class, RedisStreamConsumer.LITERAL);
        Map<String, Integer> consumerThreadCountByStream = getConsumerThreadCountByStream(consumerBeans);

        registerMetrics(consumerThreadCountByStream);

        validateThreadCount(consumerThreadCountByStream, maxThreadCount);

        validateConsumerPoolSize(consumerBeans);
    }

    private void validateThreadCount(Map<String, Integer> consumerThreadCountByStream, Integer maxThreadCount) {
        int redisConsumerThreadCount = consumerThreadCountByStream.values().stream().reduce(0, Integer::sum);
        log.info("Starting redis consumers using [{0}] threads of max thread count [{1}]", redisConsumerThreadCount, maxThreadCount);

        if (maxThreadCount < redisConsumerThreadCount + SAFETY_BUFFER) {
            throw new IllegalStateException(
                    MessageFormat.format(
                            "Max thread count [{0}] is less than redis consumer thread count [{1}] + safety buffer [{2}]",
                            maxThreadCount,
                            redisConsumerThreadCount,
                            SAFETY_BUFFER));
        }
    }

    private void validateConsumerPoolSize(Collection<Bean<?>> consumerBeans) {
        Map<String, Integer> consumerPoolSizeByConfigKey = getConsumerPoolSizeByConfigKey(consumerBeans);
        for (Map.Entry<String, Integer> entry : consumerPoolSizeByConfigKey.entrySet()) {
            int poolMaxSize = getRedisConsumerPoolSize(entry.getKey());
            log.info("Redis Consumer Pool [{0}] Max Size: [{1}], currently used: [{2}]", entry.getKey(), poolMaxSize, entry.getValue());

            if (poolMaxSize < entry.getValue() + SAFETY_BUFFER) {
                throw new IllegalStateException(
                        MessageFormat.format(
                                "Max pool size in config [{0}], [{1}] is less than redis consumer pool size [{2}] + safety buffer [{3}]",
                                entry.getKey(),
                                poolMaxSize,
                                entry.getValue(),
                                SAFETY_BUFFER));
            }
        }
    }

    private void registerMetrics(Map<String, Integer> consumerThreadCountByStream) {
        consumerThreadCountByStream.forEach(this::registerMetrics);
    }

    private void registerMetrics(String group, Integer count) {
        RedisStreamMetricEventMessage message = new RedisStreamMetricEventMessage();
        message.setCount(count);
        message.setGroup(group);
        metricsEvent.fireAsync(message);
    }

    private Map<String, Integer> getConsumerThreadCountByStream(Collection<Bean<?>> consumerBeans) {
        return consumerBeans.stream()
                .map(this::getRedisStreamConsumerAnnotation)
                .collect(Collectors.toMap(RedisStreamConsumer::group, this::getRedisConsumerThreadCount));
    }

    private Map<String, Integer> getConsumerPoolSizeByConfigKey(Collection<Bean<?>> consumerBeans) {
        return consumerBeans.stream()
                .map(this::getRedisStreamConsumerAnnotation)
                .collect(Collectors.toMap(RedisStreamConsumer::configKey, this::getRedisConsumerThreadCount, Integer::sum));
    }

    private Integer getMaxThreadCount() {
        return Optional.ofNullable(System.getenv(MANAGED_EXECUTOR_SERVICE_CORE_THREADS)).map(Integer::valueOf).orElse(DEFAULT_MAX_THREAD_COUNT);
    }

    private RedisStreamConsumer getRedisStreamConsumerAnnotation(Bean<?> bean) {
        return AnnotationUtil.getAnnotation(bean.getBeanClass(), RedisStreamConsumer.class);
    }

    private int getRedisConsumerThreadCount(RedisStreamConsumer redisStreamConsumerAnnotation) {
        streamGroupConfig.setConfigKey(redisStreamConsumerAnnotation.group());
        int count = streamGroupConfig.getConsumerThreadsCount().orElse(redisStreamConsumerAnnotation.consumerThreadsCount());
        log.debug("[{0}] redis stream consumer thread count is [{1}]", redisStreamConsumerAnnotation.group(), count);
        return count;
    }

    private Integer getRedisConsumerPoolSize(String configKey) {
        String poolMaxTotalConfigKey = String.join(StreamGroupConfig.KEY_DELIMITER, REDIS_PREFIX, configKey, POOL_DEFAULT_MAX_TOTAL);
        return config.getOptionalValue(poolMaxTotalConfigKey, Integer.class).orElse(managedRedisConfig.getPoolMaxTotal());
    }

}
