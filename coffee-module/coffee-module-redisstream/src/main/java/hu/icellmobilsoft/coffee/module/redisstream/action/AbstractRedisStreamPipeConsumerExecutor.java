package hu.icellmobilsoft.coffee.module.redisstream.action;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.common.common.RedisMessageTypeType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManagerConnection;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.module.redisstream.consumer.RedisStreamConsumerExecutor;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.resps.StreamEntry;

/**
 * Special serializable (FIFO, LIFO) processing Stream executor
 * 
 * @author imre.scheffer
 * @author tamas.cserhati
 * @since 2.1.0
 */
public abstract class AbstractRedisStreamPipeConsumerExecutor extends RedisStreamConsumerExecutor {

    @Inject
    private Logger log;

    /**
     * constructor 
     */
    protected AbstractRedisStreamPipeConsumerExecutor() {
    }

    /**
     * Handler for the {@code RedisService} that processes a FIFO/LIFO list.
     * 
     * @return The key used to populate {@code RedisConnection#configKey()} in the {@code RedisService} that reads the Redis FIFO/LIFO list.
     * @throws BaseException
     *             if any error occurs
     */
    protected abstract String pipeRedisServiceConfigKey() throws BaseException;

    @Override
    protected void consumeStreamEntry(StreamEntry streamEntry, RedisManager redisManager) throws BaseException {
        RedisMessageTypeType messageType = EnumUtils
                .getEnum(RedisMessageTypeType.class, streamEntry.getFields().get(IRedisStreamConstant.Common.DATA_MESSAGE_TYPE));
        if (messageType == RedisMessageTypeType.FIFO) {
            xExecuteOnStream(streamEntry, this::moveToLastRedisListValue);
            return;
        }
        if (messageType == RedisMessageTypeType.LIFO) {
            xExecuteOnStream(streamEntry, this::moveToFirstRedisListValue);
            return;
        }
        try {
            super.consumeStreamEntry(streamEntry, redisManager);
        } catch (Exception e) {
            handleException(null, streamEntry.getFields(), e);
        }
    }

    /**
     * returns the TTL value
     * 
     * @return ttl
     */
    protected long getTtl() {
        return getStreamGroupConfig().getProducerTTL().orElse(IRedisStreamConstant.Defaults.STREAM_READ_MAXIMUM_LATENCY_SECONDS_DEFAULT);
    }

    private Optional<String> moveToLastRedisListValue(RedisManager redisManager, String listKey) throws BaseException {
        return move(redisManager, listKey, ListDirection.LEFT, ListDirection.RIGHT);
    }

    private Optional<String> moveToFirstRedisListValue(RedisManager redisManager, String listKey) throws BaseException {
        return move(redisManager, listKey, ListDirection.RIGHT, ListDirection.LEFT);
    }

    private Optional<String> move(RedisManager redisManager, String listKey, ListDirection from, ListDirection to) throws BaseException {
        Pipeline pipeline = redisManager.run(Jedis::pipelined, "pipelined move").orElseThrow();
        Response<String> res = pipeline.lmove(listKey, listKey, from, to);
        pipeline.expire(listKey, getTtl());
        pipeline.sync();
        return Optional.ofNullable(res.get());
    }

    private void xExecuteOnStream(StreamEntry streamEntry, BaseExceptionFunction2<RedisManager, String, Optional<String>> function)
            throws BaseException {
        // kikeresni a listat
        String listKey = streamEntry.getFields().get(IRedisStreamConstant.Common.DATA_KEY_MESSAGE);

        RedisManager redisManager = CDI.current().select(RedisManager.class, new RedisConnection.Literal(pipeRedisServiceConfigKey())).get();

        try (RedisManagerConnection ignored = redisManager.initConnection()) {
            // Retrieve the pipe stream ID value — this is the key that determines how long we should stay within the loop.
            Optional<String> originPipeIdValue = getPipeIdValue(redisManager, listKey);

            log.debug("Redis stream list processing started for listKey: [{0}]", listKey);
            while (true) {
                Optional<String> message = function.apply(redisManager, listKey);
                if (message.isEmpty() || hasPipeIdValueChanged(listKey, redisManager, originPipeIdValue)) {
                    log.debug("Redis stream list processing finished for listKey: [{0}]. List is empty: [{1}]", listKey, message.isEmpty());
                    break;
                }

                xExecuteOnStreamMessage(streamEntry, listKey, redisManager, message.get());
            }
        }
    }

    private void xExecuteOnStreamMessage(StreamEntry streamEntry, String listKey, RedisManager redisManager, String message) throws BaseException {
        try {
            // Process elements properly using the retryCount logic
            executeProcessOnElement(streamEntry, message);
        } catch (Exception e) {
            // retryCount didn't help, but since we're in a list, we need to proceed to the next element
            handleException(listKey, message, e);
        } finally {
            // We started by moving the element to the end of the FIFO (or LIFO), so it must be removed;
            // otherwise, it would result in an infinite loop.
            redisManager.run(Jedis::lrem, "lrem", listKey, 0, message);
        }
    }

    private void handleException(String listKey, Object message, Exception e) {
        String msg = MessageFormat.format(
                "Exception occurred on running class [{0}]. message: [{1}], listKey: [{2}], element: [{3}]",
                getConsumerBean().getBeanClass(),
                e.getMessage(),
                listKey,
                message);
        log.error(msg, e);
    }

    private Optional<String> getPipeIdValue(RedisManager redisManager, String listKey) throws BaseException {
        return redisManager.run(Jedis::get, "get", IRedisStreamConstant.Pipe.ID + listKey);
    }

    private boolean hasPipeIdValueChanged(String listKey, RedisManager redisManager, Optional<String> originPipeIdValue) throws BaseException {
        // If the pipe stream consumer operation identifier has changed in the meantime, the process ends.
        // A new event will trigger a new processing cycle.
        // This is the point where EventControl overrides the operation identifier and starts a new process.
        Optional<String> pipeIdValue = getPipeIdValue(redisManager, listKey);
        return pipeIdValue.isPresent() && originPipeIdValue.isPresent() && !StringUtils.equals(originPipeIdValue.get(), pipeIdValue.get());
    }

    private void executeProcessOnElement(StreamEntry streamEntry, String nextElement) throws BaseException {
        Map<String, String> elementFieldMap = new HashMap<>(streamEntry.getFields());
        elementFieldMap.put(IRedisStreamConstant.Common.DATA_KEY_MESSAGE, nextElement);
        StreamEntry listElementStreamEntry = new StreamEntry(streamEntry.getID(), elementFieldMap);
        // Process all elements properly using the retryCount logic.
        executeOnStream(listElementStreamEntry, 1);
    }

}
