/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redisstream.action;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.common.RedisMessageTypeType;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamMessageParameter;
import hu.icellmobilsoft.coffee.module.redisstream.publisher.RedisStreamPublisher;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;
import redis.clients.jedis.AbstractPipeline;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.args.ExpiryOption;

/**
 *
 * General operations for handling messages in FIFO order
 * 
 * @param <T>
 *            the type of message sent to the stream
 *
 * @author imre.scheffer
 * @author tamas.cserhati
 * @since 2.1.0
 */
public abstract class EventControlAction<T> {

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * constructor
     */
    protected EventControlAction() {
    }

    /**
     * Redis service where the FIFO list will be stored.
     * 
     * @return redis manager
     */
    public abstract RedisManager getRedisManager();

    /**
     * Redis stream where the message is sent if FIFO processing needs to be started
     * 
     * @return redis stream handler
     */
    public abstract RedisStreamPublisher getRedisStreamPublisher();

    /**
     * FIFO/LIFO queue processing
     * 
     * @return Enum FIFO, LIFO...
     */
    public abstract RedisMessageTypeType messageType();

    /**
     * Handles FIFO logic. If the FIFO list is created with its first item, it publishes a message to the stream for processing. If the inserted item
     * is not the first one, the message is not published.
     *
     * @param key
     *            the key of the FIFO list
     * @param eventMessage
     *            the content of the FIFO list item, which will be converted to JSON format
     * @param secondsToExpire
     *            expiration time of the FIFO list
     * @param flowExtensionId
     *            extension of the extSessionId
     * @return {@literal true} if a publish occurred
     * @throws BaseException
     *             if any error occurs
     */
    public boolean serialStreamEvent(String key, T eventMessage, long secondsToExpire, String flowExtensionId) throws BaseException {
        if (eventMessage == null) {
            throw new InvalidParameterException(CoffeeFaultType.INVALID_INPUT, "element is null!");
        }
        if (StringUtils.isBlank(key)) {
            // If the list key is null, it indicates that there is no need for list-based sending, a simple publish will suffice.
            publish(JsonUtil.toJson(eventMessage), defaultMessageProperties());
            return true;
        }
        RedisManager redisManager = getRedisManager();
        try {
            redisManager.initConnection();

            Optional<Long> size = redisManager.run(UnifiedJedis::rpush, "rpush", key, JsonUtil.toJson(eventMessage));
            log.trace("<< rpush to [{0}]: [{1}]", key, size);
            if (size.isPresent() && size.get() > 1) {
                // If we are not going to publish, we increase the TTL of the pipe identifier key,
                // so we know there are no new list items that
                AbstractPipeline pipeline = redisManager.run(UnifiedJedis::pipelined, "pipelined serialStreamEvent expire").orElseThrow();
                pipeline.expire(createPipeIdKey(key), secondsToExpire);
                pipeline.expire(key, secondsToExpire, ExpiryOption.NX);
                pipeline.sync();
                return false;
            }

            // If the list has exactly one element after the rpush operation, we create or update the unique key used by the pipe consumer.
            // This identifier ensures that the pipe consumer can decide whether to continue the processing loop or stop,
            // as a new event is expected to arrive.
            AbstractPipeline pipeline = redisManager.run(UnifiedJedis::pipelined, "pipelined serialStreamEvent setex").orElseThrow();
            pipeline.setex(createPipeIdKey(key), secondsToExpire, RandomUtil.generateToken());
            pipeline.expire(key, secondsToExpire, ExpiryOption.NX);
            pipeline.sync();
        } finally {
            redisManager.closeConnection();
        }

        // Flow extension configuration to prevent logs from being grouped under a single SID
        Map<String, String> parameters = new HashMap<>(defaultMessageProperties());
        parameters.put(IRedisStreamConstant.Common.DATA_MESSAGE_TYPE, messageType().name());
        if (StringUtils.isNotBlank(flowExtensionId)) {
            parameters.put(StreamMessageParameter.FLOW_ID_EXTENSION.getMessageKey(), flowExtensionId);
        }

        // If exactly one item has been added to the list, the event is published with the list's key
        publish(key, parameters);
        return true;
    }

    /**
     * Handles FIFO logic. If the FIFO list is created with its first item, it publishes a message to the stream for processing. If the inserted item
     * is not the first one, the message is not published.
     *
     * @param key
     *            the key of the FIFO list
     * @param eventMessage
     *            the content of the FIFO list item, which will be converted to JSON format
     * @param flowExtensionId
     *            extension of the extSessionId
     * @return {@literal true} if a publish occurred
     * @throws BaseException
     *             if any error occurs
     */
    public boolean serialStreamEvent(String key, T eventMessage, String flowExtensionId) throws BaseException {
        return serialStreamEvent(key, eventMessage, getTtl(), flowExtensionId);
    }

    /**
     * publish message
     * 
     * @param key
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @throws BaseException
     *             if any error occurs
     */
    protected void publish(String key, Map<String, String> parameters) throws BaseException {
        getRedisStreamPublisher().publish(key, parameters);
    }

    /**
     * returns with TTL
     * 
     * @return the ttl
     */
    protected long getTtl() {
        return getRedisStreamPublisher().getStreamGroupConfig().getProducerTTL().orElse(-1L);
    }

    private String createPipeIdKey(String key) {
        return IRedisStreamConstant.Pipe.ID_PRE + key + IRedisStreamConstant.Pipe.ID_POST;
    }

    /**
     * default message properties, can be override if you need proper configuration
     * 
     * @return message properties with TTL set
     * @throws BaseException
     *             if any error occurs
     */
    protected Map<String, String> defaultMessageProperties() throws BaseException {
        long ttl = getTtl();
        long expiry = Instant.now().plus(ttl, ChronoUnit.SECONDS).toEpochMilli();
        return Map.ofEntries(RedisStreamPublisher.parameterOf(StreamMessageParameter.TTL, expiry));
    }

}
