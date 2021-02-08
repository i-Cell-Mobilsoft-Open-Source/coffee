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
package hu.icellmobilsoft.coffee.module.notification.exception;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * PushClientException class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class PushClientException extends TechnicalException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for PushClientException with given exception message, default fault type {@link CoffeeFaultType#OPERATION_FAILED} and no
     * {@link Throwable}.
     * 
     * @param message
     *            exception message to set
     */
    public PushClientException(String message) {
        this(CoffeeFaultType.OPERATION_FAILED, message, null);
    }

    /**
     * Constructor for PushClientException with given exception message, fault type and no {@link Throwable}.
     *
     * @param faultTypeEnum
     *            fault type enum to set
     * @param message
     *            exception message to set
     */
    public PushClientException(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null);
    }

    /**
     * Constructor for PushClientException with given exception message, fault type and {@link Throwable}.
     *
     * @param faultTypeEnum
     *            fault type enum to set
     * @param message
     *            exception message to set
     * @param e
     *            {@code Throwable} to set
     */
    public PushClientException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }
}
