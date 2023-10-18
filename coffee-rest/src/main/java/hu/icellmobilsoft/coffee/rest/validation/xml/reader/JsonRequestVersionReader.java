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

import java.lang.reflect.Method;
import java.text.MessageFormat;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * JSON ismeretlen obejtbol kiszedjuk a requestVersion erteket.<br>
 * Jelen esetben a "context/requestVersion" logika van keresve, persze ez nem fele meg mindenkinek, akkor CDI-el felul kell irni. Ez egy mintanak is
 * szolgal
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Alternative
@Dependent
public class JsonRequestVersionReader implements IJsonRequestVersionReader {

    /**
     * Default constructor, constructs a new object.
     */
    public JsonRequestVersionReader() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public <T> String readFromJSON(T dto) throws TechnicalException {
        try {
            // ha ismerjuk a pontos strukturankat, akkor siman castolni is lehet es egyszeruen kiszedni
            Method contextMethod = dto.getClass().getMethod("getContext");
            Object contextValue = contextMethod.invoke(dto);

            Method versionMethod = contextValue.getClass().getMethod("getRequestVersion");
            Object versionValue = versionMethod.invoke(contextValue);

            return String.valueOf(versionValue);
        } catch (ReflectiveOperationException e) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT,
                    MessageFormat.format("Error in getting requestVersion from DTO: [{0}] ", e.getLocalizedMessage()), e);
        }
    }
}
