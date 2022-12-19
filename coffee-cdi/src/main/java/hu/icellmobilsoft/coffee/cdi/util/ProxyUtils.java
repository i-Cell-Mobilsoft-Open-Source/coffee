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
package hu.icellmobilsoft.coffee.cdi.util;

import jakarta.enterprise.inject.Typed;

/**
 * Based from Deltaspike org.apache.deltaspike.core.util.ProxyUtils
 * 
 * @author Imre Scheffer
 * @since 2.0.0
 */
@Typed
public class ProxyUtils {

    private ProxyUtils() {
        // prevent instantiation
    }

    /**
     * Try to get real class encapsulated in proxy classes
     * 
     * @param currentClass
     *            current class
     * @return class of the real implementation
     */
    public static Class getUnproxiedClass(Class currentClass) {
        Class unproxiedClass = currentClass;

        while (isProxiedClass(unproxiedClass)) {
            unproxiedClass = unproxiedClass.getSuperclass();
        }

        return unproxiedClass;
    }

    /**
     * Analyses if the given class is a generated proxy class
     * 
     * @param currentClass
     *            current class
     * @return true if the given class is a known proxy class, false otherwise
     */
    public static boolean isProxiedClass(Class currentClass) {
        if (currentClass == null || currentClass.getSuperclass() == null) {
            return false;
        }

        String name = currentClass.getName();
        return name.startsWith(currentClass.getSuperclass().getName()) && (name.contains("$$") // CDI
                || name.contains("_ClientProxy") // Quarkus
                || name.contains("$HibernateProxy$")); // Hibernate
    }
}
