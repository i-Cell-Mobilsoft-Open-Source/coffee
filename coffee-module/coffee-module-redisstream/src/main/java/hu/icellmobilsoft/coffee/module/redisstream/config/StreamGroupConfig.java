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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamConsumer;

/**
 * Redis consumer stream group configuration implementation. Key-value par has standard format like yaml file:
 *
 * <pre>
 * coffee:
 *    redis:
 *        auth:
 *            host: hubphq-icon-sandbox-d001.icellmobilsoft.hu
 *            port: 6380
 *            password: authpw
 *            database: 1
 *            pool:
 *                default:
 *                    maxtotal: 64
 *                    maxidle: 16
 *                custom1:
 *                    maxtotal: 128
 *                    maxidle: 32
 *                custom2:
 *                    maxtotal: 256
 *                    maxidle: 64
 *    redisstream:
 *       sampleGroup: #(1)
 *           stream:
 *               read:
 *                   timeoutmillis: 60000 #default: 60000 (2)
 *               connection:
 *                   key: auth # connection referencia
 *           producer:
 *               maxlen: 10000 #default none (3)
 *               ttl: 300000 #millisec, default none (4)
 *               pool: custom1 # default default - pool referencia
 *           consumer:
 *               threadsCount: 2 #default: 1 (5)
 *               retryCount: 2 #default: 1 (6)
 *               pool: custom2 # default default - pool referencia
 *
 * </pre>
 *
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
/**
 * Redis stream group configuration implementation. Key-value par has standard format like yaml file:
 *
 * @author imre.scheffer
 * @since 1.3.0
 */
@Dependent
public class StreamGroupConfig implements IStreamGroupConfig {

    /**
     * Config delimiter
     */
    public static final String KEY_DELIMITER = ".";
    private static final String DEFAULT = "default";


    @Inject
    protected Config config;

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
     * Prefix for stream.read.timeoutmillis config
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

    // stream common
    @Override
    public Long getReadTimeoutMillis() {
        return config.getOptionalValue(joinKey(STREAM_READ_TIMEOUTMILLIS), Long.class).orElse(Duration.ofMinutes(1).toMillis());
    }

    @Override
    public Optional<String> getConnectionKey() {
        return config.getOptionalValue(joinKey(CONNECTION), String.class);
    }

    // producer
    @Override
    public Optional<Long> getProducerMaxLen()  {
        return config.getOptionalValue(joinKey(PRODUCER_MAXLEN), Long.class);
    }

    @Override
    public Optional<Long> getProducerTTL() {
        return config.getOptionalValue(joinKey(PRODUCER_TTL), Long.class);
    }


    @Override
    public String getProducerPool() {
        return config.getOptionalValue(joinKey(PRODUCER_POOL), String.class).orElse(DEFAULT);
    }

    // consumer
    @Override
    public Optional<Integer> getConsumerThreadsCount() {
        return config.getOptionalValue(joinKey(CONSUMER_THREADS_COUNT), Integer.class);
    }

    @Override
    public Optional<Integer> getRetryCount() throws BaseException {
        return config.getOptionalValue(joinKey(RETRY_COUNT), Integer.class);
    }

    @Override
    public String getConsumerPool() {
        return config.getOptionalValue(joinKey(CONSUMER_POOL), String.class).orElse(DEFAULT);
    }

    protected String joinKey(String key) {
        return String.join(KEY_DELIMITER, REDISSTREAM_PREFIX, getConfigKey(), key);
    }

    /**
     * Default true {@link #isEnabled()}}
     */
    @Override
    public boolean isEnabled() {
        return config.getOptionalValue(joinKey(ENABLED), Boolean.class).orElse(true);
    }

}
