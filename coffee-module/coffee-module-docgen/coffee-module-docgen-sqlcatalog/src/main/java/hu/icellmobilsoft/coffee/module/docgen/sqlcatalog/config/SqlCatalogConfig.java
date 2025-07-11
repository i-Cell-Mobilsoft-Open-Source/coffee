/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.config;

import java.util.Map;

import hu.icellmobilsoft.coffee.module.docgen.common.config.AbstractDocGenConfig;

/**
 * Class representing the configuration for the module
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class SqlCatalogConfig extends AbstractDocGenConfig {

    private static final String CONFIG_PREFIX = "coffee.docgen.sql.catalog.";

    /**
     * Default output path
     */
    public static final String DEFAULT_OUTPUT_PATH = "META-INF/";

    /**
     * Default output file name
     */
    public static final String DEFAULT_OUTPUT_FILE_NAME = "sql_catalog.adoc";

    /**
     * Creates the config object based on properties
     *
     * @param properties
     *            the map which contains the config properties
     */
    public SqlCatalogConfig(Map<String, String> properties) {
        super(properties);
    }

    @Override
    protected String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    protected String getDefaultOutputPath() {
        return DEFAULT_OUTPUT_PATH;
    }

    @Override
    protected String getDefaultOutputFileName() {
        return DEFAULT_OUTPUT_FILE_NAME;
    }
}
