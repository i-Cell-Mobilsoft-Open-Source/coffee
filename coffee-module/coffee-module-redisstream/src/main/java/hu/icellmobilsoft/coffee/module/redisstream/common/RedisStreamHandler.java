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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.module.redisstream.config.IStreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamMessageParameter;
import hu.icellmobilsoft.coffee.module.redisstream.service.RedisStreamService;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

/**
 * Redis stream helper functions
 * 
 * @author imre.scheffer
 * @author martin.nagy
 * @since 1.3.0
 */
@Dependent
public class RedisStreamHandler {

    @Inject
    private RedisStreamService redisStreamService;

    @Inject
    private StreamGroupConfig config;

    private Instance<Jedis> jedisInstance;

    private String streamGroup;

    /**
     * Initialization
     * 
     * @param jedisInstance
     *            Jedis bean instance
     * @param streamGroup
     *            stream group for setting in {@link RedisStreamService}
     */
    public void init(Instance<Jedis> jedisInstance, String streamGroup) {
        this.jedisInstance = jedisInstance;
        this.streamGroup = streamGroup;
    }

    /**
     * Is enabled Redis stream? {@link IStreamGroupConfig#isEnabled()}
     * 
     * @return true - enabled
     */
    public boolean isRedisstreamEnabled() {
        return config.isEnabled();
    }

    /**
     * Publish (send) one message to stream calculated by initialized streamGroup name.
     * 
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @return Stream message object
     * @throws BaseException
     *             exception on sending
     */
    public StreamEntryID publish(String streamMessage) throws BaseException {
        return publish(streamMessage, (Map<String, String>) null);
    }

    /**
     * Publish (send) one message to stream calculated by initialized streamGroup name.
     * 
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Stream message object
     * @throws BaseException
     *             exception on sending
     */
    public StreamEntryID publish(String streamMessage, Map<String, String> parameters) throws BaseException {
        checkInitialization();
        return publishBase(streamGroup, streamMessage, parameters);
    }

    /**
     * Publish (send) one message to stream calculated by input streamGroup name.
     * 
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @return Stream message object
     * @throws BaseException
     *             exception on sending
     */
    public StreamEntryID publish(String streamGroup, String streamMessage) throws BaseException {
        return publish(streamGroup, streamMessage, null);
    }

    /**
     * Publish (send) one message to stream calculated by input streamGroup name.
     * 
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Stream message object
     * @throws BaseException
     *             exception on sending
     */
    public StreamEntryID publish(String streamGroup, String streamMessage, Map<String, String> parameters) throws BaseException {
        checkJedisInstance();
        validateGroup(streamGroup);
        return publishBase(streamGroup, streamMessage, parameters);
    }

    /**
     * Publish (send) one message to stream calculated by input publication streamGroup name.
     *
     * @param publication
     *            stream publication data
     * @return Stream message object
     * @throws BaseException
     *             exception on sending
     */
    public StreamEntryID publishPublication(RedisStreamPublication publication) throws BaseException {
        if (publication == null) {
            throw new TechnicalException("publication is null!");
        }
        checkJedisInstance();
        validateGroup(publication.getStreamGroup());
        return publishBase(publication.getStreamGroup(), publication.getStreamMessage(), publication.getParameters());
    }

    /**
     * Publish (send) multiple messages to stream calculated by input publication streamGroup name.
     *
     * @param publications
     *            stream publication data list
     * @return Stream message objects
     * @throws BaseException
     *             exception on sending
     */
    public List<StreamEntryID> publishPublications(List<RedisStreamPublication> publications) throws BaseException {
        if (publications == null) {
            throw new TechnicalException("publications is null!");
        }
        checkJedisInstance();

        Jedis jedis = null;
        try {
            jedis = jedisInstance.get();
            return publishPublications(jedis, publications);
        } finally {
            if (jedis != null) {
                // el kell engedni a connectiont
                jedisInstance.destroy(jedis);
            }
        }
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @return Stream message objects
     * @throws BaseException
     *             exception on sending
     */
    public List<StreamEntryID> publish(List<String> streamMessages) throws BaseException {
        return publish(streamMessages, null);
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Stream message objects
     * @throws BaseException
     *             exception on sending
     */
    public List<StreamEntryID> publish(List<String> streamMessages, Map<String, String> parameters) throws BaseException {
        if (streamMessages == null) {
            throw new TechnicalException("streamMessages is null!");
        }
        checkInitialization();

        Jedis jedis = null;
        try {
            jedis = jedisInstance.get();
            return publish(jedis, streamGroup, streamMessages, parameters);
        } finally {
            if (jedis != null) {
                // el kell engedni a connectiont
                jedisInstance.destroy(jedis);
            }
        }
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @return Stream message objects
     * @throws BaseException
     *             exception on sending
     */
    public List<StreamEntryID> publish(String streamGroup, List<String> streamMessages) throws BaseException {
        return publish(streamGroup, streamMessages, null);
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Stream message objects
     * @throws BaseException
     *             exception on sending
     */
    public List<StreamEntryID> publish(String streamGroup, List<String> streamMessages, Map<String, String> parameters) throws BaseException {
        if (streamMessages == null) {
            throw new TechnicalException("streamMessages is null!");
        }
        validateGroup(streamGroup);
        checkJedisInstance();

        Jedis jedis = null;
        try {
            jedis = jedisInstance.get();
            return publish(jedis, streamGroup, streamMessages, parameters);
        } finally {
            if (jedis != null) {
                // el kell engedni a connectiont
                jedisInstance.destroy(jedis);
            }
        }
    }

    private List<StreamEntryID> publishPublications(Jedis jedis, List<RedisStreamPublication> publications) throws BaseException {
        List<StreamEntryID> ids = new ArrayList<>();
        for (RedisStreamPublication publication : publications) {
            validateGroup(publication.getStreamGroup());
            StreamEntryID id = publish(jedis, publication.getStreamGroup(), publication.getStreamMessage(), publication.getParameters());
            ids.add(id);
        }
        return ids;
    }

    private List<StreamEntryID> publish(Jedis jedis, String streamGroup, List<String> streamMessages, Map<String, String> parameters)
            throws BaseException {
        List<StreamEntryID> ids = new ArrayList<>();
        for (String streamMessage : streamMessages) {
            StreamEntryID id = publish(jedis, streamGroup, streamMessage, parameters);
            ids.add(id);
        }
        return ids;
    }

    protected StreamEntryID publishBase(String streamGroup, String streamMessage, Map<String, String> parameters) throws BaseException {
        Jedis jedis = null;
        try {
            jedis = jedisInstance.get();
            return publish(jedis, streamGroup, streamMessage, parameters);
        } finally {
            if (jedis != null) {
                // el kell engedni a connectiont
                jedisInstance.destroy(jedis);
            }
        }
    }

    protected StreamEntryID publish(Jedis jedis, String streamGroup, String streamMessage, Map<String, String> parameters) throws BaseException {
        Map<String, String> keyValues = createJedisMessage(streamMessage, parameters);
        redisStreamService.setJedis(jedis);
        redisStreamService.setGroup(streamGroup);
        return redisStreamService.publish(keyValues);
    }

    protected Map<String, String> createJedisMessage(String streamMessage, Map<String, String> parameters) {
        Map<String, String> keyValues = new HashMap<>();
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_FLOW_ID, MDC.get(LogConstants.LOG_SESSION_ID));
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_MESSAGE, streamMessage);
        if (parameters != null) {
            parameters.entrySet().forEach(e -> keyValues.put(e.getKey(), e.getValue()));
        }
        return keyValues;
    }

    /**
     * Create one stream message parameter
     * 
     * @param parameterKey
     *            system parameter enum
     * @param parameterValue
     *            parameter value
     * @return Parameter entry
     * @throws BaseException
     *             exception on sending
     */
    public static Entry<String, String> parameterOf(StreamMessageParameter parameterKey, Object parameterValue) throws BaseException {
        if (parameterKey == null) {
            throw new TechnicalException("parameterKey is null!");
        }
        return Map.entry(parameterKey.getMessageKey(), String.valueOf(parameterValue));
    }

    protected void validateGroup(String streamGroup) throws TechnicalException {
        if (StringUtils.isBlank(streamGroup)) {
            throw new TechnicalException("Input of custom streamGroup is null!");
        }
    }

    protected void checkInitialization() throws BaseException {
        if (jedisInstance == null || streamGroup == null) {
            throw notInitializedException();
        }
    }

    protected void checkJedisInstance() throws TechnicalException {
        if (jedisInstance == null) {
            throw notInitializedException();
        }
    }

    private TechnicalException notInitializedException() {
        return new TechnicalException("RedisStreamHandler is not initialized!");
    }
}
