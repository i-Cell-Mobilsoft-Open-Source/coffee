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
package hu.icellmobilsoft.coffee.module.redis.config;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * REDIS configuration values
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface RedisConfig {

    /**
     * Returns Redis host.
     *
     * @return host
     * @throws BaseException
     *             exception
     */
    String getHost() throws BaseException;

    /**
     * Returns Redis post.
     *
     * @return post
     * @throws BaseException
     *             exception
     */
    Integer getPort() throws BaseException;

    /**
     * Returns Redis password.
     * 
     * @return password
     * @throws BaseException
     *             exception
     */
    String getPassword() throws BaseException;

    /**
     * The maximum number of objects that can be allocated by the pool (checked out to clients, or idle awaiting checkout) at a given time. When
     * negative, there is no limit to the number of objects that can be managed by the pool at one time.
     *
     * @param poolKey
     *            key.
     * @return max pool instances
     * @throws BaseException
     *             exception
     */
    Integer getPoolMaxTotal(String poolKey) throws BaseException;

    /**
     * The cap on the number of "idle" instances in the pool. If maxIdle is set too low on heavily loaded systems it is possible you will see objects
     * being destroyed and almost immediately new objects being created. This is a result of the active threads momentarily returning objects faster
     * than they are requesting them, causing the number of idle objects to rise above maxIdle.
     *
     * @param poolKey
     *            key.
     * @return max idle instances
     * @throws BaseException
     *             exception
     */
    Integer getPoolMaxIdle(String poolKey) throws BaseException;

    /**
     * The number of the selected database to connect with, default value: 0 (According to Redis settings).
     *
     * @return database
     * @throws BaseException
     *             exception
     */
    default Integer getDatabase() throws BaseException {
        return 0;
    }

    /**
     * Redis connection timeout in milliseconds with default value 5000.
     *
     * @return connection timeout
     * @throws BaseException
     *             exception
     */
    default Integer getTimeout() throws BaseException {
        return 5000;
    }
}
