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

import javax.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;

/**
 * Dummy implementáció, ami a requestVersion értékét nem kezeli.
 *
 * @see IXmlRequestVersionReader
 * @see IJsonRequestVersionReader
 * @author robert.kaplar
 * @since 1.0.0
 */
@Dependent
public class EmptyRequestVersionReader implements IXmlRequestVersionReader, IJsonRequestVersionReader {

    /**
     * {@inheritDoc}
     *
     * Null-t ad vissza, amit a hívó kód lekezel
     */
    @Override
    public String readFromXML(InputStream is) throws TechnicalException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public <T> String readFromJSON(T dto) throws TechnicalException {
        return null;
    }
}
