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

import java.io.Closeable;

import javax.enterprise.context.Dependent;

import redis.clients.jedis.Jedis;

/**
 * Managed impl for AbstractRedisService.
 *
 * @author mark.petrenyi
 * @see hu.icellmobilsoft.coffee.module.redis.producer.RedisServiceProducer
 * @since 1.0.0
 */
@Dependent
public class RedisService extends AbstractRedisService implements Closeable {

    private Jedis jedis;

    /** {@inheritDoc} */
    @Override
    protected Jedis getJedis() {
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

    /** {@inheritDoc} */
    @Override
    public void close() {
        if (jedis != null) {
            Jedis connection = jedis;
            connection.close();
            jedis = null;
        }
    }
}
