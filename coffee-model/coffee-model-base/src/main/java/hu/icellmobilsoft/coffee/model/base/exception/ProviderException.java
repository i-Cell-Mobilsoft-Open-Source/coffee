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
package hu.icellmobilsoft.coffee.model.base.exception;

import hu.icellmobilsoft.coffee.model.base.audit.AuditProvider;
import hu.icellmobilsoft.coffee.model.base.javatime.listener.TimestampsProvider;

/**
 * Exception class for {@link AuditProvider} and {@link TimestampsProvider}
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
public class ProviderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception with the given message
     *
     * @param message
     *            the exception message
     */
    public ProviderException(String message) {
        super(message);
    }

    /**
     * Creates the exception with the given message and cause
     * 
     * @param message
     *            the exception message
     * @param cause
     *            {@link Throwable}
     */
    public ProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
