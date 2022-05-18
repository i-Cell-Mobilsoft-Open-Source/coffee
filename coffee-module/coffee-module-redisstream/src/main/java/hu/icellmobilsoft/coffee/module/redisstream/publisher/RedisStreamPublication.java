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
package hu.icellmobilsoft.coffee.module.redisstream.publisher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
     * Message parameters. Map key value is standardized from {@link StreamMessageParameter} enum value
     */
    private Map<String, String> parameters;

    /**
     * Creates the value class for redis stream publication
     *
     * @param streamGroup
     *            Stream group to send the message (another than initialized), nullable
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
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @return the create value class
     */
    public static RedisStreamPublication of(String streamMessage) {
        return of(null, streamMessage, null);
    }

    /**
     * Creates the value class for redis stream publication
     * 
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return the create value class
     */
    public static RedisStreamPublication of(String streamMessage, Map<String, String> parameters) {
        return of(null, streamMessage, parameters);
    }

    /**
     * Set {@code StreamMessageParameter#TTL} parameter to message. If a parameter exists, it will be overwritten
     * 
     * @param expiryInSec
     *            expiry in seconds
     * @return Same object with setted {@code StreamMessageParameter#TTL} parameter
     */
    public RedisStreamPublication withTTL(long expiryInSec) {
        this.withParameter(StreamMessageParameter.TTL, Instant.now().plus(expiryInSec, ChronoUnit.SECONDS).toEpochMilli());
        return this;
    }

    /**
     * Set standard parameter to message. If a parameter exists, it will be overwritten
     * 
     * @param parameterKey
     *            parameter key
     * @param parameterValue
     *            parameter value
     * @return Same object with setted parameter
     */
    public RedisStreamPublication withParameter(StreamMessageParameter parameterKey, Object parameterValue) {
        this.getInitializedParameters().put(parameterKey.getMessageKey(), String.valueOf(parameterValue));
        return this;
    }

    /**
     * Set custom parameter to message. If a parameter exists, it will be overwritten
     * 
     * @param parameterKey
     *            Custom parameter key
     * @param parameterValue
     *            Custom parameter value
     * @return Same object with setted parameter
     */
    public RedisStreamPublication withParameter(String parameterKey, Object parameterValue) {
        this.getInitializedParameters().put(parameterKey, String.valueOf(parameterValue));
        return this;
    }

    private Map<String, String> getInitializedParameters() {
        if (this.getParameters() == null) {
            this.parameters = new HashMap<>();
        }
        return this.getParameters();
    }

    /**
     * Creates the value class for redis stream publication
     *
     * @param streamGroup
     *            Stream group to send the message (another than initialized), nullable
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return the create value class
     */
    public static RedisStreamPublication of(String streamGroup, String streamMessage, Map<String, String> parameters) {
        return new RedisStreamPublication(streamGroup, streamMessage, parameters);
    }

    private RedisStreamPublication(String streamGroup, String streamMessage, Map<String, String> parameters) {
        this.streamGroup = streamGroup;
        this.streamMessage = streamMessage;
        this.parameters = parameters;
    }

    /**
     * Returns Stream group to send the message
     * 
     * @return Stream group to send the message
     */
    public String getStreamGroup() {
        return streamGroup;
    }

    /**
     * Returns Message in stream. Can be String or JSON List
     * 
     * @return Message in stream. Can be String or JSON List
     */
    public String getStreamMessage() {
        return streamMessage;
    }

    /**
     * Returns Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * 
     * @return Messages parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }
}
