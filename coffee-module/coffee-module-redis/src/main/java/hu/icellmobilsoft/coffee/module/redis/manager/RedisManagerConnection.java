/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.manager;

import java.io.Closeable;

/**
 * Class representing the redis connection created by {@link RedisManager#initConnection}.
 * 
 * @author martin.nagy
 * @since 1.10.0
 */
public class RedisManagerConnection implements Closeable {

    private final RedisManager redisManager;

    /**
     * Constructor to initialize the redisManager field
     * 
     * @param redisManager
     *            redis connection, operation manager object
     */
    public RedisManagerConnection(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    @Override
    public void close() {
        redisManager.closeConnection();
    }
}
