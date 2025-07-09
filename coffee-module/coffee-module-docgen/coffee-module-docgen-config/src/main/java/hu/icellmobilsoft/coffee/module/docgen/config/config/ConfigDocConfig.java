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
package hu.icellmobilsoft.coffee.module.docgen.config.config;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;

import hu.icellmobilsoft.coffee.module.docgen.common.config.AbstractDocGenConfig;

/**
 * Class representing the configuration for the module
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class ConfigDocConfig extends AbstractDocGenConfig {

    private static final String CONFIG_PREFIX = "coffee.docgen.config.";
    private static final String DYNAMIC_OUTPUT_FILE_NAME_KEY = CONFIG_PREFIX + "dynamicOutputFileName";
    private static final String COLUMNS_KEY = CONFIG_PREFIX + "columns";

    /**
     * Default output path
     */
    public static final String DEFAULT_OUTPUT_PATH = "META-INF/";

    /**
     * Default output file name
     */
    public static final String DEFAULT_OUTPUT_FILE_NAME = "config_keys.adoc";
    /**
     * Default output file name for dynamic configs
     */
    public static final String DEFAULT_DYNAMIC_OUTPUT_FILE_NAME = "dynamic_config_keys.adoc";

    private final String dynamicOutputFileName;
    private final ConfigDocColumn[] columns;

    /**
     * Creates the config object based on properties
     *
     * @param properties
     *            the map which contains the config properties
     */
    public ConfigDocConfig(Map<String, String> properties) {
        super(properties);
        dynamicOutputFileName = properties.getOrDefault(DYNAMIC_OUTPUT_FILE_NAME_KEY, DEFAULT_DYNAMIC_OUTPUT_FILE_NAME);
        columns = processColumnConfig(properties);
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

    private ConfigDocColumn[] processColumnConfig(Map<String, String> properties) {
        String columnsString = properties.get(COLUMNS_KEY);
        if (columnsString == null) {
            return ConfigDocColumn.values();
        }

        String[] split = columnsString.split("\\s*,\\s*", -1);
        ConfigDocColumn[] columns = new ConfigDocColumn[split.length];
        for (int i = 0; i < split.length; i++) {
            String name = split[i].trim().toUpperCase();
            columns[i] = EnumUtils.getEnum(ConfigDocColumn.class, name);
            if (columns[i] == null) {
                throw new IllegalStateException(
                        MessageFormat.format(
                                "Unknown configDoc column: [{0}]. Possible values: [{1}]",
                                split[i],
                                Arrays.toString(ConfigDocColumn.values())));
            }
        }
        return columns;
    }

    /**
     * Returns the generated file name for dynamic config.
     *
     * @return the generated file name for dynamic config
     */
    public String getDynamicOutputFileName() {
        return dynamicOutputFileName;
    }

    /**
     * Returns the columns of the generated table
     * 
     * @return the columns of the generated table
     */
    public ConfigDocColumn[] getColumns() {
        return columns;
    }
}
