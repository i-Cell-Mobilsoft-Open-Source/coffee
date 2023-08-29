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
package hu.icellmobilsoft.coffee.jpa.health;

/**
 * Database health configuration constants
 * 
 * @author czenczl
 * @since 1.15.0
 *
 */
public interface DatabaseHealthConstant {

    /**
     * Database resource keys
     */
    interface Database {
        /**
         * the default datasource name
         */
        String DEFAULT_DATASOURCE_NAME = "icellmobilsoftDS";

        /**
         * wildfly specific metric keys
         */
        interface Wildfly {
            /**
             * the default datasource prefix
             */
            String DEFAULT_DATASOURCE_PREFIX = "java:jboss/datasources/";

            /**
             * specific metric keys to get information about the datasource connection pool resource usage
             */
            interface Metric {
                /**
                 * wildlfy specific pool in use metric identifier
                 */
                String WILDFLY_DATASOURCES_POOL_IN_USE_COUNT = "wildfly_datasources_pool_in_use_count";
                /**
                 * wildlfy specific pool data source metric identifier
                 */
                String DATA_SOURCE_TAG = "data_source";
            }
        }

        /**
         * database datasource pool keys
         */
        interface Pool {
            /**
             * connection pool max limit
             */
            String DATASOURCE_MAX_POOL_SIZE = "DATASOURCE_MAX_POOL_SIZE";
            /**
             * connection pool usage threshold limit in percent
             */
            String DATASOURCE_POOL_USAGE_TRESHOLD_PERCENT = "DATASOURCE_POOL_USAGE_TRESHOLD_PERCENT";
            /**
             * the pool actual usage value in percent
             */
            String DATASOURCE_POOL_USAGE_PERCENT = "DATASOURCE_POOL_USAGE_PERCENT";
        }

    }

}
