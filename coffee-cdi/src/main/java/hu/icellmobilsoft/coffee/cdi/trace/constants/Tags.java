/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.cdi.trace.constants;

/**
 * 
 * OpenTracing Tags (io.opentracing.tag.Tags) Coffee Tag representation
 * 
 * @author czenczl
 * @since 1.7.0
 *
 */
public interface Tags {

    /**
     * Default redis trace Tags
     */
    interface Redis {

        /**
         * Redis db type trace tag ({@value})
         */
        String DB_TYPE = "redis";

        /**
         * Redis Stream trace keys
         */
        interface Stream {
            /**
             * Redis trace kind tag ({@value})
             */
            String KIND = "consumer";
            /**
             * Redis trace component tag ({@value})
             */
            String COMPONENT = "redis-stream";
        }

        /**
         * Redis Jedis trace keys
         */
        interface Jedis {
            /**
             * Redis trace KIND tag ({@value})
             */
            String KIND = "client";
            /**
             * Redis trace component tag ({@value})
             */
            String COMPONENT = "jedis";
        }

    }

}
