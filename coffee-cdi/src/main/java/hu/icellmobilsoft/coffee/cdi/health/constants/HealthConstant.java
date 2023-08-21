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
package hu.icellmobilsoft.coffee.cdi.health.constants;

/**
 * Health configuration constants
 * 
 * @author czenczl
 * @since 1.15.0
 *
 */
public interface HealthConstant {

    /**
     * Common health configs
     * 
     */
    interface Common {
        /**
         * default timout to connect the resource
         */
        long DEFAULT_CONNECT_TIMEOUT_SEC = 1;
        /**
         * Key that helps assign a unique nodeId to the service that performs resource checking
         */
        String NODE_NAME = "nodeName";
        /**
         * helps collect unique identifier
         */
        String JBOSS_NODE_NAME = "jboss.node.name";
        /**
         * the checked resource URL
         */
        String URL = "URL";
    }

}
