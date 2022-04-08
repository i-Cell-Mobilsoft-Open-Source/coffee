/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redisstream.consumer;

import java.util.Map;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import redis.clients.jedis.resps.StreamEntry;

/**
 * Stream pipe consumer interface. The main thing is to be able to run the stream consumer and the post-ACK operation separately in their own request
 * scope. The two operations {@code #onStream(StreamEntry)} and {@code #afterAck(StreamEntry, Map)} are completely isolated from each other, only the
 * output and input parameters are common objects<br>
 * <br>
 * If need simple version, see {@link IRedisStreamConsumer}
 * 
 * @author imre.scheffer
 * @since 1.5.0
 * @see IRedisStreamConsumer
 */
public interface IRedisStreamPipeConsumer extends IRedisStreamBaseConsumer {

    /**
     * Incoming event handle logic. Executed in separated request scope
     * 
     * @param streamEntry
     *            stream message
     * @return result data which can be used after the request scope destroying
     * @throws BaseException
     *             technical error
     */
    Map<String, Object> onStream(StreamEntry streamEntry) throws BaseException;

    /**
     * Running after Redis ACK. Executed in separated request scope
     * 
     * @param streamEntry
     *            stream event Redis object
     * @param onStreamResult
     *            input from {@code #onStream(StreamEntry)} result
     * @throws BaseException
     *             technical error
     */
    void afterAck(StreamEntry streamEntry, Map<String, Object> onStreamResult) throws BaseException;
}
