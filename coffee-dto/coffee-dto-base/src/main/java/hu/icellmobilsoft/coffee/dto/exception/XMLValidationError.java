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

import java.io.Serializable;

/**
 * XSD marshaller error collector element error
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class XMLValidationError implements Serializable {

    private static final long serialVersionUID = -2523167059288742454L;

    private int lineNumber;

    private int columnNumber;

    private String error;

    /**
     * Getter for the field <code>error</code>.
     * 
     * @return error
     */
    public String getError() {
        return error;
    }

    /**
     * Setter for the field <code>error</code>.
     *
     * @param error
     *            error
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * Return the line number if available
     *
     * {@link javax.xml.bind.ValidationEventLocator#getLineNumber()}
     *
     * @return the line number or -1 if unavailable
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Setter for the field <code>lineNumber</code>.
     *
     * @param lineNumber
     *            lineNumber
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Return the column number if available
     * 
     * {@link javax.xml.bind.ValidationEventLocator#getColumnNumber()}
     *
     * @return the column number or -1 if unavailable
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Setter for the field <code>columnNumber</code>.
     * 
     * @param columnNumber
     *            columnNumber
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
}
