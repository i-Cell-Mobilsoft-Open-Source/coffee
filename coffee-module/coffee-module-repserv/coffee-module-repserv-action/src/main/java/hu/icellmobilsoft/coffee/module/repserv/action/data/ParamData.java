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

import java.util.Map;

import jakarta.json.bind.annotation.JsonbTransient;

/**
 * Represents metadata for a single method parameter.
 * <p>
 * This class stores information about the parameter’s name, type, and any nested properties (for complex types). It is typically used during
 * annotation processing or source code generation to describe method parameters and their structure.
 * </p>
 *
 * <p>
 * Instances of this class are aggregated within {@link MethodData}.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class ParamData {

    /** The parameter name. */
    private String parameterName;

    /** The parameter type. */
    private String parameterType;

    /**
     * A mapping of nested property names to their types, used for parameters representing composite objects.
     */
    @JsonbTransient
    private Map<String, String> nestedProps;

    /**
     * Creates a new, empty {@code ParamData} instance.
     */
    public ParamData() {
        super();
    }

    /**
     * Returns the parameter name.
     *
     * @return the parameter name
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Sets the parameter name.
     *
     * @param parameterName
     *            the parameter name to set
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * Returns the parameter type.
     *
     * @return the parameter type
     */
    public String getParameterType() {
        return parameterType;
    }

    /**
     * Sets the parameter type.
     *
     * @param parameterType
     *            the parameter type to set
     */
    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    /**
     * Returns a map of nested properties for this parameter.
     * <p>
     * The keys represent property names, and the values represent their corresponding types.
     * </p>
     *
     * @return a map of nested property names and types
     */
    public Map<String, String> getNestedProps() {
        return nestedProps;
    }

    /**
     * Sets the map of nested properties for this parameter.
     *
     * @param nestedProps
     *            a map of property names and their types
     */
    public void setNestedProps(Map<String, String> nestedProps) {
        this.nestedProps = nestedProps;
    }
}
