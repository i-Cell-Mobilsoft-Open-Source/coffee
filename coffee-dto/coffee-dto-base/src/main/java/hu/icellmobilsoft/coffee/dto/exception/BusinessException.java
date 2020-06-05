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
 * Expected business Exception
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for BusinessException.</p>
     */
    public BusinessException(String message) {
        this(CoffeeFaultType.OPERATION_FAILED, message, null);
    }

    /**
     * <p>Constructor for BusinessException.</p>
     */
    public BusinessException(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null);
    }

    /**
     * <p>Constructor for BusinessException.</p>
     */
    public BusinessException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }

}
