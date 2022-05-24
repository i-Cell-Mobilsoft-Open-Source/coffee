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

import java.util.Map;

/**
 * Class representing the configuration for the module
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class ConfigDocConfig {

    private static final String CONFIG_PREFIX = "coffee.configDoc.";
    private static final String OUTPUT_DIR_KEY = CONFIG_PREFIX + "outputDir";
    private static final String OUTPUT_FILE_NAME_KEY = CONFIG_PREFIX + "outputFileName";
    private static final String OUTPUT_TO_CLASS_PATH_KEY = CONFIG_PREFIX + "outputToClassPath";
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

    private final String outputDir;
    private final String outputFileName;
    private final String dynamicOutputFileName;
    private final boolean outputToClassPath;
    private final ConfigDocColumn[] columns;

    /**
     * Creates the config object based on properties
     *
     * @param properties
     *            the map which contains the config properties
     */
    public ConfigDocConfig(Map<String, String> properties) {
        outputDir = properties.getOrDefault(OUTPUT_DIR_KEY, DEFAULT_OUTPUT_PATH);
        outputFileName = properties.getOrDefault(OUTPUT_FILE_NAME_KEY, DEFAULT_OUTPUT_FILE_NAME);
        dynamicOutputFileName = properties.getOrDefault(DYNAMIC_OUTPUT_FILE_NAME_KEY, DEFAULT_DYNAMIC_OUTPUT_FILE_NAME);
        outputToClassPath = Boolean.parseBoolean(properties.getOrDefault(OUTPUT_TO_CLASS_PATH_KEY, Boolean.TRUE.toString()));

        String columnsString = properties.get(COLUMNS_KEY);
        if (columnsString == null) {
            columns = ConfigDocColumn.values();
        } else {
            String[] split = columnsString.split("\\s*,\\s*", -1);
            columns = new ConfigDocColumn[split.length];
            for (int i = 0; i < split.length; i++) {
                columns[i] = ConfigDocColumn.valueOf(split[i].trim().toUpperCase());
            }
        }
    }

    /**
     * Returns the directory for the generated file
     *
     * @return the directory for the generated file
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * Returns the generated file name
     *
     * @return the generated file name
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * Returns {@literal true} if the output folder should be on the classpath
     *
     * @return {@literal true} if the output folder should be on the classpath
     */
    public boolean isOutputToClassPath() {
        return outputToClassPath;
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
