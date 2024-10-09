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

import hu.icellmobilsoft.coffee.dto.exception.BaseExceptionWrapper;

/**
 * An exception class to enable throwing a custom exception from MessageBodyReader/MessageBodyWriter classes.
 *
 * @author attila.nyers
 * @author ferenc.lutischan
 * @since 1.0.0
 *
 * @deprecated Use {@link BaseProcessingExceptionWrapper} instead, forRemoval = true, since = "1.13.0"
 */
@Deprecated(forRemoval = true, since = "1.13.0")
public class XsdProcessingExceptionWrapper extends ProcessingException implements BaseExceptionWrapper<XsdProcessingException> {

    private static final long serialVersionUID = 1L;

    /**
     * The wrapped exception
     */
    private XsdProcessingException exception;

    /**
     * Wraps an exception of type BaseException.
     *
     * @param cause
     *            Az eredeti BaseException
     */
    public XsdProcessingExceptionWrapper(XsdProcessingException cause) {
        super(cause);
        this.exception = cause;
    }

    /** {@inheritDoc} */
    @Override
    public void setException(XsdProcessingException exception) {
        this.exception = exception;
    }

    /** {@inheritDoc} */
    @Override
    public XsdProcessingException getException() {
        return exception;
    }
}
