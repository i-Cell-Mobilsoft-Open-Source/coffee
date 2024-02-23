/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.csv.localization;

import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.BaseExceptionWrapper;

/**
 * Exception thrown by the {@link LocalizationConverter}
 * 
 * @author martin.nagy
 * @since 1.8.0
 */
public class ConverterException extends RuntimeException implements BaseExceptionWrapper<BaseException> {

    /**
     * Creates the exception with the given message
     * 
     * @param message
     *            the exception message
     */
    public ConverterException(String message) {
        super(new BaseException(message));
    }

    @Override
    public void setException(BaseException exception) {
        initCause(exception);
    }

    @Override
    public BaseException getException() {
        return (BaseException) getCause();
    }
}
