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
package hu.icellmobilsoft.coffee.rest.validation.xml.exception;

import java.util.ArrayList;
import java.util.List;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.XMLValidationError;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.dto.exception.enums.Severity;

/**
 * Xsd validáció / feldolgozás során keletkező hibákhoz kivétel osztály
 *
 * @see XsdProcessingExceptionWrapper
 * @see hu.icellmobilsoft.coffee.rest.validation.xml.XmlMessageBodyReaderBase
 * @author ferenc.lutischan
 * @author imre.scheffer
 * @since 1.0.0
 */
public class XsdProcessingException extends BaseException {

    private static final long serialVersionUID = 1L;

    private final List<XMLValidationError> errors = new ArrayList<>();

    /**
     * Constructor for XsdProcessingException.
     *
     * @param faultTypeEnum
     *            fault type enum
     * @param message
     *            exception message
     */
    public XsdProcessingException(Enum<?> faultTypeEnum, String message) {
        super(faultTypeEnum, message);
    }

    /**
     * Constructor for XsdProcessingException.
     *
     * @param faultTypeEnum
     *            fault type enum
     * @param message
     *            exception message
     * @param e
     *            throwable
     */
    public XsdProcessingException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }

    /**
     * Constructor for XsdProcessingException.
     *
     * @param errors
     *            error list
     * @param e
     *            throwable
     */
    public XsdProcessingException(List<XMLValidationError> errors, Throwable e) {
        super(CoffeeFaultType.INVALID_XML, "Xml validation failed", e);
        setSeverity(Severity.MINOR);
        this.errors.addAll(errors);
    }

    /**
     * Getter for the field <code>errors</code>.
     *
     * @return errors
     */
    public List<XMLValidationError> getErrors() {
        return this.errors;
    }
}
