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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.context.bound.BoundRequestContext;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManagerConnection;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamConsumer;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.service.RedisStreamService;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.resps.StreamEntry;

/**
 * Redis stream consumer executor class
 * 
 * @author imre.scheffer
 * @author czenczl
 * @since 1.3.0
 */
@Dependent
public class RedisStreamConsumerExecutor implements IRedisStreamConsumerExecutor {

    /**
     * Jedis driver hibakódja ha nem tálható a stream vagy a csoport
     */
    private static final String NOGROUP_PREFIX = "NOGROUP";

    @Inject
    private Logger log;

    @Inject
    private RedisStreamService redisStreamService;

    @Inject
    private BeanManager beanManager;

    @Inject
    private BoundRequestContext boundRequestContext;

    @Inject
    private StreamGroupConfig streamGroupConfig;

    private String consumerIdentifier;

    private String redisConfigKey;

    private boolean endLoop;

    private Bean<? super IRedisStreamBaseConsumer> consumerBean;

    @Override
    public void init(String redisConfigKey, String group, Bean<? super IRedisStreamBaseConsumer> consumerBean) {
        this.redisConfigKey = redisConfigKey;
        redisStreamService.setGroup(group);
        this.consumerBean = consumerBean;
        this.streamGroupConfig.setConfigKey(group);
    }

    /**
     * Vegtelen ciklus inditasa, ami a streamet olvassa
     */
    public void startLoop() {
        consumerIdentifier = RandomUtil.generateId();
        endLoop = false;
        // óvatos futás, ellenőrzi a stream es csoport létezését
        boolean prudentRun = true;
        while (!endLoop) {
            Optional<StreamEntry> streamEntry = Optional.empty();
            String configKey = StringUtils.isEmpty(streamGroupConfig.getConnectionKey()) ? redisConfigKey : streamGroupConfig.getConnectionKey();

            Instance<RedisManager> redisManagerInstance = CDI.current().select(RedisManager.class, new RedisConnection.Literal(configKey, streamGroupConfig.getConsumerPool()));
            RedisManager redisManager = redisManagerInstance.get();
            try (RedisManagerConnection ignore = redisManager.initConnection()) {
                redisStreamService.setRedisManager(redisManager);
                if (prudentRun) {
                    // lehethogy a csoport nem letezik
                    redisStreamService.handleGroup();
                    prudentRun = false;
                }

                streamEntry = redisStreamService.consumeOne(consumerIdentifier);

                if (streamEntry.isPresent()) {
                    var entry = streamEntry.get();
                    handleMDC(entry);
                    consumeStreamEntry(entry, redisManager);
                }
            } catch (BaseException e) {
                log.error(MessageFormat.format("Exception on consume streamEntry [{0}]: [{1}]", streamEntry, e.getLocalizedMessage()), e);
                var cause = e.getCause();
                if (!(cause instanceof JedisDataException)) {
                    continue;
                }
                String message = cause.getLocalizedMessage();
                // JedisDataException: NOGROUP No such key 'xyStream' or consumer group 'xy' in XREADGROUP with GROUP option
                // ha elpusztul a Redis, helyre kell tudni allitani a stream es a csoportot
                if (StringUtils.startsWith(message, NOGROUP_PREFIX)) {
                    log.error(
                            "Detected problem on redisConfigKey [{0}] with stream group [{1}] and activating prudentRun on next cycle. Exception: [{2}]",
                            redisConfigKey, redisStreamService.getGroup(), message);
                    prudentRun = true;
                } else {
                    log.error(MessageFormat.format("Exception on redisConfigKey [{0}] with stream group [{1}]: [{2}]", redisConfigKey,
                            redisStreamService.getGroup(), message), cause);
                }
                redisManager.closeConnection();
                sleep();
            } catch (Throwable e) {
                log.error(MessageFormat.format("Exception during consume on redisConfigKey [{0}] with stream group [{1}]: [{2}]", redisConfigKey,
                        redisStreamService.getGroup(), e.getLocalizedMessage()), e);
                redisManager.closeConnection();
                sleep();
            } finally {
                cleanup(redisManagerInstance, redisManager);
            }
        }
    }

    private void cleanup(Instance<RedisManager> redisManagerInstance, RedisManager redisManager) {
        try {
            if (redisManager != null) {
                // el kell engedni a connectiont
                redisManagerInstance.destroy(redisManager);
            }
            MDC.clear();
        } catch (Throwable e) {
            log.error(MessageFormat.format("Exception during redisManager cleanup on redisConfigKey [{0}] with stream group [{1}]: [{2}]",
                    redisConfigKey, redisStreamService.getGroup(), e.getLocalizedMessage()), e);
        }
    }

    /**
     * It represents one iteration on one stream (even empty). If the process exists and runs successfully, it sends the ACK
     *
     * @param streamEntry
     *            Stream event element
     * @param redisManager
     *            redis connection, operation manager object
     * @throws BaseException
     *             Technical exception
     */
    protected void consumeStreamEntry(StreamEntry streamEntry, RedisManager redisManager) throws BaseException {
        Optional<Map<String, Object>> result = executeOnStream(streamEntry, 1);

        // ack
        ack(streamEntry.getID());
        afterAckInRequestScope(streamEntry, result.orElse(Collections.emptyMap()));
    }

    /**
     * Stream entry ACK
     *
     * @param streamEntryID
     *            Jedis StreamEntry ID
     * @throws BaseException
     *             Technical exception
     */
    protected void ack(StreamEntryID streamEntryID) throws BaseException {
        redisStreamService.ackInCurrentConnection(streamEntryID);
    }

    /**
     * Process execution with retry count. If retry {@code RedisStreamConsumer#retryCount()} &gt; count then on processing exception trying run again
     * and again
     * 
     * @param streamEntry
     *            Redis stream input entry
     * @param counter
     *            currently run count
     * @return {@code Optional} result data from {@code IRedisStreamPipeConsumer#onStream(StreamEntry)}
     * @throws BaseException
     *             exception is error
     */
    protected Optional<Map<String, Object>> executeOnStream(StreamEntry streamEntry, int counter) throws BaseException {
        try {
            return onStreamInRequestScope(streamEntry);
        } catch (BaseException e) {
            RedisStreamConsumer redisStreamConsumerAnnotation = AnnotationUtil.getAnnotation(consumerBean.getBeanClass(), RedisStreamConsumer.class);

            streamGroupConfig.setConfigKey(redisStreamConsumerAnnotation.group());
            int retryCount = streamGroupConfig.getRetryCount().orElse(redisStreamConsumerAnnotation.retryCount());
            if (counter < retryCount) {
                String msg = MessageFormat.format("Exception occured on running class [{0}], trying again [{1}]/[{2}]", consumerBean.getBeanClass(),
                        counter + 1, retryCount);
                if (log.isDebugEnabled()) {
                    log.debug(msg, e);
                } else {
                    String info = MessageFormat.format("{0}: [{1}], cause: [{2}]", msg, e.getLocalizedMessage(),
                            Optional.ofNullable(e.getCause()).map(Throwable::getLocalizedMessage).orElse(null));
                    // do not spam the info log
                    log.info(info);
                }
                return executeOnStream(streamEntry, counter + 1);
            } else {
                throw e;
            }
        }
    }

    /**
     * Process execution wrapper. Running process in self started request scope
     * 
     * @param streamEntry
     *            Redis stream input entry
     * @return {@code Optional} result data from {@code IRedisStreamPipeConsumer#onStream(StreamEntry)}
     * @throws BaseException
     *             exception is error
     */
    protected Optional<Map<String, Object>> onStreamInRequestScope(StreamEntry streamEntry) throws BaseException {
        // get reference for the consumerBean
        Object consumer = beanManager.getReference(consumerBean, consumerBean.getBeanClass(), beanManager.createCreationalContext(consumerBean));

        Map<String, Object> requestScopeStore = null;
        try {
            requestScopeStore = new ConcurrentHashMap<>();
            startRequestScope(requestScopeStore);
            if (consumer instanceof IRedisStreamConsumer) {
                ((IRedisStreamConsumer) consumer).onStream(streamEntry);
            } else if (consumer instanceof IRedisStreamPipeConsumer) {
                Map<String, Object> result = ((IRedisStreamPipeConsumer) consumer).onStream(streamEntry);
                return Optional.of(result);
            }
            return Optional.empty();
        } finally {
            endRequestScope(requestScopeStore);
        }
    }

    /**
     * Process execution wrapper. Running {@code IRedisStreamPipeConsumer#afterAck(StreamEntry, Map)} process in self started request scope
     * 
     * @param streamEntry
     *            Redis stream input entry
     * @param onStreamResult
     *            result of {@code IRedisStreamPipeConsumer#onStream(StreamEntry)}
     * @throws BaseException
     *             exception is error
     */
    protected void afterAckInRequestScope(StreamEntry streamEntry, Map<String, Object> onStreamResult) throws BaseException {
        if (!consumerBean.getBeanClass().isAssignableFrom(IRedisStreamPipeConsumer.class)) {
            return;
        }
        // get reference for the consumerBean
        Object consumer = beanManager.getReference(consumerBean, consumerBean.getBeanClass(), beanManager.createCreationalContext(consumerBean));

        Map<String, Object> requestScopeStore = null;
        try {
            requestScopeStore = new ConcurrentHashMap<>();
            startRequestScope(requestScopeStore);
            if (consumer instanceof IRedisStreamPipeConsumer) {
                ((IRedisStreamPipeConsumer) consumer).afterAck(streamEntry, onStreamResult);
            }
        } finally {
            endRequestScope(requestScopeStore);
        }
    }

    private void startRequestScope(Map<String, Object> requestScopeDataStore) {
        boundRequestContext.associate(requestScopeDataStore);
        boundRequestContext.activate();
    }

    private void endRequestScope(Map<String, Object> requestScopeDataStore) {
        try {
            boundRequestContext.invalidate();
            boundRequestContext.deactivate();
        } finally {
            if (requestScopeDataStore != null) {
                boundRequestContext.dissociate(requestScopeDataStore);
            }
        }
    }

    private void sleep() {
        try {
            // fontos a szuneteltetes hogy peldaul a connection szakadasa ne floodolja a logot
            // es ne menjen felesleges korlatlan vegtelen probalkosba
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException ex) {
            log.warn("Interrupted sleep.", ex);
            // sonar: "InterruptedException" should not be ignored (java:S2142)
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.warn("Exception during interrupt.", ex);
            }
        }
    }

    /**
     * Logging MDC handling, setting variables
     * 
     * @param streamEntry
     *            {@link IRedisStreamConsumer#onStream(StreamEntry)}
     */
    protected void handleMDC(StreamEntry streamEntry) {
        Map<String, String> fieldMap = streamEntry.getFields();
        String flowId = fieldMap.getOrDefault(IRedisStreamConstant.Common.DATA_KEY_FLOW_ID,
                fieldMap.get(IRedisStreamConstant.Common.DATA_KEY_MESSAGE));
        MDC.put(LogConstants.LOG_SESSION_ID, flowId);
    }

    /**
     * Uniq stream consumer identifier
     * 
     * @return identifier
     */
    public String getConsumerIdentifier() {
        return consumerIdentifier;
    }

    /**
     * Stop endless stream reading
     */
    public void stopLoop() {
        endLoop = true;
    }

    @Override
    public void run() {
        try {
            startLoop();
        } finally {
            CDI.current().destroy(this);
        }
    }

    /**
     * Returns the consumer bean instance
     * 
     * @return the consumer bean instance
     */
    public Bean<? super IRedisStreamBaseConsumer> getConsumerBean() {
        return consumerBean;
    }
}
