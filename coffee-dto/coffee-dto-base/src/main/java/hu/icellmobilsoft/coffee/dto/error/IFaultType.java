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
package hu.icellmobilsoft.coffee.dto.error;

/**
 * Generic interface to mark the fault code enums and the generic error categories.
 *
 * @param <E>
 *            the fault code enumeration
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface IFaultType<E extends Enum<E>> {

    /**
     * {@link Enum#getDeclaringClass()}
     *
     * @return the class of the fault code enum
     */
    Class<E> getDeclaringClass();

    /**
     * {@link Enum#name()}
     *
     * @return the name of this enum constant
     */
    String name();
}
