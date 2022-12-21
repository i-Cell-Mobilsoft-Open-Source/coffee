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
package hu.icellmobilsoft.coffee.rest.projectstage;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * ProjectStage enum
 *
 * @author speter555
 * @since 1.13.0
 */
public enum ProjectStageEnum {

    /**
     * Production projectStage enum
     */
    PRODUCTION("Production", "Prod", "Normal"),

    /**
     * Development projectStage enum
     */
    DEVELOPMENT("Development", "Dev"),

    /**
     * Test projectStage enum
     */
    TEST("Test", "UnitTest", "SystemTest", "IntegrationTest", "Staging");

    /**
     * Alternative names of projectStage enum
     */
    private final List<String> alternativeNames;

    /**
     * Constructor
     * 
     * @param alternativeNames
     *            alternative name os projectStage enum
     */
    ProjectStageEnum(String... alternativeNames) {
        this.alternativeNames = Arrays.asList(alternativeNames);
    }

    /**
     * Get defined alternative names
     * 
     * @return list of defined alternative names
     */
    public List<String> getAlternativeNames() {
        return alternativeNames;
    }

    /**
     * Create ProjectStageEnum from alternative name
     * 
     * @param name
     *            alternative name param
     * @return {@link ProjectStageEnum} if parameter is in alternative names of any {@link ProjectStageEnum}, otherwise
     *         {@link ProjectStageEnum#PRODUCTION}
     */
    public static ProjectStageEnum createProjectStageEnumFromAlternativeName(String name) {
        for (ProjectStageEnum projectStageEnum : ProjectStageEnum.values()) {
            for (String projectStageName : projectStageEnum.getAlternativeNames()) {
                if (StringUtils.equalsIgnoreCase(name, projectStageName)) {
                    return projectStageEnum;
                }
            }
        }
        return PRODUCTION;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
