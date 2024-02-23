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
 * AccessDenied if login failed
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class AccessDeniedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for AccessDeniedException.
     *
     * @param message
     *            message
     */
    public AccessDeniedException(String message) {
        this(CoffeeFaultType.NOT_AUTHORIZED, message);
    }

    /**
     * Constructor for AccessDeniedException.
     *
     * @param message
     *            message
     * @param e
     *            e
     */
    public AccessDeniedException(String message, Throwable e) {
        this(CoffeeFaultType.NOT_AUTHORIZED, message, e);
    }

    /**
     * Constructor for AccessDeniedException.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     */
    public AccessDeniedException(Enum<?> faultTypeEnum, String message) {
        super(faultTypeEnum, message);
    }

    /**
     * Constructor for AccessDeniedException.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     * @param e
     *            e
     */
    public AccessDeniedException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }
}
