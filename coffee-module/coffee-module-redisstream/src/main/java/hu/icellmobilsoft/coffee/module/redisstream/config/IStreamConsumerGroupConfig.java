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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamConsumer;

/**
 * Redis consumer stream group configuration interface
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public interface IStreamConsumerGroupConfig {


    /**
     * How many threads start to listening on stream group. This value override {@link RedisStreamConsumer#consumerThreadsCount()}
     * 
     * @return threads count
     * @throws BaseException
     *             Exception on read properties
     */
    Optional<Integer> getConsumerThreadsCount() throws BaseException;

    /**
     * How many times to try again if an exception occurs in the consumer process. This value override {@link RedisStreamConsumer#retryCount()}
     * 
     * @return retry process count
     * @throws BaseException
     *             Exception on read properties
     */
    Optional<Integer> getRetryCount() throws BaseException;

    /**
     * See https://redis.io/commands/xreadgroup and https://redis.io/commands/xread BLOCK option
     *
     * @return timeout on stream read in millis. Waiting on stream until this time for new message or return null
     * @throws BaseException
     *             Exception on read properties
     */
    Long getStreamReadTimeoutMillis() throws BaseException;

}
