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


/**
 * Redis consumer stream group configuration implementation. Key-value par has standard format like yaml file:
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
public class StreamConsumerGroupConfig extends StreamGroupConfig implements IStreamConsumerGroupConfig {


    /**
     * Default 1 thread {@link #getConsumerThreadsCount()}}
     */
    public static final String CONSUMER_THREADS_COUNT = "consumer.threadsCount";

    /**
     * Default 1 retry count {@link #getRetryCount()}}
     */
    public static final String RETRY_COUNT = "consumer.retryCount";

    /**
     * Default 1 minute {@link #getStreamReadTimeoutMillis()}}
     */
    public static final String STREAM_READ_TIMEOUTMILLIS = "stream.read.timeoutmillis";



    @Override
    public Optional<Integer> getConsumerThreadsCount() {
        return config.getOptionalValue(joinKey(CONSUMER_THREADS_COUNT), Integer.class);
    }

    @Override
    public Optional<Integer> getRetryCount() {
        return config.getOptionalValue(joinKey(RETRY_COUNT), Integer.class);
    }

    @Override
    public Long getStreamReadTimeoutMillis() {
        return config.getOptionalValue(joinKey(STREAM_READ_TIMEOUTMILLIS), Long.class).orElse(Duration.ofMinutes(1).toMillis());
    }




}
