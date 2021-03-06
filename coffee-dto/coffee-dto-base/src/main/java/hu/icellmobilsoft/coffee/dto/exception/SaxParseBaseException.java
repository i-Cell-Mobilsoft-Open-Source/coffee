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

import java.util.List;

/**
 * SaxParse interface base exception
 *
 * @author imre.scheffer
 * @since 1.0.0
 * @deprecated replaced by {@code XsdProcessingException}
 */
@Deprecated(since = "1.2.0", forRemoval = true)
public class SaxParseBaseException extends InvalidRequestException {

    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * Constructor for SaxParseBaseException.
     * </p>
     * 
     * @param message
     *            message
     * @param e
     *            e
     */
    public SaxParseBaseException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Constructor for SaxParseBaseException.
     *
     * @param errors
     *            errors
     */
    public SaxParseBaseException(List<XMLValidationError> errors) {
        super(errors);
    }

    /**
     * Constructor for SaxParseBaseException.
     * 
     * @param errors
     *            errors
     * @param e
     *            e
     */
    public SaxParseBaseException(List<XMLValidationError> errors, Throwable e) {
        super(errors, e);
    }

}
