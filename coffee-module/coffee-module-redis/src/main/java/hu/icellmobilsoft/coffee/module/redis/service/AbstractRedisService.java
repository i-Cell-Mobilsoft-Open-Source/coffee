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
package hu.icellmobilsoft.coffee.module.redis.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.repository.RedisRepository;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

/**
 * Abstract class for redis repository service. Main target is use multiple Redis connection (for cache, for authentication, for other businness
 * logic)
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class AbstractRedisService {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(AbstractRedisService.class);

    /**
     * <p>getRedisData.</p>
     */
    @SuppressWarnings("unchecked")
    public <T> T getRedisData(String redisKey, Class<T> c) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty!");
        }

        RedisRepository redisRepository = new RedisRepository(getJedis());
        String redisDataString = redisRepository.get(redisKey);
        if (StringUtils.isBlank(redisDataString)) {
            throw new BONotFoundException("Redis data not found for key [" + redisKey + "]!");
        }

        if (c == String.class) {
            return (T) redisDataString;
        } else {
            T redisData = JsonUtil.toObject(redisDataString, c);
            if (redisData == null) {
                throw new BONotFoundException("Invalid dedis data found for key [" + redisKey + "] and type [" + c.getSimpleName() + "]!");
            }

            return redisData;
        }
    }

    /**
     * {@link #getRedisData(String, Class)} eredménye, csak BONotFOundException helyett emptyOptional-t ad vissza.
     *
     * @param redisKey
     * @param c
     * @param <T>
     * @throws BaseException
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getRedisDataOpt(String redisKey, Class<T> c) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty!");
        }

        RedisRepository redisRepository = new RedisRepository(getJedis());
        String redisDataString = redisRepository.get(redisKey);
        if (StringUtils.isBlank(redisDataString)) {
            return Optional.empty();
        }

        if (c == String.class) {
            return Optional.of((T) redisDataString);
        } else {
            return Optional.ofNullable(JsonUtil.toObject(redisDataString, c));
        }
    }

    /**
     * <p>setRedisData.</p>
     */
    public <T> String setRedisData(String redisKey, int secondsToExpire, T redisData) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }

        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }

        String redisDataString = JsonUtil.toJson(redisData);
        RedisRepository redisRepository = new RedisRepository(getJedis());
        String result = redisRepository.setex(redisKey, secondsToExpire, redisDataString);
        LOGGER.trace("Redis key [{0}] value [{1}] expire [{2}] setted", redisKey, redisData, secondsToExpire);
        return result;
    }

    /**
     * <p>removeRedisData.</p>
     */
    public void removeRedisData(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }

        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long count = redisRepository.del(redisKey);
        LOGGER.trace("Redis key [{0}] remove count: [{1}]", redisKey, count);
    }

    /**
     * <p>removeAllRedisData.</p>
     */
    public void removeAllRedisData(List<String> redisKeys) throws BaseException {
        if (redisKeys == null || redisKeys.isEmpty()) {
            throw new BONotFoundException("Redis key is empty.");
        }

        String[] keys = new String[redisKeys.size()];
        keys = redisKeys.toArray(keys);
        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long count = redisRepository.del(keys);
        LOGGER.trace("Redis key remove count: {0}", count);
    }

    /**
     * Redis data mar a raw data ami megy a redisbe, rpush nal mar nem kell convertalni
     *
     * @param redisKey
     * @param redisData
     * @param secondsToExpire
     * @throws BaseException
     */
    public void rpushRedisData(String redisKey, String redisData, int secondsToExpire) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        // ures stringet adhatunk a redisnek csak a null check szükséges
        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        redisRepository.rpush(redisKey, redisData, secondsToExpire);
        LOGGER.trace("Redis key [{0}] value [{1}] expire [{2}] pushed", redisKey, redisData, secondsToExpire);
    }

    /**
     * <p>listRedisData.</p>
     */
    public List<String> listRedisData(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return redisRepository.lrange(redisKey);
    }

    /**
     * <p>expireRedisData.</p>
     */
    public void expireRedisData(String redisKey, int seconds) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty. key: " + redisKey);
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long result = redisRepository.expire(redisKey, seconds);
        if (result == null || result.intValue() != 1) {
            throw new BONotFoundException("Redis data by key: [" + redisKey + "] not found! Result: [" + result + "]");
        }
    }

    /**
     * <p>removeAllRedisData.</p>
     *
     * @see Jedis#flushDB()
     */
    public String removeAllRedisData() {
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return redisRepository.flushDB();
    }

    /**
     * <p>setnxRedisData.</p>
     *
     * @see Jedis#setnx(String, int, String)
     * @param redisKey
     * @param redisData
     * @param secondsToExpire
     */
    public Long setnxRedisData(String redisKey, String redisData, int secondsToExpire) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        // ures stringet adhatunk a redisnek csak a null check szükséges
        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return redisRepository.setnx(redisKey, redisData, secondsToExpire);
    }

    /**
     * Get info from Redis
     *
     * @throws BaseException
     */
    public String redisInfo() throws BaseException {
        RedisRepository repository = new RedisRepository(getJedis());
        return repository.info();
    }

    /**
     * Get section info from Redis
     *
     * @see <a href="https://redis.io/commands/INFO">https://redis.io/commands/INFO</a>
     * @param section
     *            available: server, clients, memory, persistence, stats, replication, cpu, commandstats, cluster, keyspace
     * @throws BaseException
     */
    public String redisInfo(String section) throws BaseException {
        RedisRepository repository = new RedisRepository(getJedis());
        return repository.info(section);
    }

    /**
     * <p>hsetnxRedisData.</p>
     *
     * @see Jedis#hsetnx(String, String, String)
     * @param redisKey
     * @param field
     * @param redisData
     * @param secondsToExpire
     */
    public Long hsetnxRedisData(String redisKey, String field, String redisData, int secondsToExpire) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        // ures stringet adhatunk a redisnek csak a null check szükséges
        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }
        RedisRepository repository = new RedisRepository(getJedis());
        Long result = repository.hsetnx(redisKey, field, redisData, secondsToExpire);
        return result;
    }

    /**
     * <p>hscanRedisData.</p>
     *
     * @see Jedis#hscan(String, String)
     * @param redisKey
     * @param secondsToExpire
     */
    public List<Map.Entry<String, String>> hscanRedisData(String redisKey, int secondsToExpire) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository repository = new RedisRepository(getJedis());
        ScanResult<Map.Entry<String, String>> result = repository.hscan(redisKey, RedisRepository.CURSOR_0, secondsToExpire);
        return result.getResult();
    }

    /**
     * <p>setRedisData.</p>
     *
     * @see Jedis#set(String, String) ()
     */
    public <T> String setRedisData(String redisKey, T redisData) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }

        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }

        String redisDataString = JsonUtil.toJson(redisData);
        RedisRepository redisRepository = new RedisRepository(getJedis());
        String result = redisRepository.set(redisKey, redisDataString);
        LOGGER.trace(MessageFormat.format("Redis key [{0}] value [{1}] setted", redisKey, redisData));
        return result;
    }

    /**
     * <p>getJedis.</p>
     */
    protected abstract Jedis getJedis();
}
