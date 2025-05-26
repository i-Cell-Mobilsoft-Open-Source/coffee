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
package hu.icellmobilsoft.coffee.cdi.config;

import hu.icellmobilsoft.coffee.module.configdoc.ConfigDoc;

/**
 * Collector of configuration keys at the quarkus level
 *
 * @author tamas.cserhati
 * @since 2.11.0
 */
@ConfigDoc
public interface IQuarkusConfigKey {

    /**
     * Prefix for quarkus application configurations
     */
    @ConfigDoc(exclude = true)
    String QUARKUS_APPLICATION_PREFIX = "quarkus.application.";

    /**
     * Quarkus application name key
     */
    String QUARKUS_APPLICATION_NAME = QUARKUS_APPLICATION_PREFIX + ".name";

}
