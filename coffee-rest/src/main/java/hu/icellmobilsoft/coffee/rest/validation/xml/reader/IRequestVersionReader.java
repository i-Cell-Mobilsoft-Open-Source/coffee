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

import java.io.InputStream;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;

/**
 * Interface for reading the requestVersion value.
 *
 * @see EmptyRequestVersionReader
 * @author tamas.cserhati
 * @since 2.6.0
 */
public interface IRequestVersionReader {

    /**
     * Determines the version number based on a general HTTP request. It could be from the HTTP entity, header, or anything else.
     * Can read the request version from a general http request. (request body, header...)
     *
     * @param inputStream
     *            HTTP entity in raw stream format, if its possible read version from header or uri
     * @return the request version
     * @throws TechnicalException
     *             on error
     */
    default String readVersion(InputStream inputStream) throws TechnicalException {
        return null;
    }
    
    /**
     * Determines version number value from a DTO.
     *
     * @param <T>
     *            T object
     * @param dto
     *            HTTP entity converted to DTO
     * @return request number
     * @throws TechnicalException
     *             exception
     */
    default <T> String readVersion(T dto) throws TechnicalException {
        return null;
    }

}
