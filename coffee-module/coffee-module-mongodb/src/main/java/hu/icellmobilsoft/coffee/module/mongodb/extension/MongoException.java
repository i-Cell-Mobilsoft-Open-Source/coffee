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
package hu.icellmobilsoft.coffee.module.mongodb.extension;

import hu.icellmobilsoft.coffee.exception.TechnicalException;
import hu.icellmobilsoft.coffee.exception.enums.CoffeeFaultType;

/**
 * Exception class for mongo exceptions
 * 
 * @author czenczl
 * @since 1.1.0
 *
 */
public class MongoException extends TechnicalException {

    private static final long serialVersionUID = 1L;

    /**
     * Initializes the exception with a message
     * 
     * @param message
     *            exception message
     */
    public MongoException(String message) {
        this(CoffeeFaultType.OPERATION_FAILED, message, null);
    }

    /**
     * Initializes the exception with a message, and fault type
     *
     * @param faultTypeEnum
     *            fault type enum
     * @param message
     *            exception message
     */
    public MongoException(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null);
    }

    /**
     * Initializes the exception with a message, fault type, and cause
     *
     * @param faultTypeEnum
     *            fault type enum
     * @param message
     *            exception message
     * @param cause
     *            the cause of the exception
     */
    public MongoException(Enum<?> faultTypeEnum, String message, Throwable cause) {
        super(faultTypeEnum, message, cause);
    }

}
