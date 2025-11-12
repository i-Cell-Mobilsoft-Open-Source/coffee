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

import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Name;

/**
 * Represents a repository method used to associate metadata with JPQL statements.
 * <p>
 * This class stores information about a specific method declared in a repository interface, including its name, parameter types, and the repository
 * type it belongs to. It is mainly used for mapping repository methods to their corresponding JPQL queries during annotation processing or code
 * generation.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class RepositoryMethod {

    /** The repository type that declares the method. */
    private String repositoryType;

    /** The name of the method. */
    private Name methodName;

    /** The list of parameter types for the method. */
    private List<String> parameterTypes;

    /** The JPQL query associated with this repository method. */
    private String jpql;

    /**
     * Creates a new, empty {@code RepositoryMethod} instance.
     */
    public RepositoryMethod() {
        super();
    }

    /**
     * Returns the repository type that declares this method.
     *
     * @return the repository type
     */
    public String getRepositoryType() {
        return repositoryType;
    }

    /**
     * Sets the repository type that declares this method.
     *
     * @param repositoryType
     *            the repository type to set
     */
    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

    /**
     * Returns the method name.
     *
     * @return the method name
     */
    public Name getMethodName() {
        return methodName;
    }

    /**
     * Sets the method name.
     *
     * @param methodName
     *            the method name to set
     */
    public void setMethodName(Name methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns the list of parameter types for this method.
     *
     * @return a list of parameter types
     */
    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Sets the list of parameter types for this method.
     *
     * @param parameterTypes
     *            the list of parameter types to set
     */
    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    /**
     * Returns the JPQL query associated with this repository method.
     *
     * @return the JPQL query string
     */
    public String getJpql() {
        return jpql;
    }

    /**
     * Sets the JPQL query associated with this repository method.
     *
     * @param jpql
     *            the JPQL query string to set
     */
    public void setJpql(String jpql) {
        this.jpql = jpql;
    }

    /**
     * Indicates whether this object is equal to another. Two {@code RepositoryMethod} instances are considered equal if they represent the same
     * repository type, method name, and parameter types.
     *
     * @param o
     *            the object to compare
     * @return {@code true} if equal, otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RepositoryMethod that = (RepositoryMethod) o;
        return Objects.equals(repositoryType, that.repositoryType) && Objects.equals(methodName, that.methodName)
                && Objects.equals(parameterTypes, that.parameterTypes);
    }

    /**
     * Returns the hash code for this object, based on repository type, method name, and parameter types.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(repositoryType, methodName, parameterTypes);
    }
}
