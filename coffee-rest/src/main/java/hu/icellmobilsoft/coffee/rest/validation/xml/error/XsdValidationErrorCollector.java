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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.ValidationEvent;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.XMLValidationError;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Hiba összegyűjtő az XSD validáláskor talált hibákhoz
 *
 * @author imre.scheffer
 * @author ferenc.lutischan
 * @since 1.0.0
 */
public class XsdValidationErrorCollector implements IXsdValidationErrorCollector {
    private static final Logger log = Logger.getLogger(XsdValidationErrorCollector.class);
    private List<XMLValidationError> errors;

    /** {@inheritDoc} */
    @Override
    public boolean handleEvent(ValidationEvent event) {
        if (StringUtils.isNotBlank(event.getMessage())
                && (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR)) {
            XMLValidationError xmlValidationError = new XMLValidationError();
            log.warn("!> XSD validation error: [{0}]", event.getMessage());
            xmlValidationError.setError(event.getMessage());
            if (!Objects.isNull(event.getLocator())) {
                xmlValidationError.setLineNumber(event.getLocator().getLineNumber());
                xmlValidationError.setColumnNumber(event.getLocator().getColumnNumber());
            }
            getErrorList().add(xmlValidationError);
        } else {
            log.warn(" +++ XSD validation warning [{0}]", event.getMessage());
        }
        // ha ez false megszakitjuk a (un)marshallert
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Error lista törlése Akkor szükséges meghívni, ha újrahasznosítjuk a létrehozott példányt! Egyébként memória luk keletkezhet!
     */
    @Override
    public void clearErrors() {
        getErrorList().clear();
    }

    private List<XMLValidationError> getErrorList() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors;
    }

    /** {@inheritDoc} */
    @Override
    public List<XMLValidationError> getErrors() {
        return Collections.unmodifiableList(getErrorList());
    }
}
