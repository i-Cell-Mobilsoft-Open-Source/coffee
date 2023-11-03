/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.model.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import hu.icellmobilsoft.coffee.model.base.exception.ProviderException;

/**
 * Abstract class of providers.
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
public abstract class AbstractProvider {

    private static ClassFieldsAndMethodsCache classFieldsAndMethodsCache = new ClassFieldsAndMethodsCache();

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractProvider() {
        super();
    }

    /**
     * Returns the field from the specified list associated with the specified getter method based on its name
     *
     * @param method
     *            the getter method
     * @param allFields
     *            list of fields
     * @return the associated field
     */
    protected Field getFieldByMethod(Method method, List<Field> allFields) {
        for (Field field : allFields) {
            String fieldName = field.getName();
            String suffixMethodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            List<String> possibleMethodNames = List.of("get" + suffixMethodName, "is" + suffixMethodName);
            if (possibleMethodNames.contains(method.getName()) && field.getDeclaringClass().equals(method.getDeclaringClass())) {
                return field;
            }
        }
        throw new ProviderException(
                "Field is not found based on the name of the annotated method: " + method.getDeclaringClass() + "." + method.getName());
    }

    /**
     * Returns all the fields and methods of the specified class in a pair of lists
     *
     * @param clazz
     *            the specified class
     * @return pair of fields and methods lists
     */
    protected Pair<List<Field>, List<Method>> getAllFieldsAndMethods(Class<?> clazz) {
        return classFieldsAndMethodsCache.getFieldsAndMethods(clazz);
    }

}
