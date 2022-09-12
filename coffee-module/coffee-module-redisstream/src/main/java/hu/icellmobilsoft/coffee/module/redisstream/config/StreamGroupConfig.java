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
package hu.icellmobilsoft.coffee.module.redisstream.config;

import java.time.Duration;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import static hu.icellmobilsoft.coffee.module.redis.config.RedisConfig.POOL_CONFIG_KEY_DEFAULT_VALUE;

/**
 * Redis stream group configuration implementation. Key-value par has standard format like yaml file:
 *
 * <pre>
 * coffee:
 *    redis:
 *      auth:
 *        host: sample-sandbox.icellmobilsoft.hu
 *        port: 6380
 *        password: *****
 *        database: 1
 *        pool:
 *          default:
 *              maxtotal: 64
 *              maxidle: 16
 *          custom1:
 *              maxtotal: 128
 *              maxidle: 32
 *          custom2:
 *              maxtotal: 256
 *              maxidle: 64
 *    redisstream:
 *       enabled: true
 *       sampleGroup:
 *           stream:
 *               read:
 *                   timeoutmillis: 60000 #default: 60000
 *               connection:
 *                   key: auth # connection reference
 *           producer:
 *               maxlen: 10000 #default none
 *               ttl: 300000 #millisec, default none
 *               pool: custom1 # default - coffee.redis.*.pool config reference
 *           consumer:
 *               threadsCount: 2 #default: 1
 *               retryCount: 2 #default: 1
 *               pool: custom2 # default - coffee.redis.*.pool config reference
 *
 * </pre>
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@Dependent
public class StreamGroupConfig implements IStreamGroupConfig {

    /**
     * Config delimiter
     */
    public static final String KEY_DELIMITER = ".";

    @Inject
    private Config config;

    private String configKey;

    /**
     * Prefix for all configs
     */
    public static final String REDISSTREAM_PREFIX = "coffee.redisstream";

    /**
     * Default is no limit. See {@link #getProducerMaxLen()}}
     */
    public static final String PRODUCER_MAXLEN = "producer.maxlen";

    /**
     * Default no ttl, value in millisecond. See {@link #getProducerTTL()}}
     */
    public static final String PRODUCER_TTL = "producer.ttl";
    /**
     * Defines the redis connection pool configuration key to be used.
     */
    public static final String PRODUCER_POOL = "producer.pool";
    /**
     * Default 1 minute {@link #getStreamReadTimeoutMillis()}}
     */
    public static final String STREAM_READ_TIMEOUTMILLIS = "stream.read.timeoutmillis";

    /**
     * Prefix for stream.connection.key config
     */
    public static final String CONNECTION = "stream.connection.key";

    /**
     * Default 1 thread {@link #getConsumerThreadsCount()}}
     */
    public static final String CONSUMER_THREADS_COUNT = "consumer.threadsCount";

    /**
     * Default 1 retry count {@link #getRetryCount()}}
     */
    public static final String RETRY_COUNT = "consumer.retryCount";

    /**
     * Defines the redis connection pool configuration key to be used.
     */
    public static final String CONSUMER_POOL = "consumer.pool";

    /**
     * Default true {@link #isEnabled()}}
     */
    public static final String ENABLED = "enabled";

    @Override
    public Optional<Long> getProducerMaxLen() {
        return config.getOptionalValue(joinKey(PRODUCER_MAXLEN), Long.class);
    }

    @Override
    public Optional<Long> getProducerTTL() {
        return config.getOptionalValue(joinKey(PRODUCER_TTL), Long.class);
    }

    @Override
    public Long getStreamReadTimeoutMillis() {
        return config.getOptionalValue(joinKey(STREAM_READ_TIMEOUTMILLIS), Long.class).orElse(Duration.ofMinutes(1).toMillis());
    }

    @Override
    public Optional<Integer> getConsumerThreadsCount() {
        return config.getOptionalValue(joinKey(CONSUMER_THREADS_COUNT), Integer.class);
    }

    @Override
    public Optional<Integer> getRetryCount() {
        return config.getOptionalValue(joinKey(RETRY_COUNT), Integer.class);
    }

    @Override
    public boolean isEnabled() {
        return config.getOptionalValue(String.join(KEY_DELIMITER, REDISSTREAM_PREFIX, ENABLED), Boolean.class).orElse(true);
    }

    /**
     * Getter for the field {@code configKey}.
     *
     * @return configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Setter for the field {@code configKey}.
     *
     * @param configKey
     *            configKey
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private String joinKey(String key) {
        return String.join(KEY_DELIMITER, REDISSTREAM_PREFIX, getConfigKey(), key);
    }

    @Override
    public String getConsumerPool() {
        return config.getOptionalValue(joinKey(CONSUMER_POOL), String.class).orElse(POOL_CONFIG_KEY_DEFAULT_VALUE);
    }

    @Override
    public String getProducerPool() {
        return config.getOptionalValue(joinKey(PRODUCER_POOL), String.class).orElse(POOL_CONFIG_KEY_DEFAULT_VALUE);
    }

    @Override
    public String getConnectionKey() {
        return config.getValue(joinKey(CONNECTION), String.class);
    }

}
