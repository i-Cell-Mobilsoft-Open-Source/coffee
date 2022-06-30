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
package hu.icellmobilsoft.coffee.module.redisstream.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManagerConnection;
import hu.icellmobilsoft.coffee.module.redisstream.common.RedisStreamUtil;
import hu.icellmobilsoft.coffee.module.redisstream.config.IStreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.se.logging.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamGroupInfo;
import redis.clients.jedis.resps.StreamPendingEntry;

/**
 * Service class for redis stream logic
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@Dependent
public class RedisStreamService {
    private static final int EXPIRED_MESSAGE_CLEANUP_BLOCK_SIZE = 1000;

    @Inject
    private Logger log;

    /**
     * {@link StreamGroupConfig#setConfigKey(String)} is setted in {@link #setGroup(String)}
     */
    @Inject
    private StreamGroupConfig config;

    private RedisManager redisManager;

    private String group;

    /**
     * Stream key, calculated by {@link #group}
     * 
     * @return Stream key
     */
    public String streamKey() {
        return RedisStreamUtil.streamKey(getGroup());
    }

    /**
     * Is enabled Redis stream? {@link IStreamGroupConfig#isEnabled()}
     *
     * @return true - enabled
     */
    public boolean isRedisStreamEnabled() {
        return config.isEnabled();
    }

    /**
     * Count elements in stream. This is equivalent to redis console:
     * 
     * <pre>
     * XLEN streamKey
     * </pre>
     * 
     * @return elements count
     * @throws BaseException
     *             Exception
     */
    public Long count() throws BaseException {
        Long count = getRedisManager().runWithConnection(Jedis::xlen, "xlen", streamKey()).orElse(0L);
        if (log.isTraceEnabled()) {
            log.trace("[{0}] stream have [{1}] elements", streamKey(), count);
        }
        return count;
    }

    /**
     * Check group exist. This is equivalent to redis console:
     * 
     * <pre>
     * XINFO GROUPS streamKey
     * </pre>
     * 
     * and search checking group
     * 
     * @return true if exist
     * @throws BaseException
     *             exception
     */
    public boolean existGroup() throws BaseException {
        try (RedisManagerConnection ignored = getRedisManager().initConnection()) {
            return existsGroupInActiveConnection();
        }
    }

    /**
     * Checking for existing group. If not exist, then create it, with also creating non-existing stream too. This is equivalent to redis console:
     * 
     * <pre>
     * XINFO GROUPS streamKey
     * # if not exist group then
     * XGROUP CREATE streamKey group 0 MKSTREAM
     * </pre>
     * 
     * @throws BaseException
     *             exception
     */
    public void handleGroup() throws BaseException {
        try (RedisManagerConnection ignored = getRedisManager().initConnection()) {
            if (existsGroupInActiveConnection()) {
                if (log.isTraceEnabled()) {
                    log.trace("Group [{0}] already exist", getGroup());
                }
            } else {
                Optional<String> createGroupResult = getRedisManager().run(Jedis::xgroupCreate, "xgroupCreate", streamKey(), getGroup(),
                        new StreamEntryID(), true);
                log.info("Stream group [{0}] on stream [{1}] created with result: [{2}]", getGroup(), streamKey(), createGroupResult);
            }
        }
    }

    private boolean existsGroupInActiveConnection() throws BaseException {
        try {
            Optional<List<StreamGroupInfo>> info = getRedisManager().run(Jedis::xinfoGroups, "xinfoGroups", streamKey());
            return info.isPresent() && info.get().stream().map(StreamGroupInfo::getName).anyMatch(name -> StringUtils.equals(getGroup(), name));
        } catch (TechnicalException e) {
            if (!(e.getCause() instanceof JedisDataException)) {
                throw e;
            }
            // ha nincs kulcs akkor a kovetkezo hiba jon:
            // redis.clients.jedis.exceptions.JedisDataException: ERR no such key
            log.info("Redis exception during checking group [{0}]: [{1}]", streamKey(), e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * Consume one entry from stream. Read timeout is defined in configuration. This is equivalent to redis console:
     * 
     * <pre>
     * XREADGROUP GROUP group consumerIdentifier BLOCK config.StreamReadTimeoutMillis COUNT 1 STREAMS mystream &gt;
     * </pre>
     * 
     * @param consumerIdentifier
     *            unique consumer identifier
     * @return stream entry
     * @throws BaseException
     *             exception
     */
    public Optional<StreamEntry> consumeOne(String consumerIdentifier) throws BaseException {
        if (StringUtils.isBlank(consumerIdentifier)) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, "consumerIdentifier is null");
        }
        Map<String, StreamEntryID> streamQuery = Map.of(streamKey(), StreamEntryID.UNRECEIVED_ENTRY);
        // kepes tobb streambol is egyszerre olvasni, de mi 1-re hasznaljuk
        XReadGroupParams params = new XReadGroupParams().count(1).block(config.getStreamReadTimeoutMillis().intValue());
        Optional<List<Entry<String, List<StreamEntry>>>> result = getRedisManager().run(Jedis::xreadGroup, "xreadGroup", getGroup(),
                consumerIdentifier, params, streamQuery);
        if (result.isEmpty() || result.get().isEmpty()) {
            // nincs uj uzenet
            if (log.isTraceEnabled()) {
                log.trace("No new message in [{0}] stream", streamKey());
            }
            return Optional.empty();
        }
        // 1 stream-bol olvasunk
        Entry<String, List<StreamEntry>> stream = result.get().get(0);
        if (stream.getValue() == null || stream.getValue().isEmpty()) {
            if (log.isTraceEnabled()) {
                log.trace("Stream key [{0}] in stream [{1}] no have values stream", stream.getKey(), streamKey());
            }
            return Optional.empty();
        }
        // csak 1 tetelt kertunk ki belole, tobb nem lessz benne
        StreamEntry entry = stream.getValue().get(0);
        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder("Consumed one entry from:");
            sb.append("\nStream key [" + stream.getKey() + "] ");
            sb.append("\n  ID: [" + entry.getID() + "], values: [");
            entry.getFields().forEach((key, value) -> sb.append("\n    Key[" + key + "]: Value[" + value + "]"));
            sb.append("\n]");
            log.trace(sb.toString());
        }
        return Optional.of(entry);
    }

    /**
     * ACK stream element. This is equivalent to redis console:
     * 
     * <pre>
     * XACK streamKey group 1526569495631-0
     * </pre>
     * 
     * @param streamEntryID
     *            stream element unique ID. If null then do nothing
     * @return success count, if &gt;0 then successfully ACKed
     * @throws BaseException
     *             Exception
     */
    public long ack(StreamEntryID streamEntryID) throws BaseException {
        if (Objects.isNull(streamEntryID)) {
            return 0;
        }

        try (RedisManagerConnection ignored = getRedisManager().initConnection()) {
            return ackInCurrentConnection(streamEntryID);
        }
    }

    /**
     * ACK stream element without opening a new connection. This is equivalent to redis console:
     *
     * <pre>
     * XACK streamKey group 1526569495631-0
     * </pre>
     *
     * @param streamEntryID
     *            stream element unique ID. If null then do nothing
     * @return success count, if &gt;0 then successfully ACKed
     * @throws BaseException
     *             Exception
     */
    public long ackInCurrentConnection(StreamEntryID streamEntryID) throws BaseException {
        if (Objects.isNull(streamEntryID)) {
            return 0;
        }
        long successCount = getRedisManager().run(Jedis::xack, "xack", streamKey(), getGroup(), streamEntryID).orElse(0L);
        if (log.isTraceEnabled()) {
            if (successCount > 0) {
                log.trace("StreamEntryID [{0}] successfully ACKed", streamEntryID);
            } else {
                log.trace("StreamEntryID [{0}] not ACKed", streamEntryID);
            }
        }
        return successCount;
    }

    /**
     * List stream entries in pending. This is equivalent to redis console:
     * 
     * <pre>
     * XPENDING streamKey group from to pendingCount
     * </pre>
     * 
     * @param pendingCount
     *            pending count limit
     * @param from
     *            entry id, can be null
     * @param to
     *            entry id, can be null. Result include this value
     * @return pending entries
     * @throws BaseException
     *             Exception
     */
    public Optional<List<StreamPendingEntry>> pending(int pendingCount, StreamEntryID from, StreamEntryID to) throws BaseException {
        try (RedisManagerConnection ignored = getRedisManager().initConnection()) {
            return pendingInCurrentConnection(pendingCount, from, to);
        }
    }

    private Optional<List<StreamPendingEntry>> pendingInCurrentConnection(int pendingCount, StreamEntryID from, StreamEntryID to)
            throws BaseException {
        XPendingParams params = new XPendingParams(from, to, pendingCount);
        return getRedisManager().run(Jedis::xpending, "xpending", streamKey(), getGroup(), params);
    }

    /**
     * List the stream entries that were read without ACK and expired in durationg. This is equivalent to redis console:
     * 
     * <pre>
     * XPENDING streamKey group - now-expiry pendingCount
     * </pre>
     * 
     * @param pendingCount
     *            pending count limit
     * @param expiryDuration
     *            expiry duration, null value return empty list
     * @return expired pending entries
     * @throws BaseException
     *             Exception
     */
    public Optional<List<StreamPendingEntry>> pendingExpired(int pendingCount, Duration expiryDuration) throws BaseException {
        if (expiryDuration == null) {
            return Optional.empty();
        }
        return pending(pendingCount, null, LocalDateTime.now().minus(expiryDuration));
    }

    /**
     * Clear expired pending stream entries. This is equivalent to redis console:
     * 
     * <pre>
     * XPENDING streamKey group - now-expiry pendingCount
     * # loop for every entry
     * XACK streamKey group StreamEntryID
     * # until entry is zero
     * </pre>
     * 
     * @param expiryDuration
     *            expiry duration
     * @return removed entries count
     * @throws BaseException
     *             Exception
     */
    public long removeExpiredPendingEntries(Duration expiryDuration) throws BaseException {
        if (expiryDuration == null) {
            return 0;
        }
        long removedEntries = 0;
        boolean again;

        try (RedisManagerConnection ignored = getRedisManager().initConnection()) {
            do {
                List<StreamPendingEntry> pendingEntries = pendingInCurrentConnection(EXPIRED_MESSAGE_CLEANUP_BLOCK_SIZE, null,
                        toStreamEntryID(LocalDateTime.now().minus(expiryDuration))).orElseGet(List::of);
                for (StreamPendingEntry pendingEntry : pendingEntries) {
                    StreamEntryID id = pendingEntry.getID();
                    ackInCurrentConnection(id);
                }
                removedEntries += pendingEntries.size();
                again = pendingEntries.size() >= EXPIRED_MESSAGE_CLEANUP_BLOCK_SIZE;
            } while (again);
        }
        return removedEntries;
    }

    /**
     * List stream entries in pending
     * 
     * @param pendingCount
     *            pending count limit
     * @param from
     *            date type, can be null
     * @param to
     *            date type, can be null. Result include this value
     * @return pending entries
     * @throws BaseException
     *             Exception
     */
    public Optional<List<StreamPendingEntry>> pending(int pendingCount, LocalDateTime from, LocalDateTime to) throws BaseException {
        return pending(pendingCount, toStreamEntryID(from), toStreamEntryID(to));
    }

    /**
     * List stream entries in pending
     * 
     * @param pendingCount
     *            pending count limit
     * @return pending entries
     * @throws BaseException
     *             Exception
     */
    public Optional<List<StreamPendingEntry>> pending(int pendingCount) throws BaseException {
        return pending(pendingCount, (StreamEntryID) null, (StreamEntryID) null);
    }

    /**
     * Convert Date types to StreamEntryID
     * 
     * @param localDateTime
     *            date
     * @return new StreamEntryID
     */
    public static StreamEntryID toStreamEntryID(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return new StreamEntryID(localDateTime.toEpochSecond(ZoneOffset.UTC), 0);
    }

    /**
     * Returns the redis manager
     *
     * @return the redis manager
     */
    public RedisManager getRedisManager() {
        return redisManager;
    }

    /**
     * Sets the redis manager
     *
     * @param redisManager
     *            the new redis manager
     */
    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    /**
     * Returns the redis stream group
     * 
     * @return the redis stream group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the redis stream group
     * 
     * @param group
     *            the new redis stream group
     */
    public void setGroup(String group) {
        this.group = group;
        config.setConfigKey(group);
    }

    /**
     * Get CDI instance
     * 
     * @return Dependent instance of RedisStreamService
     */
    public static Instance<RedisStreamService> instance() {
        return CDI.current().select(RedisStreamService.class);
    }
}
