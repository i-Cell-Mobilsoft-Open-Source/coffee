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

/**
 * ProjectStage , default is Production
 *
 * @author speter555
 * @since 1.13.0
 * 
 */
public class ProjectStage {

    /** the enum of the ProjectStage */
    private final ProjectStageEnum projectStageEnum;

    /**
     * ProjectStage construtor with alternative name
     * 
     * @param name
     *            alternative name
     */
    public ProjectStage(String name) {
        projectStageEnum = ProjectStageEnum.createProjectStageEnumFromAlternativeName(name);
    }

    /**
     * ProjectStage construtor for Production
     */
    public ProjectStage() {
        projectStageEnum = ProjectStageEnum.PRODUCTION;
    }

    /**
     * Get ProjectStage enum
     *
     * @return {@link ProjectStageEnum} for instance
     */
    public ProjectStageEnum getProjectStageEnum() {
        return projectStageEnum;
    }

    /**
     * Tell that projectStage is in production stage.
     * 
     * @return true if projectStage in production stage, otherwise false
     */
    public boolean isProductionStage() {
        return projectStageEnum == ProjectStageEnum.PRODUCTION;
    }
}
