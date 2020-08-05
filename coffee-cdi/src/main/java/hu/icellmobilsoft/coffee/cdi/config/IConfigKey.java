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

/**
 * Coffee szintű konfigurációs kulcsok gyűjtője
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface IConfigKey {

    /**
     * Coffee konfigurációk prefix-e
     */
    String COFFEE_CONFIG_PREFIX = "coffee.config";

    /**
     * Nyelvesítés szótárak elérési útvonalak (java class package formátum).<br>
     * Pl. "i18n.messages,i18n.validators,i18n.enums" - space-k nelkul
     */
    String RESOURCE_BUNDLES = COFFEE_CONFIG_PREFIX + ".resource.bundles";

    /**
     * XML Catalog fájl elérési helye. Pl. "xsd/hu/icellmobilsoft/project/dto/super.catalog.xml"
     */
    String CATALOG_XML_PATH = COFFEE_CONFIG_PREFIX + ".xml.catalog.path";

    /**
     * Reguláris kifejezés a request logolásnál, etcd lekérdezésnél maszkolandó kulcsokra. Pl. "[\\w\\s]*?secret[\\w\\s]*?",
     * "[\\w\\s]*?pass[\\w\\s]*?"
     */
    String LOG_SENSITIVE_KEY_PATTERN = COFFEE_CONFIG_PREFIX + ".log.sensitive.key.pattern";
}
