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

import hu.icellmobilsoft.coffee.dto.common.LogConstants;

/**
 * Redis stream constants
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public interface IRedisStreamConstant {

    /**
     * Logging keys in MDC
     * 
     * @author Imre Scheffer
     *
     * @since 1.5.0
     */
    public interface Log {
        /**
         * Key for redis stream retry counter
         */
        String RETRY_COUNTER = "retryCounter";
    }

    /**
     * Common values
     * 
     * @author imre.scheffer
     * @since 1.3.0
     */
    public interface Common {
        /**
         * Key for redis stream message data
         */
        String DATA_KEY_MESSAGE = "message";
        /**
         * Key for redis stream message flow ID
         */
        String DATA_KEY_FLOW_ID = LogConstants.LOG_SESSION_ID;
        /**
         * Key for redis stream message timeout in epoch time
         */
        String DATA_KEY_TTL = "ttl";
        /**
         * {@code #DATA_KEY_FLOW_ID} message extension for logging searching
         */
        String FLOW_ID_EXTENSION = "flowIdExtension";
        /**
         * Key for redis stream message data
         */
        String DATA_MESSAGE_TYPE = "messageType";
    }

    /**
     * Project PIPE stream values
     */
    public interface Pipe {
        /**
         * Prefix key for redis stream list key
         */
        String ID = "id_";
    }

    /**
     * Default values
     */
    public interface Defaults {
        /**
         * {@link #STREAM_READ_MAXIMUM_LATENCY_SECONDS_DEFAULT} Default value is 5 minutes
         */
        long STREAM_READ_MAXIMUM_LATENCY_SECONDS_DEFAULT = 300;

    }
}
