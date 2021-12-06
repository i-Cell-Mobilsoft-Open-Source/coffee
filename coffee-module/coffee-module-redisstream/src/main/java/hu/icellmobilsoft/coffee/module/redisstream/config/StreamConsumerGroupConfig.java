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

    private static final String CONSUMER_POOL = "consumer.pool";

    /**
     * How many threads start to listening on stream group. This value override {@link RedisStreamConsumer#consumerThreadsCount()}
     *
     * @return threads count
     */
    @Override
    public Optional<Integer> getConsumerThreadsCount() {
        return config.getOptionalValue(joinKey(CONSUMER_THREADS_COUNT), Integer.class);
    }

    /**
     * How many times to try again if an exception occurs in the consumer process. This value override {@link RedisStreamConsumer#retryCount()}
     *
     * @return retry process count
     */
    @Override
    public Optional<Integer> getRetryCount() {
        return config.getOptionalValue(joinKey(RETRY_COUNT), Integer.class);
    }

    /**
     * Configuration parameter key for defining consumer-stream settings.
     * 
     * @return streamType.
     */
    @Override
    public String getStreamTypeKey() {
        return CONSUMER_POOL;
    }
}
