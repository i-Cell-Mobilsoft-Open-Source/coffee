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
package hu.icellmobilsoft.coffee.module.redisstream.common;

import java.util.Map;

import hu.icellmobilsoft.coffee.module.redisstream.config.StreamMessageParameter;

/**
 * Value class for redis stream publications
 *
 * @author martin.nagy
 * @since 1.3.0
 */
public class RedisStreamPublication {

    /**
     * Stream group to send the message (another than initialized)
     */
    private final String streamGroup;

    /**
     * Message in stream. Can be String or JSON
     */
    private final String streamMessage;

    /**
     * Message parameters
     */
    private Map<StreamMessageParameter, String> parameters;

    /**
     * Creates the value class for redis stream publication
     *
     * @param streamGroup
     *            Stream group to send the message (another than initialized)
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @return the create value class
     */
    public static RedisStreamPublication of(String streamGroup, String streamMessage) {
        return of(streamGroup, streamMessage, null);
    }

    /**
     * Creates the value class for redis stream publication
     *
     * @param streamGroup
     *            Stream group to send the message (another than initialized)
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters
     * @return the create value class
     */
    public static RedisStreamPublication of(String streamGroup, String streamMessage, Map<StreamMessageParameter, String> parameters) {
        return new RedisStreamPublication(streamGroup, streamMessage, parameters);
    }

    private RedisStreamPublication(String streamGroup, String streamMessage, Map<StreamMessageParameter, String> parameters) {
        this.streamGroup = streamGroup;
        this.streamMessage = streamMessage;
        this.parameters = parameters;
    }

    public String getStreamGroup() {
        return streamGroup;
    }

    public String getStreamMessage() {
        return streamMessage;
    }

    public Map<StreamMessageParameter, String> getParameters() {
        return parameters;
    }
}
