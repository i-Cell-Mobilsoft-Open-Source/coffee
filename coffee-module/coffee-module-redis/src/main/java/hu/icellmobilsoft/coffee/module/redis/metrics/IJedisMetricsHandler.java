/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.metrics;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import redis.clients.jedis.JedisPool;

/**
 * Jedis metric handler interface
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
public interface IJedisMetricsHandler {

    /**
     * Provides metrics for the specified connection pool.
     * 
     * @param configKey
     *            Redis connection config key
     * @param poolConfigKey
     *            Redis connection pool config key
     * @param jedisPool
     *            {@link JedisPool} handle connection
     * @throws BaseException
     *             if wrong config provided
     */
    void addMetric(String configKey, String poolConfigKey, JedisPool jedisPool) throws BaseException;
}
