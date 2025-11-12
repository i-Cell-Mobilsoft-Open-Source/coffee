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

/**
 * Represents metadata for a single generic type parameter.
 * <p>
 * This class holds information about a type parameter's name and its bounds, which define constraints on the parameter’s type. It is mainly used in
 * code generation or analysis to reconstruct generic method or class signatures.
 * </p>
 *
 * <p>
 * Instances of this class are typically used within {@link MethodData}.
 * </p>
 *
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class TypeParamData {

    /** The name of the type parameter (e.g., {@code T}, {@code E}). */
    private String typeParameterName;

    /** The list of upper bounds for the type parameter (e.g., {@code Number}, {@code Serializable}). */
    private List<String> bounds;

    /**
     * Creates a new, empty {@code TypeParamData} instance.
     */
    public TypeParamData() {
        super();
    }

    /**
     * Returns the name of the type parameter.
     *
     * @return the type parameter name
     */
    public String getTypeParameterName() {
        return typeParameterName;
    }

    /**
     * Sets the name of the type parameter.
     *
     * @param typeParameterName
     *            the type parameter name to set
     */
    public void setTypeParameterName(String typeParameterName) {
        this.typeParameterName = typeParameterName;
    }

    /**
     * Returns the list of upper bounds for this type parameter.
     *
     * @return a list of type bounds
     */
    public List<String> getBounds() {
        return bounds;
    }

    /**
     * Sets the list of upper bounds for this type parameter.
     *
     * @param bounds
     *            the list of type bounds to set
     */
    public void setBounds(List<String> bounds) {
        this.bounds = bounds;
    }
}
