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
package hu.icellmobilsoft.coffee.exception;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * Exception for optimistic lock.
 * 
 * @author gabor.balazs
 * @since 2.0.0
 */
public class OptimisticLockException extends TechnicalException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for {@link OptimisticLockException} with given exception message. The default fault type is
     * {@link CoffeeFaultType#OPTIMISTIC_LOCK_EXCEPTION}.
     *
     * @param message
     *            the exception message.
     */
    public OptimisticLockException(String message) {
        super(CoffeeFaultType.OPTIMISTIC_LOCK_EXCEPTION, message);
    }

    /**
     * Constructor for {@link OptimisticLockException} with given exception message, fault type and throwable.
     *
     * @param faultTypeEnum
     *            the fault type.
     * @param message
     *            the exception message.
     * @param e
     *            the {@link Throwable}.
     */
    public OptimisticLockException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }
}
