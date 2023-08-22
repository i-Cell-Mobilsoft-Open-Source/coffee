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

/**
 * Jedis metric constants
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
public interface JedisMetricsConstants {

    /**
     * Jedis metrics config
     */
    interface Tag {

        /**
         * key for redis server connection
         */
        String COFFEE_JEDIS_CONFIG_KEY = "configKey";
        /**
         * key for server pool
         */
        String COFFEE_JEDIS_POOL_CONFIG_KEY = "poolConfigKey";
    }

    /**
     * Jedis metrics description
     */
    interface Description {

        /**
         * Active jedis pool connections
         */
        String COFFEE_JEDIS_POOL_ACTIVE_DESCRIPTION = "Active connection number";

        /**
         * Idle jedis pool connections
         */
        String COFFEE_JEDIS_POOL_IDLE_DESCRIPTION = "Idle connection number";
    }

    /**
     * Gauge constants
     *
     */
    interface Gauge {

        /**
         * Active redis pool
         */
        String COFFEE_JEDIS_POOL_ACTIVE = "coffee_jedis_pool_active";

        /**
         * Idle redis pool
         */
        String COFFEE_JEDIS_POOL_IDLE = "coffee_jedis_pool_idle";

    }
}
