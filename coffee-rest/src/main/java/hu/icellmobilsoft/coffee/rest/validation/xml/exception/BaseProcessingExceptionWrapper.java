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
package hu.icellmobilsoft.coffee.rest.validation.xml.exception;

import jakarta.ws.rs.ProcessingException;

import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.BaseExceptionWrapper;

/**
 * Exception for wrapping and throwing {@link BaseException} as {@link ProcessingException}.
 *
 * @author csaba.balogh
 * @since 1.13.0
 */
public class BaseProcessingExceptionWrapper extends ProcessingException implements BaseExceptionWrapper<BaseException> {

    private static final long serialVersionUID = 1L;

    /**
     * The wrapped exception
     */
    private BaseException exception;

    /**
     * Constructor for wrapping {@link BaseException}.
     *
     * @param exception
     *            the original {@link BaseException}.
     */
    public BaseProcessingExceptionWrapper(BaseException exception) {
        super(exception);
        this.exception = exception;
    }

    /** {@inheritDoc} */
    @Override
    public void setException(BaseException exception) {
        this.exception = exception;
    }

    /** {@inheritDoc} */
    @Override
    public BaseException getException() {
        return exception;
    }
}
