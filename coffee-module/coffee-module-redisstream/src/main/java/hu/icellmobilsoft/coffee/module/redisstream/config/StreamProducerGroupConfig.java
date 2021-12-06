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

import java.util.Optional;

import javax.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Redis stream group configuration implementation. Key-value par has standard format like yaml file:
 * 
 * <pre>
 * coffee:
 * redisstream:
 *     sampleGroup: #
 *        stream:
 *            read:
 *                timeoutmillis: 60000 #default: 60000 (2)
 *            connection:
 *                key: auth # connection referencia
 *        producer:
 *            maxlen: 10000 #default none (3)
 *            ttl: 300000 #millisec, default none (4)
 *            pool: custom1 # default default - pool referencia
 *        consumer:
 *            threadsCount: 2 #default: 1 (5)
 *            retryCount: 2 #default: 1 (6)
 *            pool: custom2 # default default - pool referencia
 * </pre>
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@Dependent
public class StreamProducerGroupConfig extends StreamGroupConfig implements IStreamProducerGroupConfig {

    /**
     * Default is no limit. See {@link #getProducerMaxLen()}}
     */
    public static final String PRODUCER_MAXLEN = "producer.maxlen";

    /**
     * Default no ttl, value in millisecond. See {@link #getProducerTTL()}}
     */
    public static final String PRODUCER_TTL = "producer.ttl";


    /**
     * Default true {@link #isEnabled()}}
     */
    public static final String ENABLED = "enabled";
    private static final String PRODUCER_POOL = "producer.pool";


    /**
     * Is stream enabled.
     * @return isEnabled value.
     */
    @Override
    public boolean isEnabled() {
        return config.getOptionalValue(joinKey(ENABLED), Boolean.class).orElse(true);
    }

    /**
     * Max elements in stream, oldest will be removed. See https://redis.io/commands/xadd MAXLEN parameter. <br>
     * <br>
     * This parameter has higher priority than {@link #getProducerTTL()}, if both setted, this parameter is applied and {@link #getProducerTTL()}
     * ignored.
     *
     * @return Max elements in stream
     */
    @Override
    public Optional<Long> getProducerMaxLen() {
        return config.getOptionalValue(super.joinKey(PRODUCER_MAXLEN), Long.class);
    }
    /**
     * Millisec TTL. When a new entry is produced, all old entries are deleted which have identifier time older than (sysdate - millisecond). See
     * https://redis.io/commands/xadd MINID parameter. <br>
     * <br>
     * The parameter {@link #getProducerMaxLen()} has higher priority, if both setted, this parameter is ignored.
     *
     * @return millisec ttl
     */
    @Override
    public Optional<Long> getProducerTTL() {
        return super.config.getOptionalValue(super.joinKey(PRODUCER_TTL), Long.class);
    }
    /**
     * Configuration parameter key for defining producer-stream settings.
     * @return a string.
     */
    @Override
    public String getStreamTypeKey() {
        return PRODUCER_POOL;
    }
}
