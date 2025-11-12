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
import java.util.stream.Collectors;

import hu.icellmobilsoft.coffee.module.repserv.action.write.JavaFileWriter;

/**
 * Represents metadata for a method within a generated or analyzed Java class.
 * <p>
 * This class stores information about the method’s name, return type, parameters, thrown exceptions, type parameters, identifier, and associated JPQL
 * query. It is typically used during annotation processing or source code generation to describe method signatures and related metadata.
 * </p>
 *
 * <p>
 * Instances of this class are aggregated within {@link ClassData}.
 * </p>
 *
 * @author xy
 * @since 0.0.1
 */
public class MethodData {

    /** The method name. */
    private String methodName;

    /** The fully qualified return type of the method. */
    private String returnType;

    /** A list of exception types declared in the method's {@code throws} clause. */
    private List<String> thrownTypes;

    /** A list of parameter metadata objects for the method. */
    private List<ParamData> params;

    /** A list of generic type parameters defined for the method. */
    private List<TypeParamData> typeParams;

    /** A unique identifier for the method (optional). */
    private String id;

    /** The JPQL query string associated with this method, if any. */
    private String jpql;

    /**
     * Creates a new, empty {@code MethodData} instance.
     */
    public MethodData() {
        super();
    }

    /**
     * Returns the method name.
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the method name.
     *
     * @param methodName
     *            the method name to set
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Returns the return type of the method.
     *
     * @return the return type
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the return type of the method.
     *
     * @param returnType
     *            the return type to set
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Returns the list of thrown exception types.
     * <p>
     * If the list is {@code null}, a new list will be created.
     * </p>
     *
     * @return the list of thrown types
     */
    public List<String> getThrownTypes() {
        if (thrownTypes == null) {
            thrownTypes = new ArrayList<>();
        }
        return thrownTypes;
    }

    /**
     * Sets the list of thrown exception types.
     *
     * @param thrownTypes
     *            the list of thrown types to set
     */
    public void setThrownTypes(List<String> thrownTypes) {
        this.thrownTypes = thrownTypes;
    }

    /**
     * Returns the list of method parameters.
     * <p>
     * If the list is {@code null}, a new list will be created.
     * </p>
     *
     * @return the list of {@link ParamData} objects
     */
    public List<ParamData> getParams() {
        if (params == null) {
            params = new ArrayList<>();
        }
        return params;
    }

    /**
     * Sets the list of method parameters.
     *
     * @param params
     *            the list of {@link ParamData} to set
     */
    public void setParams(List<ParamData> params) {
        this.params = params;
    }

    /**
     * Adds a parameter entry to the parameter list.
     *
     * @param param
     *            the {@link ParamData} object to add
     */
    public void addParam(ParamData param) {
        getParams().add(param);
    }

    /**
     * Returns the list of type parameters defined for the method.
     * <p>
     * If the list is {@code null}, a new list will be created.
     * </p>
     *
     * @return the list of {@link TypeParamData} objects
     */
    public List<TypeParamData> getTypeParams() {
        if (typeParams == null) {
            typeParams = new ArrayList<>();
        }
        return typeParams;
    }

    /**
     * Sets the list of type parameters.
     *
     * @param typeParams
     *            the list of {@link TypeParamData} to set
     */
    public void setTypeParams(List<TypeParamData> typeParams) {
        this.typeParams = typeParams;
    }

    /**
     * Adds a new type parameter to the list.
     *
     * @param typeParam
     *            the {@link TypeParamData} object to add
     */
    public void addTypeParam(TypeParamData typeParam) {
        getTypeParams().add(typeParam);
    }

    /**
     * Returns the unique identifier for this method.
     *
     * @return the method ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this method.
     *
     * @param id
     *            the method ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the JPQL query associated with this method, if present.
     *
     * @return the JPQL query string
     */
    public String getJpql() {
        return jpql;
    }

    /**
     * Sets the JPQL query associated with this method.
     *
     * @param jpql
     *            the JPQL query string to set
     */
    public void setJpql(String jpql) {
        this.jpql = jpql;
    }

    /**
     * Builds and returns a string representation of the method signature, including type parameters, return type, name, parameters, and thrown types.
     *
     * @return the method signature as a string
     */
    @Override
    public String toString() {
        String retVal = getTypeParamsString();

        retVal += getReturnType() + " " + getMethodName();

        retVal += getParamsString();

        if (!getThrownTypes().isEmpty()) {
            retVal += "throws ";
            retVal += String.join(",", getThrownTypes());
        }

        return retVal;
    }

    /**
     * Builds a string representation of the method’s type parameters.
     *
     * @return the type parameters as a formatted string
     */
    public String getTypeParamsString() {
        String retVal = "";

        if (!getTypeParams().isEmpty()) {
            retVal += "<";
            retVal += getTypeParams().stream()
                    .map(typeParam -> typeParam.getTypeParameterName() + JavaFileWriter.getExtendsClause(String.join("&", typeParam.getBounds())))
                    .collect(Collectors.joining(","));
            retVal += "> ";
        }
        return retVal;
    }

    /**
     * Builds a string representation of the method’s parameter list.
     *
     * @return the parameters as a formatted string
     */
    public String getParamsString() {
        String retVal = "(";
        retVal += getParams().stream().map(param -> param.getParameterType() + " " + param.getParameterName()).collect(Collectors.joining(","));
        retVal += ")";
        return retVal;
    }
}
