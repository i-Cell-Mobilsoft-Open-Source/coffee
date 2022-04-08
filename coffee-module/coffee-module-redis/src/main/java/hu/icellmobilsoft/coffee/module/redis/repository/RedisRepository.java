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
package hu.icellmobilsoft.coffee.module.redis.repository;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

/**
 * Redis java Jedis client functions handler
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class RedisRepository {

    /**
     * Redis response codes
     */
    public interface RedisResultCode {
        /**
         * Successful action result code
         */
        Long SUCCESSFUL = 1L;

        /**
         * Unsuccessful action result code
         */
        Long UNSUCCESSFUL = 0L;
    }

    /** Constant <code>CURSOR_0="0"</code> */
    public static final String CURSOR_0 = "0";

    private Jedis jedis;

    /**
     * One arg constructor which sets {@link Jedis} redis client.
     *
     * @param jedis
     *            {@code Jedis} redis client to set
     */
    public RedisRepository(Jedis jedis) {
        super();
        this.jedis = jedis;
    }

    /**
     * Sets given JSON value as value of the key and sets a timeout on the specified key. Atomic equivalent of SET + EXPIRE commands.
     *
     * @param key
     *            key to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @param json
     *            JSON to set as value
     * @return status code reply
     * @see Jedis#setex(String, long, String)
     */
    public String setex(final String key, final long secondsToExpire, final String json) {
        return getJedis().setex(key, secondsToExpire, json);
    }

    /**
     * Adds given session id to the tail of the list stored at key then sets a timeout on the specified key. If the key does not exist an empty list
     * is created just before the append operation.
     *
     * @param key
     *            key to set elementValue for
     * @param elementValue
     *            element value to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return number of element in key list
     *
     * @see Jedis#rpush(String, String...)
     */
    public Long rpush(final String key, final String elementValue, long secondsToExpire) {
        Long result = getJedis().rpush(key, elementValue);
        getJedis().expire(key, secondsToExpire);
        return result;
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example if the list contains the elements "a","b","c"
     * LPOP will return "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @param key
     *            key of list
     * 
     * @return next first value from list
     * @see Jedis#lpop(String)
     */
    public String lpop(final String key) {
        return getJedis().lpop(key);
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example if the list contains the elements "a","b","c"
     * LPOP will return "a" and the list will become "b","c".
     * <p>
     * If the key does not exist or the list is already empty the special value 'nil' is returned.
     * 
     * @param key
     *            key of list
     * 
     * @return next last value from list
     * @see Jedis#rpop(String)
     */
    public String rpop(final String key) {
        return getJedis().rpop(key);
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
     * @param secondsToExpire
     *            timeout on the destinationKey given in seconds
     * @return moved value
     * @see Jedis#lmove(String, String, ListDirection, ListDirection)
     */
    public String lmove(final String sourceKey, final String destinationKey, final ListDirection from, final ListDirection to,
            final long secondsToExpire) {
        String value = getJedis().lmove(sourceKey, destinationKey, from, to);
        getJedis().expire(destinationKey, secondsToExpire);
        return value;
    }

    /**
     * Remove the first count occurrences of the value element from the list. If count is zero all the elements are removed. If count is negative
     * elements are removed from tail to head, instead to go from head to tail that is the normal behaviour. So for example LREM with count -2 and
     * hello as value to remove against the list (a,b,c,hello,x,hello,hello) will leave the list (a,b,c,hello,x). The number of removed elements is
     * returned as an integer, see below for more information about the returned value. Note that non existing keys are considered like empty lists by
     * LREM, so LREM against non existing keys will always return 0.
     * 
     * @param key
     *            key of list
     * @param count
     *            how many hit values to remove, 0 all, -x from the end of the list, +x from the beginning of the list
     * @param value
     *            value to remove
     * @return Integer Reply, specifically: The number of removed elements if the operation succeeded
     * @see Jedis#lrem(String, long, String)
     */
    public Long lrem(final String key, final long count, final String value) {
        return getJedis().lrem(key, count, value);
    }

    /**
     * Returns all elements of the list stored at the specified key.
     *
     * @param key
     *            key to return elements of
     * @return {@link List} of elements with the given key
     * @see Jedis#lrange(String, long, long)
     */
    public List<String> lrange(final String key) {
        return getJedis().lrange(key, 0, -1);
    }

    /**
     * Returns the value of the specified key. If the key does not exist null is returned.
     *
     * @param key
     *            key to return value of
     * @return value of the key or null if key does not exist
     * @see Jedis#get(String)
     */
    public String get(final String key) {
        return getJedis().get(key);
    }

    /**
     * Returns all the hash fields and associated values stored at given key in a {@link Map}.
     *
     * @param key
     *            key to return fields and values of
     * @return list of fields and their values stored in the hash, or an empty list if key does not exist
     * @see Jedis#hgetAll(String)
     */
    public Map<String, String> getMap(final String key) {
        return getJedis().hgetAll(key);
    }

    /**
     * Removes the specified keys. If a given key does not exist no operation is performed for this key.
     *
     * @param keys
     *            keys to delete
     * @return number of keys removed
     * @see Jedis#del(String...)
     */
    public Long del(final String... keys) {
        return getJedis().del(keys);
    }

    /**
     * Sets a timeout on the specified key. After the timeout the key will be automatically deleted by the server.
     *
     * @param key
     *            key to expire
     * @param seconds
     *            timeout given in seconds
     * @return 1, if timeout was set; 0 if key does not exist or timeout was not set successfully
     * @see Jedis#expire(String, long)
     */
    public Long expire(final String key, long seconds) {
        return getJedis().expire(key, seconds);
    }

    /**
     * Returns information and statistics about the server in a format that is simple to parse by computers and easy to read by humans.
     *
     * @return server info
     * @see Jedis#info()
     * @see <a href="https://redis.io/commands/INFO">https://redis.io/commands/INFO</a>
     */
    public String info() {
        // https://redis.io/commands/INFO
        // https://ma.ttias.be/redis-oom-command-not-allowed-used-memory-maxmemory/
        return getJedis().info();
    }

    /**
     * Returns information and statistics about the server info's given section in a format that is simple to parse by computers and easy to read by
     * humans.
     *
     * @param section
     *            section to return info of
     * @return Redis server info section
     * @see Jedis#info(String)
     * @see <a href="https://redis.io/commands/INFO">https://redis.io/commands/INFO</a>
     */
    public String info(String section) {
        return getJedis().info(section);
    }

    /**
     * Set the JSON value as value of the key.
     *
     * @param key
     *            key to set value for
     * @param json
     *            JSON value to set
     * @return status code reply
     * @see Jedis#set(String, String)
     */
    public String set(final String key, final String json) {
        return getJedis().set(key, json);
    }

    /**
     * Sets given key to hold given value if key does not exist, then sets a timeout on the specified key.
     *
     * @param key
     *            key to set value for
     * @param value
     *            value to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return 1 if the key was set successfully; 0 if the key was not set (key already exists)
     * @see Jedis#setnx(String, String)
     */
    public Long setnx(String key, String value, long secondsToExpire) {
        Long result = getJedis().setnx(key, value);
        if (RedisResultCode.SUCCESSFUL.equals(result)) {
            getJedis().expire(key, secondsToExpire);
        }
        return result;
    }

    /**
     * Sets the specified hash field to the specified value if the field does not exist.
     *
     * @param key
     *            key to set field for
     * @param field
     *            field to set value for
     * @param value
     *            value to set
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return 1 if the key was set successfully; 0 if the key was not set (key already exists)
     * @see Jedis#hsetnx(String, String, String)
     */
    public Long hsetnx(String key, String field, String value, long secondsToExpire) {
        Long result = getJedis().hsetnx(key, field, value);
        getJedis().expire(key, secondsToExpire);
        return result;
    }

    /**
     * Executes Redis HSCAN operation which incrementally iterates over a collection of elements.
     *
     * @param key
     *            redis key
     * @param cursor
     *            cursor
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @return hscan result
     * @see Jedis#hscan(String, String)
     */
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, long secondsToExpire) {
        ScanResult<Map.Entry<String, String>> result = getJedis().hscan(key, cursor);
        getJedis().expire(key, secondsToExpire);
        return result;
    }

    /**
     * Executes Redis HSCAN operation which incrementally iterates over a collection of elements.
     *
     * @param key
     *            redis key
     * @param cursor
     *            cursor
     * @param secondsToExpire
     *            timeout on the key given in seconds
     * @param scanParams
     *            params to use on hscan
     * @return hscan result
     * @see Jedis#hscan(String, String, ScanParams)
     */
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, long secondsToExpire, ScanParams scanParams) {
        ScanResult<Map.Entry<String, String>> result = getJedis().hscan(key, cursor, scanParams);
        getJedis().expire(key, secondsToExpire);
        return result;
    }

    /**
     * Deletes all the keys of the currently selected DB.
     *
     * @return OK
     * @see Jedis#flushDB()
     */
    public String flushDB() {
        return getJedis().flushDB();
    }

    /**
     * Getter for the field {@code jedis}.
     *
     * @return {@code jedis}
     */
    public Jedis getJedis() {
        return jedis;
    }

    /**
     * Setter for the field {@code jedis}.
     *
     * @param jedis
     *            jedis to set
     */
    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }
}
