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
package hu.icellmobilsoft.coffee.rest.validation.xml.reader;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;

/**
 * Interfész a JSON fájlok requestVersion értékének kiolvasásához
 *
 * @see JsonRequestVersionReader
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface IJsonRequestVersionReader {

    /**
     * Verziószámot állapítja meg egy általános HTTP request alapján. Lehet az HTTP entity, header, akármi...
     *
     * @param <T>
     *            T object
     * @param dto
     *            HTTP entity DTO-vá alakítva
     * @throws TechnicalException
     */
    <T> String readFromJSON(T dto) throws TechnicalException;
}
