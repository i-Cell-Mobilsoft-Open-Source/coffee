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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hu.icellmobilsoft.coffee.rest.projectstage;

import jakarta.enterprise.inject.Vetoed;

import java.io.Serializable;
import java.util.Objects;


/**
 * ProjectStage , default is Production
 *
 * @author speter555
 * @since 1.13.0
 * 
 */
@Vetoed
public class ProjectStage implements Serializable {

    /**
     * Production ProjectStage
     */
    public final static ProjectStage Production = new ProjectStage();

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

    public ProjectStageEnum getProjectStageEnum() {
        return projectStageEnum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return projectStageEnum.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProjectStage) {
            return ((ProjectStage) obj).getProjectStageEnum() == this.getProjectStageEnum();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectStageEnum);
    }

}
