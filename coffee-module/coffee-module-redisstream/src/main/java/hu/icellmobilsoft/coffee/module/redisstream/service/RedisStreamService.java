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

import java.io.Closeable;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamGroupInfo;
import redis.clients.jedis.StreamPendingEntry;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.XAddParams;

/**
 * Service class for redis stream logic
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@Dependent
public class RedisStreamService implements Closeable {

    @Inject
    private Logger log;

    /**
     * {@link StreamGroupConfig#setConfigKey(String)} is setted in {@link #setGroup(String)}
     */
    @Inject
    private StreamGroupConfig config;

    private Jedis jedis;

    private String group;

    /**
     * Stream key, calculated by {@link #group}
     * 
     * @return Sream key
     */
    public String streamKey() {
        return getGroup() + "Stream";
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
     * @return Generated ID
     * @throws BaseException
     *             Exception
     */
    public StreamEntryID publish(Map<String, String> values) throws BaseException {
        if (values == null) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, "publish values is null");
        }
        XAddParams params = XAddParams.xAddParams();
        if (config.getProducerMaxLen().isPresent()) {
            params.maxLen(config.getProducerMaxLen().get());
        }
        if (config.getProducerTTL().isPresent()) {
            StreamEntryID before = new StreamEntryID(Instant.now().minusMillis(config.getProducerTTL().get()).toEpochMilli(), 0);
            params.minId(before.toString());
        }
        StreamEntryID streamEntryID = getJedis().xadd(streamKey(), values, params);
        if (log.isTraceEnabled()) {
            log.trace("Published streamEntryID: [{0}] into [{1}]", streamEntryID, streamKey());
        }
        return streamEntryID;
    }

    /**
     * Count elements in stream. This is equivalent to redis console:
     * 
     * <pre>
     * XLEN streamKey
     * </pre>
     * 
     * @return elements count
     */
    public Long count() {
        Long count = getJedis().xlen(streamKey());
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
     */
    public boolean existGroup() {
        try {
            List<StreamGroupInfo> info = getJedis().xinfoGroup(streamKey());
            return info.stream().map(StreamGroupInfo::getName).anyMatch(name -> StringUtils.equals(getGroup(), name));
        } catch (JedisDataException e) {
            // ha nincs kulcs akkor a kovetkezo hiba jon:
            // redis.clients.jedis.exceptions.JedisDataException: ERR no such key
            log.info("Redis exception duringchecking group [{0}]: [{1}]", streamKey(), e.getLocalizedMessage());
            return false;
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
     */
    public void handleGroup() {
        if (existGroup()) {
            if (log.isTraceEnabled()) {
                log.trace("Group [{0}] already exist", getGroup());
            }
        } else {
            String createGroupResult = getJedis().xgroupCreate(streamKey(), getGroup(), new StreamEntryID(), true);
            log.info("Stream group [{0}] on stream [{1}] created with result: [{2}]", getGroup(), streamKey(), createGroupResult);
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
        Entry<String, StreamEntryID> streamQuery = new AbstractMap.SimpleImmutableEntry<>(streamKey(), StreamEntryID.UNRECEIVED_ENTRY);
        // kepes tobb streambol is egyszerre olvasni, de mi 1-re hasznaljuk
        @SuppressWarnings("unchecked")
        List<Entry<String, List<StreamEntry>>> result = getJedis().xreadGroup(getGroup(), consumerIdentifier, 1, config.getStreamReadTimeoutMillis(),
                false, streamQuery);
        if (result == null || result.isEmpty()) {
            // nincs uj uzenet
            if (log.isTraceEnabled()) {
                log.trace("No new message in [{0}] stream", streamKey());
            }
            return Optional.empty();
        }
        // 1 stream-bol olvasunk
        Entry<String, List<StreamEntry>> stream = result.get(0);
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
            entry.getFields().entrySet().stream().forEach(e -> sb.append("\n    Key[" + e.getKey() + "]: Value[" + e.getValue() + "]"));
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
     */
    public long ack(StreamEntryID streamEntryID) {
        if (Objects.isNull(streamEntryID)) {
            return 0;
        }
        long sucessCount = jedis.xack(streamKey(), getGroup(), streamEntryID);
        if (log.isTraceEnabled()) {
            if (sucessCount > 0) {
                log.trace("StreamEntryID [{0}] sucessfully ACKed", streamEntryID);
            } else {
                log.trace("StreamEntryID [{0}] not ACKed", streamEntryID);
            }
        }
        return sucessCount;
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
     */
    public List<StreamPendingEntry> pending(int pendingCount, StreamEntryID from, StreamEntryID to) {
        return jedis.xpending(streamKey(), getGroup(), from, to, pendingCount, null);
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
     */
    public List<StreamPendingEntry> pendingExpired(int pendingCount, Duration expiryDuration) {
        if (expiryDuration == null) {
            return List.of();
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
     */
    public long removeExpiredPendingEntries(Duration expiryDuration) {
        if (expiryDuration == null) {
            return 0;
        }
        int blockSize = 1000;
        long removedEntries = 0;
        boolean again = false;
        do {
            List<StreamPendingEntry> pendingEntries = pendingExpired(blockSize, expiryDuration);
            pendingEntries.stream().map(StreamPendingEntry::getID).forEach(this::ack);
            removedEntries = removedEntries + pendingEntries.size();
            again = pendingEntries.size() >= blockSize;
        } while (again);
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
     */
    public List<StreamPendingEntry> pending(int pendingCount, LocalDateTime from, LocalDateTime to) {
        return pending(pendingCount, toStreamEntryID(from), toStreamEntryID(to));
    }

    /**
     * List stream entries in pending
     * 
     * @param pendingCount
     *            pending count limit
     * @return pending entries
     */
    public List<StreamPendingEntry> pending(int pendingCount) {
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

    protected Jedis getJedis() {
        return jedis;
    }

    /**
     * Setter for the field {@code jedis}.
     *
     * @param jedis
     *            jedis
     */
    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        if (jedis != null) {
            Jedis connection = jedis;
            connection.close();
            jedis = null;
        }
    }

    public String getGroup() {
        return group;
    }

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
