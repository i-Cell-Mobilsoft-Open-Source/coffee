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
package hu.icellmobilsoft.coffee.dto.common;

/**
 * Constant identifiers used for logging purposes
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface LogConstants {

    /**
     * When logging, this variable is searched for in the pattern.
     */
    final String LOG_SESSION_ID = "extSessionId";

    /**
     * When logging, the service name
     */
    final String LOG_SERVICE_NAME = "serviceName";

}
