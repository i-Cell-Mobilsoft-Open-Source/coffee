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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManagerConnection;
import hu.icellmobilsoft.coffee.module.redisstream.common.RedisStreamUtil;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamMessageParameter;
import hu.icellmobilsoft.coffee.module.redisstream.service.RedisStreamService;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;

/**
 * Redis stream publish functions
 *
 * @author imre.scheffer
 * @author martin.nagy
 * @since 1.3.0
 */
@Dependent
public class RedisStreamPublisher {

    private static final int PIPELINE_SIZE = 1000;

    @Inject
    private Logger log;

    @Inject
    private StreamGroupConfig config;

    private RedisManager redisManager;

    private String streamGroup;

    /**
     * Default constructor, constructs a new object.
     */
    public RedisStreamPublisher() {
        super();
    }

    /**
     * Initialization
     *
     * @param redisManager
     *            redis connection, operation manager object
     * @param streamGroup
     *            stream group for setting in {@link RedisStreamService}
     */
    public void init(RedisManager redisManager, String streamGroup) {
        this.redisManager = redisManager;
        this.streamGroup = streamGroup;
        config.setConfigKey(streamGroup);
    }

    /**
     * Publish (send) one message to stream calculated by initialized streamGroup name.
     *
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public Optional<StreamEntryID> publish(String streamMessage) throws BaseException {
        return publish(streamMessage, (Map<String, String>) null);
    }

    /**
     * Publish (send) one message to stream calculated by initialized streamGroup name.
     *
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @param parameters
     *            Message parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public Optional<StreamEntryID> publish(String streamMessage, Map<String, String> parameters) throws BaseException {
        checkInitialization();
        return publishInNewConnection(streamGroup, streamMessage, parameters);
    }

    /**
     * Publish (send) one message to stream calculated by input streamGroup name.
     *
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessage
     *            Message in stream. Can be String or JSON
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public Optional<StreamEntryID> publish(String streamGroup, String streamMessage) throws BaseException {
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
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public Optional<StreamEntryID> publish(String streamGroup, String streamMessage, Map<String, String> parameters) throws BaseException {
        checkRedisManager();
        validateGroup(streamGroup);
        return publishInNewConnection(streamGroup, streamMessage, parameters);
    }

    /**
     * Publish (send) one message to stream calculated by input publication streamGroup name.
     *
     * @param publication
     *            stream publication data
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public Optional<StreamEntryID> publishPublication(RedisStreamPublication publication) throws BaseException {
        if (publication == null) {
            throw new InvalidParameterException("publication is null!");
        }
        checkRedisManager();
        if (StringUtils.isBlank(publication.getStreamGroup())) {
            validateGroup(streamGroup);
            return publishInNewConnection(streamGroup, publication.getStreamMessage(), publication.getParameters());
        } else {
            return publishInNewConnection(publication.getStreamGroup(), publication.getStreamMessage(), publication.getParameters());
        }
    }

    /**
     * Publish (send) multiple messages to stream calculated by input publication streamGroup name.
     *
     * @param publications
     *            stream publication data list
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publishPublications(List<RedisStreamPublication> publications) throws BaseException {
        if (publications == null) {
            throw new InvalidParameterException("publications is null!");
        }
        checkRedisManager();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            List<Optional<StreamEntryID>> ids = new ArrayList<>(publications.size());
            for (RedisStreamPublication publication : publications) {
                Optional<StreamEntryID> id;
                if (StringUtils.isBlank(publication.getStreamGroup())) {
                    validateGroup(streamGroup);
                    id = publishInActiveConnection(createJedisMessage(publication.getStreamMessage(), publication.getParameters()), streamGroup);
                } else {
                    id = publishInActiveConnection(
                            createJedisMessage(publication.getStreamMessage(), publication.getParameters()),
                            publication.getStreamGroup());
                }
                ids.add(id);
            }
            return ids;
        }
    }

    /**
     * Publish (send) multiple messages through pipeline to stream calculated by input publication streamGroup name.
     *
     * @param publications
     *            stream publication data list
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publishPublicationsPipelined(List<RedisStreamPublication> publications) throws BaseException {
        if (publications == null) {
            throw new InvalidParameterException("publications is null!");
        }
        checkRedisManager();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            List<Response<StreamEntryID>> responses = new ArrayList<>(publications.size());
            Pipeline pipeline = initPipeline();
            int i = 1;
            for (RedisStreamPublication publication : publications) {
                Response<StreamEntryID> response;
                if (StringUtils.isBlank(publication.getStreamGroup())) {
                    validateGroup(streamGroup);
                    response = publishThroughPipeline(
                            pipeline,
                            streamGroup,
                            createJedisMessage(publication.getStreamMessage(), publication.getParameters()));
                } else {
                    validateGroup(publication.getStreamGroup());
                    response = publishThroughPipeline(
                            pipeline,
                            publication.getStreamGroup(),
                            createJedisMessage(publication.getStreamMessage(), publication.getParameters()));
                }
                responses.add(response);
                i = syncPipelineIfNeeded(pipeline, publications.size(), i);
            }
            syncPipelineIfNeeded(pipeline, publications.size(), i);
            return getStreamEntryIds(responses);
        }
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publish(List<String> streamMessages) throws BaseException {
        return publish(streamMessages, null);
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publish(List<String> streamMessages, Map<String, String> parameters) throws BaseException {
        validateStreamMessages(streamMessages);
        checkInitialization();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            return publishInActiveConnection(streamGroup, streamMessages, parameters);
        }
    }

    /**
     * Publish (send) multiple messages to stream calculated by input streamGroup name.
     *
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publish(String streamGroup, List<String> streamMessages) throws BaseException {
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
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publish(String streamGroup, List<String> streamMessages, Map<String, String> parameters)
            throws BaseException {
        validateStreamMessages(streamMessages);
        validateGroup(streamGroup);
        checkRedisManager();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            return publishInActiveConnection(streamGroup, streamMessages, parameters);
        }
    }

    /**
     * Publish (send) multiple messages to stream
     *
     * @param streamGroup
     *            Stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    protected List<Optional<StreamEntryID>> publishInActiveConnection(String streamGroup, List<String> streamMessages, Map<String, String> parameters)
            throws BaseException {
        List<Optional<StreamEntryID>> ids = new ArrayList<>(streamMessages.size());
        for (String streamMessage : streamMessages) {
            Optional<StreamEntryID> id = publishInActiveConnection(createJedisMessage(streamMessage, parameters), streamGroup);
            ids.add(id);
        }
        return ids;
    }

    /**
     * Publish (send) multiple messages through pipeline to stream calculated by input streamGroup name.
     *
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publishPipelined(List<String> streamMessages) throws BaseException {
        return publishPipelined(streamMessages, null);
    }

    /**
     * Publish (send) multiple messages through pipeline to stream with pipeline calculated by input streamGroup name.
     *
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publishPipelined(List<String> streamMessages, Map<String, String> parameters) throws BaseException {
        validateStreamMessages(streamMessages);
        checkInitialization();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            return publishPipelinedInActiveConnection(streamGroup, streamMessages, parameters);
        }
    }

    /**
     * Publish (send) multiple messages through pipeline to stream with pipeline calculated by input streamGroup name.
     *
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publishPipelined(String streamGroup, List<String> streamMessages) throws BaseException {
        return publishPipelined(streamGroup, streamMessages, null);
    }

    /**
     * Publish (send) multiple messages through pipeline to stream with pipeline calculated by input streamGroup name.
     *
     * @param streamGroup
     *            stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    public List<Optional<StreamEntryID>> publishPipelined(String streamGroup, List<String> streamMessages, Map<String, String> parameters)
            throws BaseException {
        validateStreamMessages(streamMessages);
        validateGroup(streamGroup);
        checkRedisManager();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            return publishPipelinedInActiveConnection(streamGroup, streamMessages, parameters);
        }
    }

    /**
     * Publish (send) multiple messages through pipeline to stream
     *
     * @param streamGroup
     *            Stream group to send (another than initialized)
     * @param streamMessages
     *            Messages in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream messages identifiers from Redis server
     * @throws BaseException
     *             exception on sending
     */
    protected List<Optional<StreamEntryID>> publishPipelinedInActiveConnection(String streamGroup, List<String> streamMessages,
            Map<String, String> parameters) throws BaseException {

        Pipeline pipeline = initPipeline();

        int i = 1;
        List<Response<StreamEntryID>> responses = new ArrayList<>(streamMessages.size());
        for (String streamMessage : streamMessages) {
            Response<StreamEntryID> response = publishThroughPipeline(pipeline, streamGroup, createJedisMessage(streamMessage, parameters));
            responses.add(response);
            i = syncPipelineIfNeeded(pipeline, streamMessages.size(), i);
        }
        syncPipelineIfNeeded(pipeline, streamMessages.size(), i);
        return getStreamEntryIds(responses);
    }

    /**
     * Publish (send) message to stream with class initialized {@code #jedisInstance}
     *
     * @param streamGroup
     *            Stream group to send (another than initialized)
     * @param streamMessage
     *            Message in stream. Can be String or JSON List
     * @param parameters
     *            Messages parameters, nullable. Map key value is standardized in {@link StreamMessageParameter} enum value
     * @return Created Redis Stream message identifier from Redis server
     * @throws BaseException
     *             exception on sending
     */
    protected Optional<StreamEntryID> publishInNewConnection(String streamGroup, String streamMessage, Map<String, String> parameters)
            throws BaseException {
        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            return publishInActiveConnection(createJedisMessage(streamMessage, parameters), streamGroup);
        }
    }

    /**
     * Create Redis Stream message structure, ready to publish
     *
     * @param streamMessage
     *            Message in stream. Can be String or JSON List
     * @param parameters
     *            Message parameters, ttt, SID
     * @return Redis Stream message structure, ready to publish
     */
    protected Map<String, String> createJedisMessage(String streamMessage, Map<String, String> parameters) {
        Map<String, String> keyValues = new HashMap<>();
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_FLOW_ID, getFlowIdMessage(parameters));
        keyValues.put(IRedisStreamConstant.Common.DATA_KEY_MESSAGE, streamMessage);
        // Intentionally, it's positioned at the end so that the above values can be overridden if needed.
        if (parameters != null) {
            keyValues.putAll(parameters);
        }
        return keyValues;
    }

    private String getFlowIdMessage(Map<String, String> parameters) {
        String flowIdMessage = MDC.get(LogConstants.LOG_SESSION_ID);
        if (parameters == null) {
            return flowIdMessage;
        }
        return Optional.ofNullable(parameters.get(StreamMessageParameter.FLOW_ID_EXTENSION.getMessageKey()))
                .map(extension -> flowIdMessage + "_" + extension)
                .orElse(flowIdMessage);
    }

    /**
     * Publish one element to stream with values. Stream max size is trimmed by config. This is equivalent to redis console:
     *
     * <pre>
     * XADD streamKey * key1 value1 key2 value2...
     * </pre>
     *
     * @param values
     *            Values in stream element
     * @param streamGroup
     *            the redis stream group
     * @return Generated ID
     * @throws BaseException
     *             Exception
     */
    protected Optional<StreamEntryID> publishInActiveConnection(Map<String, String> values, String streamGroup) throws BaseException {
        XAddParams params = getXAddParams();
        Optional<StreamEntryID> streamEntryID = redisManager.run(Jedis::xadd, "xadd", RedisStreamUtil.streamKey(streamGroup), values, params);
        if (log.isTraceEnabled()) {
            log.trace("Published streamEntryID: [{0}] into [{1}]", streamEntryID, RedisStreamUtil.streamKey(streamGroup));
        }
        return streamEntryID;
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
            throw new InvalidParameterException("parameterKey is null!");
        }
        return Map.entry(parameterKey.getMessageKey(), String.valueOf(parameterValue));
    }

    /**
     * Validates the passed redis stream group
     *
     * @param streamGroup
     *            the stream group to validate
     * @throws TechnicalException
     *             if validation fails
     */
    protected void validateGroup(String streamGroup) throws TechnicalException {
        if (StringUtils.isBlank(streamGroup)) {
            throw new InvalidParameterException("Input of custom streamGroup is null!");
        }
    }

    /**
     * Validates the instance fields
     *
     * @throws BaseException
     *             if validation fails
     */
    protected void checkInitialization() throws BaseException {
        if (redisManager == null || streamGroup == null) {
            throw notInitializedException();
        }
    }

    /**
     * Validates the {@link #redisManager} field
     *
     * @throws TechnicalException
     *             if validation fails
     */
    protected void checkRedisManager() throws TechnicalException {
        if (redisManager == null) {
            throw notInitializedException();
        }
    }

    /**
     * Validates streamMessages
     * 
     * @param streamMessages
     *            streamMessages
     * @throws BaseException
     *             if validation fails
     */
    protected void validateStreamMessages(List<String> streamMessages) throws BaseException {
        if (streamMessages == null) {
            throw new InvalidParameterException("streamMessages is null!");
        }
    }

    /**
     * Returns the default XAddParams
     * 
     * @return the default XAdd params
     */
    protected XAddParams getXAddParams() {
        XAddParams params = XAddParams.xAddParams();
        config.getProducerMaxLen().ifPresent(params::maxLen);
        config.getProducerTTL().ifPresent(ttl -> params.minId(new StreamEntryID(Instant.now().minusMillis(ttl).toEpochMilli(), 0).toString()));
        return params;
    }

    /**
     * Initializes a new pipeline<br/>
     * Requires active connection
     * 
     * @return pipeline instance
     * @throws BaseException
     *             if any error occurred while creating pipeline
     */
    protected Pipeline initPipeline() throws BaseException {
        return redisManager.run(Jedis::pipelined, "pipelined")
                .orElseThrow(() -> new BaseException(CoffeeFaultType.REDIS_OPERATION_FAILED, "Error occurred while creating pipeline"));
    }

    /**
     * Publish (send) message to stream through pipeline
     *
     * @param pipeline
     *            The pipeline instance
     * @param streamGroup
     *            Stream group to send
     * @param jedisMessage
     *            Redis Stream message structure, ready to publish
     *
     * @return {@link Pipeline#xadd(String, Map, XAddParams)} response
     */
    protected Response<StreamEntryID> publishThroughPipeline(Pipeline pipeline, String streamGroup, Map<String, String> jedisMessage) {
        return pipeline.xadd(RedisStreamUtil.streamKey(streamGroup), jedisMessage, getXAddParams());
    }

    /**
     * Gets StreamEntryIds-s from pipeline responses
     * 
     * @param responses
     *            {@link Pipeline#xadd(String, Map, XAddParams)} response list
     * @return list of optional StreamEntryIds
     */
    protected List<Optional<StreamEntryID>> getStreamEntryIds(List<Response<StreamEntryID>> responses) {
        List<Optional<StreamEntryID>> streamEntryIds = responses.stream().map(Response::get).map(Optional::ofNullable).toList();
        if (log.isTraceEnabled()) {
            log.trace(
                    "Published [{0}] streamEntries into [{1}]",
                    streamEntryIds.stream().filter(Optional::isPresent).count(),
                    RedisStreamUtil.streamKey(streamGroup));
        }
        return streamEntryIds;
    }

    /**
     * Syncs pipeline if needed and increases the counter<br/>
     * If pipelined responses reach {@link RedisStreamPublisher#PIPELINE_SIZE},<br/>
     * or if all messages have been sent, and there are pipelined responses,<br/>
     * then {@link Pipeline#sync()} is called<br/>
     *
     * @param pipeline
     *            the Pipeline instance
     * @param messagesToSend
     *            The number of messages to be sent
     * @param messagesSent
     *            The number of sent messages
     * @return The number of sent messages increased
     * 
     */
    protected int syncPipelineIfNeeded(Pipeline pipeline, int messagesToSend, int messagesSent) {
        if ((messagesSent < messagesToSend && messagesSent % getPipelineSize() == 0)
                || (messagesSent == messagesToSend && pipeline.hasPipelinedResponse())) {
            pipeline.sync();
        }
        messagesSent++;
        return messagesSent;
    }

    /**
     * Returns the size of the pipeline
     *
     * @return the size of the pipeline
     */
    protected int getPipelineSize() {
        return PIPELINE_SIZE;
    }

    /**
     * returns the stream group config
     * 
     * @return the stream group config
     */
    public final StreamGroupConfig getStreamGroupConfig() {
        return config;
    }

    private TechnicalException notInitializedException() {
        return new TechnicalException("RedisStreamHandler is not initialized!");
    }
}
