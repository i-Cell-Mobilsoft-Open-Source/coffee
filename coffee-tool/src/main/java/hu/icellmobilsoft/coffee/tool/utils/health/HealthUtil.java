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
package hu.icellmobilsoft.coffee.tool.utils.health;

import hu.icellmobilsoft.coffee.cdi.health.constants.HealthConstant;

/**
 * Helps collect nodeId
 * 
 * @author czenczl
 * @since 1.15.0
 *
 */
public class HealthUtil {

    /**
     * Gets the node id from the running wildfly server
     * 
     * @param nodeId
     *            unique identifier for health check
     * @return the underling unique node id
     */
    public static String getNodeId(String nodeId) {
        return nodeId == null ? System.getProperty(HealthConstant.Common.JBOSS_NODE_NAME) : nodeId;
    }

}
