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
package hu.icellmobilsoft.coffee.rest.validation.xml.error;

import java.util.List;

import javax.xml.bind.ValidationEventHandler;

import hu.icellmobilsoft.coffee.dto.exception.XMLValidationError;

/**
 * ValidationEventHandler interfészt terjeszti ki<br>
 * Alap implementációja az:<br>
 *
 * @see XsdValidationErrorCollector
 * @author ferenc.lutischan
 * @since 1.0.0
 */
public interface IXsdValidationErrorCollector extends ValidationEventHandler {
    /**
     * Clears XSD validation errors.
     */
    void clearErrors();

    /**
     * Returns {@link List} of XSD validation errors.
     *
     * @return {@code XMLValidationError} list.
     */
    List<XMLValidationError> getErrors();
}
