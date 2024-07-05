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
 * Collector of configuration keys at the Coffee level
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@ConfigDoc
public interface IConfigKey {

    /**
     * Prefix for Coffee configurations
     */
    @ConfigDoc(exclude = true)
    String COFFEE_CONFIG_PREFIX = "coffee.config";

    /**
     * Prefix for Coffee app configurations
     */
    @ConfigDoc(exclude = true)
    String COFFEE_CONFIG_APP_PREFIX = "coffee.app";

    /**
     * Prefix for Coffee Microprofile Config Source configurations
     */
    @ConfigDoc(exclude = true)
    String COFFEE_CONFIG_SOURCE_PREFIX = "coffee.configSource";

    /**
     * Coffee app ProjectStage
     */
    String COFFEE_APP_PROJECT_STAGE = COFFEE_CONFIG_APP_PREFIX + ".projectStage";

    /**
     * Coffee app name key
     */
    String COFFEE_APP_NAME = COFFEE_CONFIG_APP_PREFIX + ".name";

    /**
     * Language localization dictionaries access paths (in Java class package format).<br>
     * e.g. "i18n.messages,i18n.validators,i18n.enums" - without space
     */
    String RESOURCE_BUNDLES = COFFEE_CONFIG_PREFIX + ".resource.bundles";

    /**
     * Location of the XML Catalog file. For example: "xsd/hu/icellmobilsoft/project/dto/super.catalog.xml"
     */
    String CATALOG_XML_PATH = COFFEE_CONFIG_PREFIX + ".xml.catalog.path";

    /**
     * Regular expression for masking keys in request logging, etcd queries, etc. For example: "[\\w\\s]*?secret[\\w\\s]*?",
     * "[\\w\\s]*?pass[\\w\\s]*?"
     */
    String LOG_SENSITIVE_KEY_PATTERN = COFFEE_CONFIG_PREFIX + ".log.sensitive.key.pattern";

    /**
     * Default datasource name key used in the system. If not specified, then {@value #DATASOURCE_DEFAULT_NAME_VALUE}
     */
    String DATASOURCE_DEFAULT_NAME = COFFEE_CONFIG_PREFIX + ".datasource.default.name";
    /**
     * Default name of the datasource
     */
    String DATASOURCE_DEFAULT_NAME_VALUE = "icellmobilsoftDS";
}
