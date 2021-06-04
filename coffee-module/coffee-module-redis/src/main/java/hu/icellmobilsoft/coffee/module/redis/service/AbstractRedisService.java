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
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.args.ListDirection;

/**
 * Abstract class for redis repository service. Main target is use multiple Redis connection (for cache, for authentication, for other business logic)
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class AbstractRedisService {

    private static Logger log = Logger.getLogger(AbstractRedisService.class);

    /**
     * Returns data of given key from {@link RedisRepository}.
     *
     * @param redisKey
     *            key of redis data to find
     * @param c
     *            return object class
     * @param <T>
     *            return object type
     * @return redis data
     * @throws BONotFoundException
     *             if invalid parameters, data not found or data is invalid
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
     * Returns data of given key from {@link RedisRepository}. Same as {@link #getRedisData(String, Class)}, but returns an empty {@link Optional}
     * instead of {@link BONotFoundException}.
     *
     * @param redisKey
     *            key of redis data to find
     * @param c
     *            return object class
     * @param <T>
     *            return object type
     * @return redis data or empty {@code Optional}
     * @throws BONotFoundException
     *             if key param is empty
     * @see #getRedisData(String, Class)
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
     * Sets data with given key to {@link RedisRepository} and sets a timeout on the specified key.
     *
     * @param redisKey
     *            key to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @param redisData
     *            data to set
     * @param <T>
     *            data object type
     * @return status code reply
     * @throws BONotFoundException
     *             if key or data param is empty
     * @see RedisRepository#setex(String, long, String)
     */
    public <T> String setRedisData(String redisKey, long secondsToExpire, T redisData) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }

        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }

        String redisDataString = JsonUtil.toJson(redisData);
        RedisRepository redisRepository = new RedisRepository(getJedis());
        String result = redisRepository.setex(redisKey, secondsToExpire, redisDataString);
        log.trace("Redis key [{0}] value [{1}] expire [{2}] setted", redisKey, redisData, secondsToExpire);
        return result;
    }

    /**
     * Removes the specified key. If the given key does not exist no operation is performed for this key.
     *
     * @param redisKey
     *            key to delete
     * @throws BONotFoundException
     *             if key param is empty
     * @see Jedis#del(String...)
     */
    public void removeRedisData(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }

        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long count = redisRepository.del(redisKey);
        log.trace("Redis key [{0}] remove count: [{1}]", redisKey, count);
    }

    /**
     * Removes the specified Redis keys. If a given key does not exist no operation is performed for this key.
     *
     * @param redisKeys
     *            keys to delete
     * @throws BONotFoundException
     *             if key param is empty
     * @see Jedis#del(String...)
     */
    public void removeAllRedisData(List<String> redisKeys) throws BaseException {
        if (redisKeys == null || redisKeys.isEmpty()) {
            throw new BONotFoundException("Redis key is empty.");
        }

        String[] keys = new String[redisKeys.size()];
        keys = redisKeys.toArray(keys);
        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long count = redisRepository.del(keys);
        log.trace("Redis key remove count: {0}", count);
    }

    /**
     * Remove the all occurrences of the value element from the list.
     * 
     * @param listKey
     *            key of list
     * @param value
     *            value to remove
     * @throws BaseException
     *             if input param is empty
     * @see Jedis#lrem(String, long, String)
     */
    public void removeValueFromList(String listKey, String value) throws BaseException {
        if (StringUtils.isAnyBlank(listKey, value)) {
            throw new BONotFoundException("Redis list key or value is empty.");
        }

        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long count = redisRepository.lrem(listKey, 0, value);
        log.trace("Removed [{0}] values from list key: [{1}]", count, listKey);
    }

    /**
     * Adds given redis data to the tail of the list stored at key then sets a timeout on the specified key. If the key does not exist an empty list
     * is created just before the append operation.
     *
     * @param redisKey
     *            redis key to set session id for
     * @param redisData
     *            redis data to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return number of element in key list
     * @throws BONotFoundException
     *             if key param is empty or data param is null
     * @see Jedis#rpush(String, String...)
     */
    public Long rpushRedisData(String redisKey, String redisData, long secondsToExpire) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        // ures stringet adhatunk a redisnek csak a null check szükséges
        if (redisData == null) {
            throw new BONotFoundException("Data to store in redis is null!");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        Long result = redisRepository.rpush(redisKey, redisData, secondsToExpire);
        log.trace("Redis key [{0}] value [{1}] result [{2}] expire [{3}] pushed", redisKey, redisData, result, secondsToExpire);
        return result;
    }

    /**
     * Atomically return and remove the first (LPOP) element of the list. For example if the list contains the elements "a","b","c" LPOP will return
     * "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @param redisKey
     *            key of list
     * 
     * @return next first value from list
     * @throws BONotFoundException
     *             if key param is empty or list is empty
     * @see Jedis#lpop(String)
     */
    public String lpopRedisData(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        String result = redisRepository.lpop(redisKey);
        if (result == null) {
            throw new BONotFoundException("Redis data by key: [" + redisKey + "] not found!");
        }
        return result;
    }

    /**
     * Atomically return and remove the first (LPOP) element of the list. For example if the list contains the elements "a","b","c" LPOP will return
     * "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @param redisKey
     *            key of list
     * 
     * @return next first value from list
     * @throws BaseException
     *             if technical error occured
     * @see Jedis#lpop(String)
     */
    public Optional<String> lpopRedisDataOpt(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            return Optional.empty();
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return Optional.ofNullable(redisRepository.lpop(redisKey));
    }

    /**
     * Atomically return and remove the last (RPOP) element of the list. For example if the list contains the elements "a","b","c" RPOP will return
     * "c" and the list will become "a","b".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @param redisKey
     *            key of list
     * 
     * @return next last value from list
     * @throws BONotFoundException
     *             if key param is empty or list is empty
     * @see Jedis#rpop(String)
     */
    public String rpopRedisData(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        String result = redisRepository.rpop(redisKey);
        if (result == null) {
            throw new BONotFoundException("Redis data by key: [" + redisKey + "] not found!");
        }
        return result;
    }

    /**
     * Atomically return and remove the last (RPOP) element of the list. For example if the list contains the elements "a","b","c" RPOP will return
     * "c" and the list will become "a","b".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @param redisKey
     *            key of list
     * 
     * @return next last value from list
     * @throws BaseException
     *             if technical error occured
     * @see Jedis#rpop(String)
     */
    public Optional<String> rpopRedisDataOpt(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            return Optional.empty();
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return Optional.ofNullable(redisRepository.rpop(redisKey));
    }

    /**
     * Pop an element from a list, push it to another list and return it
     * 
     * @param sourceKey
     *            source list key
     * @param destinationKey
     *            destination list key
     * @param from
     *            LEFT or RIGHT POP from source list
     * @param to
     *            LEFT or RIGHT POP to destination list
     * @return moved value
     * @throws BONotFoundException
     *             if key param is empty or source list is empty
     * @see Jedis#lmove(String, String, ListDirection, ListDirection)
     */
    public String lmoveRedisData(String sourceKey, String destinationKey, ListDirection from, ListDirection to) throws BaseException {
        if (StringUtils.isAnyBlank(sourceKey, destinationKey) || from == null || to == null) {
            throw new BONotFoundException("One or more input from sourceKey, destinationKey, from, to is empty.");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        String result = redisRepository.lmove(sourceKey, destinationKey, from, to);
        if (result == null) {
            throw new BONotFoundException("Data in sourceKey: [" + sourceKey + "] not found!");
        }
        return result;
    }

    /**
     * Pop an element from a list, push it to another list and return it
     * 
     * @param sourceKey
     *            source list key
     * @param destinationKey
     *            destination list key
     * @param from
     *            LEFT or RIGHT POP from source list
     * @param to
     *            LEFT or RIGHT POP to destination list
     * @return moved value
     * @throws BaseException
     *             if technical error occured
     * @see Jedis#lmove(String, String, ListDirection, ListDirection)
     */
    public Optional<String> lmoveRedisDataOpt(String sourceKey, String destinationKey, ListDirection from, ListDirection to) throws BaseException {
        if (StringUtils.isAnyBlank(sourceKey, destinationKey) || from == null || to == null) {
            throw new BONotFoundException("One or more input from sourceKey, destinationKey, from, to is empty.");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return Optional.ofNullable(redisRepository.lmove(sourceKey, destinationKey, from, to));
    }

    /**
     * Returns all elements of the list stored at the specified Redis key.
     *
     * @param redisKey
     *            key to return elements of
     * @return {@link List} of elements with the given key
     * @throws BONotFoundException
     *             if key param is empty
     * @see Jedis#lrange(String, long, long)
     */
    public List<String> listRedisData(String redisKey) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return redisRepository.lrange(redisKey);
    }

    /**
     * Sets a timeout on the specified Redis key. After the timeout the key will be automatically deleted by the server.
     *
     * @param redisKey
     *            key to expire
     * @param seconds
     *            timeout given in seconds
     * @throws BONotFoundException
     *             if key param is empty, or result of expire command is not 1
     * @see Jedis#expire(String, long)
     */
    public void expireRedisData(String redisKey, long seconds) throws BaseException {
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
     * Deletes all the keys of the currently selected DB.
     *
     * @return OK
     * @see Jedis#flushDB()
     */
    public String removeAllRedisData() {
        RedisRepository redisRepository = new RedisRepository(getJedis());
        return redisRepository.flushDB();
    }

    /**
     * Sets given Redis key to hold given value if key does not exist, then sets a timeout on the specified key.
     *
     * @param redisKey
     *            key to set value for
     * @param redisData
     *            value to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return 1 if the key was set successfully; 0 if the key was not set (key already exists)
     * @throws BONotFoundException
     *             if key or value is empty
     * @see Jedis#setnx(String, String)
     */
    public Long setnxRedisData(String redisKey, String redisData, long secondsToExpire) throws BaseException {
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
     * Returns info from Redis server.
     *
     * @return Redis server info
     * @throws BaseException
     *             exception
     * @see Jedis#info()
     */
    public String redisInfo() throws BaseException {
        RedisRepository repository = new RedisRepository(getJedis());
        return repository.info();
    }

    /**
     * Returns section info from Redis server.
     *
     * @param section
     *            available: server, clients, memory, persistence, stats, replication, cpu, commandstats, cluster, keyspace
     * @return Redis server info section
     * @throws BaseException
     *             exception
     * @see Jedis#info(String)
     * @see <a href="https://redis.io/commands/INFO">https://redis.io/commands/INFO</a>
     */
    public String redisInfo(String section) throws BaseException {
        RedisRepository repository = new RedisRepository(getJedis());
        return repository.info(section);
    }

    /**
     * Sets the specified hash field to the specified value if the field does not exist.
     *
     * @param redisKey
     *            key to set field for
     * @param field
     *            field to set value for
     * @param redisData
     *            value to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return 1 if the key was set successfully; 0 if the key was not set (key already exists)
     * @throws BONotFoundException
     *             if key param is empty or data param is null
     * @see Jedis#hsetnx(String, String, String)
     */
    public Long hsetnxRedisData(String redisKey, String field, String redisData, long secondsToExpire) throws BaseException {
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
     * Executes Redis HSCAN operation which incrementally iterates over a collection of elements.
     *
     * @param redisKey
     *            redis key
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return hscan result
     * @throws BONotFoundException
     *             if key param is empty
     * @see Jedis#hscan(String, String)
     */
    public List<Map.Entry<String, String>> hscanRedisData(String redisKey, long secondsToExpire) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository repository = new RedisRepository(getJedis());
        ScanResult<Map.Entry<String, String>> result = repository.hscan(redisKey, RedisRepository.CURSOR_0, secondsToExpire);
        return result.getResult();
    }

    /**
     * Executes Redis HSCAN operation which incrementally iterates over a collection of elements.
     *
     * @param redisKey
     *            redis key
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @param count
     *            count param
     * @return hscan result
     * @throws BONotFoundException
     *             if key param is empty
     * @see Jedis#hscan(String, String, ScanParams)
     */
    public List<Map.Entry<String, String>> hscanRedisData(String redisKey, long secondsToExpire, int count) throws BaseException {
        if (StringUtils.isBlank(redisKey)) {
            throw new BONotFoundException("Redis key is empty.");
        }
        RedisRepository repository = new RedisRepository(getJedis());
        ScanResult<Map.Entry<String, String>> result = repository.hscan(redisKey, RedisRepository.CURSOR_0, secondsToExpire,
                new ScanParams().count(count));
        return result.getResult();
    }

    /**
     * Sets data with given key to {@link RedisRepository}.
     *
     * @param redisKey
     *            key to set
     * @param redisData
     *            data to set
     * @param <T>
     *            data object type
     * @return status code reply
     * @throws BONotFoundException
     *             if key or data param is empty
     * @see RedisRepository#setex(String, long, String)
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
        log.trace(MessageFormat.format("Redis key [{0}] value [{1}] setted", redisKey, redisData));
        return result;
    }

    /**
     * Returns {@link Jedis} Redis client instance.
     * 
     * @return {@code Jedis}
     */
    protected abstract Jedis getJedis();
}
