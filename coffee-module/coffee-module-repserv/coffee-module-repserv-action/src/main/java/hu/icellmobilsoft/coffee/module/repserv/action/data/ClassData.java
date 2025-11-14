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
package hu.icellmobilsoft.coffee.module.repserv.action.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents metadata for a generated or analyzed Java class.
 * <p>
 * This class stores information such as the package name, class name,
 * superclass name, repository reference, and method-level metadata.
 * It is primarily used during code generation or annotation processing
 * to accumulate structural information about the current class.
 * </p>
 *
 * <p>Each {@link MethodData} entry in {@code methodDataList} represents
 * a single method belonging to this class.</p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class ClassData {

    /** The package name of the class. */
    private String packageName;

    /** The simple name of the inheritor class. */
    private String inheritorName;

    /** The name of the class. */
    private String className;

    /** A list containing metadata for each method in this class. */
    private List<MethodData> methodDataList;

    /** The name of the associated repository, if applicable. */
    private String repositoryName;

    /** The type of the associated repository, if applicable. */
    private String repositoryType;

    /**
     * Creates a new, empty {@code ClassData} instance.
     */
    public ClassData() {
        super();
    }

    /**
     * Returns the package name of this class.
     *
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the package name of this class.
     *
     * @param packageName the package name to set
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Returns the class name.
     *
     * @return the class name
     */
    public String getInheritorName() {
        return inheritorName;
    }

    /**
     * Sets the class name.
     *
     * @param inheritorName the class name to set
     */
    public void setInheritorName(String inheritorName) {
        this.inheritorName = inheritorName;
    }

    /**
     * Returns the name of the superclass (ancestor).
     *
     * @return the ancestor name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of the superclass (ancestor).
     *
     * @param className the ancestor name to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Returns the list of method metadata entries.
     * <p>If the list has not been initialized yet, a new one will be created.</p>
     *
     * @return the list of {@link MethodData} objects
     */
    public List<MethodData> getMethodDataList() {
        if (methodDataList == null) {
            methodDataList = new ArrayList<>();
        }
        return methodDataList;
    }

    /**
     * Sets the method metadata list.
     *
     * @param methodDataList the list of {@link MethodData} objects to set
     */
    public void setMethodDataList(List<MethodData> methodDataList) {
        this.methodDataList = methodDataList;
    }

    /**
     * Adds a new {@link MethodData} entry to the method list.
     *
     * @param methodData the {@link MethodData} object to add
     */
    public void addMethodData(MethodData methodData) {
        getMethodDataList().add(methodData);
    }

    /**
     * Returns the most recently added {@link MethodData} entry.
     * <p>Used to access the method currently being processed or generated.</p>
     *
     * @return the latest {@link MethodData} object
     */
    public MethodData getLatestMethodData() {
        return getMethodDataList().get(getMethodDataList().size() - 1);
    }

    /**
     * Returns the repository name associated with this class.
     *
     * @return the repository name
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * Sets the repository name associated with this class.
     *
     * @param repositoryName the repository name to set
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    /**
     * Returns the repository type associated with this class.
     *
     * @return the repository type
     */
    public String getRepositoryType() {
        return repositoryType;
    }

    /**
     * Sets the repository type associated with this class.
     *
     * @param repositoryType the repository type to set
     */
    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }
}
