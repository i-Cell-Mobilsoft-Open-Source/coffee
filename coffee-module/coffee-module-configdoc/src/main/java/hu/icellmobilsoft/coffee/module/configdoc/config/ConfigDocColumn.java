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
package hu.icellmobilsoft.coffee.module.configdoc.config;

/**
 * Config doc columns
 *
 * @author martin.nagy
 * @since 1.10.0
 */
public enum ConfigDocColumn {
    /**
     * the configuration key
     */
    KEY,
    /**
     * the source class where the configuration key can be found
     */
    SOURCE,
    /**
     * the description of the configuration key
     */
    DESCRIPTION,
    /**
     * the default value of the configuration key
     */
    DEFAULT_VALUE,
    /**
     * the version since the configuration key available
     */
    SINCE,
}
