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
 *         stream:
 *             read:
 *                 timeoutmillis: 60000 #default: 60000
 *         producer:
 *             maxlen: 10000 #default none
 *             ttl: 300000 #millisec, default none
 *         consumer:
 *             threadsCount: 2 #default: 1
 *             retryCount: 2 #default: 1
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

    @Override
    public boolean isEnabled() {
        return config.getOptionalValue(joinKey(ENABLED), Boolean.class).orElse(true);
    }

    @Override
    public Optional<Long> getProducerMaxLen() {
        return config.getOptionalValue(super.joinKey(PRODUCER_MAXLEN), Long.class);
    }

    @Override
    public Optional<Long> getProducerTTL() {
        return super.config.getOptionalValue(super.joinKey(PRODUCER_TTL), Long.class);
    }
}
