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
package hu.icellmobilsoft.coffee.module.repserv.action.config;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;

/**
 * Class representing the configuration for the module
 *
 * @author janos.boroczki
 * @since 2.13.0
 */
public class RepositoryServiceConfig {

    private static final String PROJECT_NAME_KEY = "coffee.repserv.config.project.name";
    private static final String DEFAULT_PROJECT_NAME = "";
    private static final String GENERATED_JSON_FOLDER_KEY = "coffee.repserv.config.generated.json.folder";
    private static final String GENERATED_JSON_CUSTOM_VALUES_KEY = "coffee.repserv.config.generated.json.customValues";
    private static final String CUSTOM_VALUE_ELEMENT_SEPARATOR = ";";
    private static final String CUSTOM_VALUE_KEY_VALUE_SEPARATOR = "=";

    /**
     * Default path value for JSON catalog file
     */
    public static final String DEFAULT_GENERATED_JSON_PATH = "repository-service/json/";

    private final Map<String, String> properties;
    private final Map<String, String> customValues;

    /**
     * Creates the config object based on properties
     *
     * @param properties
     *            the map which contains the config properties
     * @throws InvalidParameterException
     *             in case of invalid formatted value for {@code  GENERATED_JSON_CUSTOM_VALUES_KEY}
     */
    public RepositoryServiceConfig(Map<String, String> properties) throws InvalidParameterException {
        this.properties = properties;
        this.customValues = initCustomValues();
    }

    private Map<String, String> initCustomValues() throws InvalidParameterException {
        List<String[]> keyValueList = Optional.ofNullable(properties.get(GENERATED_JSON_CUSTOM_VALUES_KEY))
                .map(v -> v.split(CUSTOM_VALUE_ELEMENT_SEPARATOR))
                .stream()
                .flatMap(Arrays::stream)
                .map(String::trim)
                .map(entry -> entry.split(CUSTOM_VALUE_KEY_VALUE_SEPARATOR))
                .toList();

        Map<String, String> customValuesMap = new HashMap<>();

        for (String[] keyValue : keyValueList) {
            if (keyValue.length != 2) {
                throw new InvalidParameterException(
                        MessageFormat.format(
                                "[{0}] config format is not valid, correct format is 'KEY=VALUE; [KEY=VALUE...]'",
                                GENERATED_JSON_CUSTOM_VALUES_KEY));
            }
            customValuesMap.put(keyValue[0].trim(), keyValue[1].trim());
        }

        return customValuesMap;
    }

    /**
     * Returns the name of current project.
     *
     * @return the name of current project
     */
    public String getProjectName() {
        return Optional.ofNullable(properties.get(PROJECT_NAME_KEY)).map(pn -> pn + "_").orElse(DEFAULT_PROJECT_NAME);
    }

    /**
     * Returns the folder which is used to generate json files.
     *
     * @return the folder which is used to generate json files
     */
    public String getGeneratedJsonFolder() {
        return Optional.ofNullable(properties.get(GENERATED_JSON_FOLDER_KEY)).orElse(DEFAULT_GENERATED_JSON_PATH);
    }

    /**
     * Returns the custom values which are used to generate custom values into the root level of JSON.
     *
     * @return the custom values which are used to generate custom values into the root level of JSON
     */
    public Map<String, String> getCustomValues() {
        return customValues;
    }
}
