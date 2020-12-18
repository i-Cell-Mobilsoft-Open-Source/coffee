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
package hu.icellmobilsoft.coffee.dto.exception;

import java.util.ArrayList;
import java.util.List;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * REST request validation Error
 *
 * @author imre.scheffer
 * @since 1.0.0
 * @deprecated replaced by {@code XsdProcessingException}
 */
@Deprecated(since = "1.2.0", forRemoval = true)
public class InvalidRequestException extends BaseException {

    private static final long serialVersionUID = 1L;

    private List<XMLValidationError> errors = new ArrayList<XMLValidationError>();

    /**
     * <p>
     * Constructor for InvalidRequestException.
     * </p>
     * 
     * @param message
     *            message
     * @param e
     *            e
     */
    public InvalidRequestException(String message, Throwable e) {
        super(CoffeeFaultType.INVALID_REQUEST, message, e);
    }

    /**
     * Constructor for InvalidRequestException.
     * 
     * @param errors
     *            errors
     */
    public InvalidRequestException(List<XMLValidationError> errors) {
        super(CoffeeFaultType.INVALID_REQUEST, "Cannot construct valid object");
        this.errors = errors;
    }

    /**
     * Constructor for InvalidRequestException.
     * 
     * @param errors
     *            errors
     * @param e
     *            e
     */
    public InvalidRequestException(List<XMLValidationError> errors, Throwable e) {
        super(CoffeeFaultType.INVALID_REQUEST, "Cannot construct valid object", e);
        this.errors = errors;
    }

    /**
     * Getter for the field <code>errors</code>.
     *
     * @return errors
     */
    public List<XMLValidationError> getErrors() {
        return errors;
    }

    /**
     * Setter for the field <code>errors</code>.
     * 
     * @param errors
     *            errors
     */
    public void setErrors(List<XMLValidationError> errors) {
        this.errors = errors;
    }
}
