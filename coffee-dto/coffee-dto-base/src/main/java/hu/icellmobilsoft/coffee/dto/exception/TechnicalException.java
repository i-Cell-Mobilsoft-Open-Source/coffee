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

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 *
 * Unexpected technical exception
 *
 * @deprecated Instead, use {@link hu.icellmobilsoft.coffee.se.api.exception.TechnicalException}.
 * 
 * @author imre.scheffer
 * @since 1.0.0
 */
@Deprecated(forRemoval = true, since = "2.8.0")
public class TechnicalException extends hu.icellmobilsoft.coffee.se.api.exception.TechnicalException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for TechnicalException.
     *
     * @param message
     *            message
     */
    public TechnicalException(String message) {
        this(CoffeeFaultType.REPOSITORY_FAILED, message, null);
    }

    /**
     * Constructor for TechnicalException.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     */
    public TechnicalException(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null);
    }

    /**
     * Constructor for TechnicalException.
     * 
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     * @param e
     *            e
     */
    public TechnicalException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }
}
