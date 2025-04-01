/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.quarkus.extension.deltaspike.data.deployment;

import java.util.ArrayList;

import io.quarkus.builder.item.SimpleBuildItem;

/**
 * Build item which contains Repository classes
 *
 * @since 2.6.0
 * @author speter555
 */
public final class DeltaspikeDataBuildItem extends SimpleBuildItem {

    private final ArrayList<Class<?>> repositoryClasses;
    private final ArrayList<Class<?>> removeableClasses;

    /**
     *
     * Constructor
     *
     * @param repositoryClasses
     *            list of reposiotry classNames
     * @param removeableClasses
     *            lisf of removeable classes
     */
    public DeltaspikeDataBuildItem(ArrayList<Class<?>> repositoryClasses, ArrayList<Class<?>> removeableClasses) {
        this.repositoryClasses = repositoryClasses;
        this.removeableClasses = removeableClasses;
    }

    /**
     * Getter of repositoryClasses
     * 
     * @return repositoryClasses list
     */
    public ArrayList<Class<?>> getRepositoryClasses() {
        return repositoryClasses;
    }

    /**
     * Getter of removeableClasses
     *
     * @return removeableClasses list
     */
    public ArrayList<Class<?>> getRemoveableClasses() {
        return removeableClasses;
    }
}
