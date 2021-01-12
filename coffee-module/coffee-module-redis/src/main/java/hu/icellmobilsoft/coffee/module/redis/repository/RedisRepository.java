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
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

/**
 * Redis java Jedis client functions handler
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class RedisRepository {

    public interface RedisResultCode {
        Long SUCCESSFUL = 1L;
        Long UNSUCCESSFUL = 0L;
    }

    /** Constant <code>CURSOR_0="0"</code> */
    public final static String CURSOR_0 = "0";

    private Jedis jedis;

    /**
     * <p>Constructor for RedisRepository.</p>
     */
    public RedisRepository(Jedis jedis) {
        super();
        this.jedis = jedis;
    }

    /**
     * <p>setex.</p>
     *
     * @see Jedis#setex(String, int, String)
     * @param key
     * @param secondsToExpire
     * @param json
     */
    public String setex(final String key, final int secondsToExpire, final String json) {
        return getJedis().setex(key, secondsToExpire, json);
    }

    /**
     * <p>rpush.</p>
     *
     * @see Jedis#rpush(String, String)
     * @param customerId
     * @param sessionId
     */
    public void rpush(final String key, final String sessionId, int secondsToExpire) {
        getJedis().rpush(key, sessionId);
        getJedis().expire(key, secondsToExpire);
    }

    /**
     * <p>lrange.</p>
     *
     * @see Jedis#lrange(String, long, long)
     * @param key
     */
    public List<String> lrange(final String key) {
        return getJedis().lrange(key, 0, -1);
    }

    /**
     * <p>get.</p>
     *
     * @see Jedis#get(String)
     * @param key
     */
    public String get(final String key) {
        return getJedis().get(key);
    }

    /**
     * <p>getMap.</p>
     *
     * @see Jedis#hgetAll(String)
     * @param key
     */
    public Map<String, String> getMap(final String key) {
        return getJedis().hgetAll(key);
    }

    /**
     * <p>del.</p>
     *
     * @see Jedis#hgetAll(String...)
     * @param keys
     */
    public Long del(final String... keys) {
        return getJedis().del(keys);
    }

    /**
     * <p>expire.</p>
     *
     * @see Jedis#expire(String, int)
     * @param key
     * @param seconds
     */
    public Long expire(final String key, int seconds) {
        return getJedis().expire(key, seconds);
    }

    /**
     * <p>info.</p>
     *
     * @see Jedis#info()
     */
    public String info() {
        // https://redis.io/commands/INFO
        // https://ma.ttias.be/redis-oom-command-not-allowed-used-memory-maxmemory/
        return getJedis().info();
    }

    /**
     * <p>info.</p>
     *
     * @param section
     * @see Jedis#info(String)
     */
    public String info(String section) {
        return getJedis().info(section);
    }

    /**
     * <p>set.</p>
     *
     * @see Jedis#set(String, String)
     * @param key
     * @param json
     */
    public String set(final String key, final String json) {
        return getJedis().set(key, json);
    }

    /**
     * <p>setnx.</p>
     *
     * @see Jedis#setnx(String, int, String)
     * @param key
     * @param value
     * @param secondsToExpire
     */
    public Long setnx(String key, String value, int secondsToExpire) {
        Long result = getJedis().setnx(key, value);
        if (RedisResultCode.SUCCESSFUL.equals(result)) {
            getJedis().expire(key, secondsToExpire);
        }
        return result;
    }

    /**
     * <p>hsetnx.</p>
     *
     * @see Jedis#hsetnx(String, String, String)
     * @param key
     * @param field
     * @param value
     * @param secondsToExpire
     */
    public Long hsetnx(String key, String field, String value, int secondsToExpire) {
        Long result = getJedis().hsetnx(key, field, value);
        getJedis().expire(key, secondsToExpire);
        return result;
    }

    /**
     * <p>hscan.</p>
     * 
     * @see Jedis#hscan(String, String)
     * @param key redis key
     * @param cursor cursor
     * @param secondsToExpire timeout on the key
     * @return scan result
     */
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, int secondsToExpire) {
        ScanResult<Map.Entry<String, String>> result = getJedis().hscan(key, cursor);
        getJedis().expire(key, secondsToExpire);
        return result;
    }
    
    /**
     * <p>hscan.</p>
     * 
     * @see Jedis#hscan(String, String, ScanParams)
     * @param key redis key
     * @param cursor cursor
     * @param secondsToExpire timeout on the key
     * @param scanParams param use on hscan
     * @return hscan result
     */
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, int secondsToExpire, ScanParams scanParams) {
        ScanResult<Map.Entry<String, String>> result = getJedis().hscan(key, cursor, scanParams);
        getJedis().expire(key, secondsToExpire);
        return result;
    }
    
    /**
     * <p>flushDB.</p>
     *
     * @see Jedis#flushDB()
     */
    public String flushDB() {
        return getJedis().flushDB();
    }

    /**
     * <p>Getter for the field <code>jedis</code>.</p>
     */
    public Jedis getJedis() {
        return jedis;
    }

    /**
     * <p>Setter for the field <code>jedis</code>.</p>
     */
    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }
}
