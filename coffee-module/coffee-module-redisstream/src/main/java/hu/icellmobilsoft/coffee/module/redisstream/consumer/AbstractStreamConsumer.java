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
package hu.icellmobilsoft.coffee.module.redisstream.consumer;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import redis.clients.jedis.StreamEntry;

/**
 * Default Redis stream consumer
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public abstract class AbstractStreamConsumer extends BaseStreamConsumer implements IRedisStreamConsumer {

    /**
     * {@inheritDoc}
     * 
     * <br>
     * Input is full Redis Stream message
     */
    @Override
    public void onStream(StreamEntry streamEntry) throws BaseException {
        handleMDC(streamEntry);

        if (customize(streamEntry)) {
            return;
        }
        String mainData = streamEntry.getFields().get(IRedisStreamConstant.Common.DATA_KEY_MESSAGE);
        doWork(mainData);
    }

    /**
     * Default imlepmenting menthod, contains only business logic
     * 
     * @param text
     *            stream data content, {@value IRedisStreamConstant.Common#DATA_KEY_MESSAGE} key value, which can be string or json
     * @throws BaseException
     *             technical error on processing
     */
    public abstract void doWork(String text) throws BaseException;
}
