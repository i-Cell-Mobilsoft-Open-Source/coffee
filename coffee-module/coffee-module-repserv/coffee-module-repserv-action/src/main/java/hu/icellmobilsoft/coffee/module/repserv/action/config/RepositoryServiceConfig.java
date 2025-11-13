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

import java.util.Map;
import java.util.Optional;

/**
 * Class representing the configuration for the module
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class RepositoryServiceConfig {

    private static final String PROJECT_NAME_KEY = "coffee.repserv.config.project.name";
    private static final String DEFAULT_PROJECT_NAME = "";
    private static final String GENERATED_JSON_FOLDER_KEY = "coffee.repserv.config.generated.json.folder";
    /**
     * Default path value for JSON catalog file
     */
    public static final String DEFAULT_GENERATED_JSON_PATH = "repository-service/json/";

    private final Map<String, String> properties;

    /**
     * Creates the config object based on properties
     *
     * @param properties
     *            the map which contains the config properties
     */
    public RepositoryServiceConfig(Map<String, String> properties) {
        this.properties = properties;
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
}
