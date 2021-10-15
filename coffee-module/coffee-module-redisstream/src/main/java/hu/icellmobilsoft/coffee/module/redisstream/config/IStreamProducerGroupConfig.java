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
package hu.icellmobilsoft.coffee.module.redisstream.config;

import java.util.Optional;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Redis producer stream group configuration interface
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public interface IStreamProducerGroupConfig {


    /**
     * Max elements in stream, oldest will be removed. See https://redis.io/commands/xadd MAXLEN parameter. <br>
     * <br>
     * This parameter has higher priority than {@link #getProducerTTL()}, if both setted, this parameter is applied and {@link #getProducerTTL()}
     * ignored.
     *
     * @return Max elements in stream
     * @throws BaseException
     *             Exception on read properties
     */
    Optional<Long> getProducerMaxLen() throws BaseException;

    /**
     * Millisec TTL. When a new entry is produced, all old entries are deleted which have identifier time older than (sysdate - millisecond). See
     * https://redis.io/commands/xadd MINID parameter. <br>
     * <br>
     * The parameter {@link #getProducerMaxLen()} has higher priority, if both setted, this parameter is ignored.
     *
     * @return millisec ttl
     * @throws BaseException
     *             Exception on read properties
     */
    Optional<Long> getProducerTTL() throws BaseException;


    /**
     * Enable/Disable any part of stream function for any reason. This is only for project logic, no have direct use-case in coffee
     *
     * @return true - enabled, default
     * @throws BaseException
     *             if any exception occurs
     */
    boolean isEnabled() throws BaseException;

}
