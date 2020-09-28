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
 * Amikor az alkalmazas tudatosan ugy van beallitva hogy elutasitsa a feldolgozast
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class ServiceUnavailableException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * message + throwable constructor
     *
     * @param message
     *            message
     * @param e
     *            message
     */
    public ServiceUnavailableException(String message, Throwable e) {
        super(CoffeeFaultType.SERVICE_UNAVAILABLE, message, e);
    }

    /**
     * message constructor
     *
     * @param message
     *            message
     */
    public ServiceUnavailableException(String message) {
        super(CoffeeFaultType.SERVICE_UNAVAILABLE, message);
    }

    /**
     * constructor
     */
    public ServiceUnavailableException() {
        this("The REST endpoint is down by configuration");
    }
}
