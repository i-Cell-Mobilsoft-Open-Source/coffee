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
 */
public class InvalidRequestException extends BaseException {

    private static final long serialVersionUID = 1L;

    private List<XMLValidationError> errors = new ArrayList<XMLValidationError>();

    /**
     * <p>Constructor for InvalidRequestException.</p>
     */
    public InvalidRequestException(String message, Throwable e) {
        super(CoffeeFaultType.INVALID_REQUEST, message, e);
    }

    /**
     * <p>Constructor for InvalidRequestException.</p>
     */
    public InvalidRequestException(List<XMLValidationError> errors) {
        super(CoffeeFaultType.INVALID_REQUEST, "Cannot construct valid object");
        this.errors = errors;
    }

    /**
     * <p>Constructor for InvalidRequestException.</p>
     */
    public InvalidRequestException(List<XMLValidationError> errors, Throwable e) {
        super(CoffeeFaultType.INVALID_REQUEST, "Cannot construct valid object", e);
        this.errors = errors;
    }

    /**
     * <p>Getter for the field <code>errors</code>.</p>
     */
    public List<XMLValidationError> getErrors() {
        return errors;
    }

    /**
     * <p>Setter for the field <code>errors</code>.</p>
     */
    public void setErrors(List<XMLValidationError> errors) {
        this.errors = errors;
    }
}
