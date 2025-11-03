/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.se.api.exception.wrapper;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.enums.Severity;

/**
 * Exception for wrapping a {@link BaseException} so that it can be thrown as a {@link RuntimeException}. The cause of this exception will always be a
 * {@link BaseException}.
 *
 * @author attila-kiss-it
 * @since 2.12.0
 */
public class RuntimeBaseExceptionWrapper extends RuntimeException implements IBaseExceptionWrapper<BaseException> {

    /**
     * Constructor.
     *
     * @param e
     *            the {@link BaseException} to wrap and use as the cause
     */
    public RuntimeBaseExceptionWrapper(BaseException e) {
        super(e.getMessage(), e);
    }

    /**
     * Constructor. Creates a new {@link BaseException} with the given parameters and wraps it to be used as the cause.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     */
    public RuntimeBaseExceptionWrapper(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null, null);
    }

    /**
     * Constructor. Creates a new {@link BaseException} with the given parameters and wraps it to be used as the cause.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     * @param e
     *            e
     */
    public RuntimeBaseExceptionWrapper(Enum<?> faultTypeEnum, String message, Throwable e) {
        this(faultTypeEnum, message, e, null);
    }

    /**
     * Constructor. Creates a new {@link BaseException} with the given parameters and wraps it to be used as the cause.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     * @param e
     *            e
     * @param severity
     *            severity
     */
    public RuntimeBaseExceptionWrapper(Enum<?> faultTypeEnum, String message, Throwable e, Severity severity) {
        super(message, new BaseException(faultTypeEnum, message, e, severity));
    }

    @Override
    public BaseException getWrappedBaseException() {
        return (BaseException) getCause();
    }

}
