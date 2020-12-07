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

import java.util.HashMap;
import java.util.Map;

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
import hu.icellmobilsoft.coffee.module.redisstream.service.RedisStreamService;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;

/**
 * Redis stream helper functions
 * 
 * @author imre.scheffer
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
        checkInitialization();
        return publishBase(streamGroup, streamMessage);
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
        if (jedisInstance == null) {
            throw new TechnicalException("RedisStreamHandler is not initialized!");
        }
        if (StringUtils.isBlank(streamGroup)) {
            throw new TechnicalException("Input of custom streamGroup is null!");
        }
        return publishBase(streamGroup, streamMessage);
    }

    protected StreamEntryID publishBase(String streamGroup, String streamMessage) throws BaseException {
        Map<String, String> keyValues = new HashMap<>();
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_FLOW_ID, MDC.get(LogConstants.LOG_SESSION_ID));
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_MESSAGE, streamMessage);
        Jedis jedis = null;
        try {
            jedis = jedisInstance.get();
            redisStreamService.setGroup(streamGroup);
            redisStreamService.setJedis(jedis);
            return redisStreamService.publish(keyValues);
        } finally {
            if (jedis != null) {
                // el kell engedni a connectiont
                jedisInstance.destroy(jedis);
            }
        }
    }

    protected void checkInitialization() throws BaseException {
        if (jedisInstance == null || streamGroup == null) {
            throw new TechnicalException("RedisStreamHandler is not initialized!");
        }
    }
}
