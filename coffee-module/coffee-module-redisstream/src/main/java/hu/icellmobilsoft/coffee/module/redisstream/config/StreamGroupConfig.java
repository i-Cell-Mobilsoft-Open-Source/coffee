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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

/**
 * Redis stream group configuration implementation. Key-value par has standard format like yaml file:
 * 
 * <pre>
 * coffee:
 *   redisstream:
 *     enabled: true
 *     sampleGroup:
 *       stream:
 *         maxlen: 10
 *         read:
 *           timeoutmillis: 60000
 *       consumer:
 *         threadsCount: 2
 *         manualAck: true
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
     * Default 1 minute {@link #getStreamReadTimeoutMillis()}}
     */
    public static final String STREAM_READ_TIMEOUTMILLIS = "stream.read.timeoutmillis";

    /**
     * Default 1 thread {@link #getConsumerThreadsCount()}}
     */
    public static final String CONSUMER_THREADS_COUNT = "consumer.threadsCount";

    /**
     * Default 1 retry count {@link #getRetryCount()}}
     */
    public static final String RETRY_COUNT = "consumer.retryCount";

    /**
     * Default false {@link #isManualAck()}
     */
    public static final String MANUAL_ACK = "consumer.manualAck";

    /**
     * Default true {@link #isEnabled()}}
     */
    public static final String ENABLED = "enabled";

    @Inject
    private Config config;

    private String configKey;

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
        return config.getOptionalValue(joinKey(ENABLED), Boolean.class).orElse(true);
    }

    @Override
    public boolean isManualAck() {
        return config.getOptionalValue(joinKey(MANUAL_ACK), Boolean.class).orElse(false);
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
}
