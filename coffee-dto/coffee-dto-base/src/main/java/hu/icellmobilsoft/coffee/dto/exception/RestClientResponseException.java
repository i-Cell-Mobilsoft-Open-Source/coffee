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
import hu.icellmobilsoft.coffee.dto.exception.enums.Severity;

/**
 * Exception for mp rest client response exception mapping.
 *
 * @author adam.magyari
 * @since 1.2.0
 */
public class RestClientResponseException extends BaseException {

    private String service;
    private String className;
    private String exception;
    private int statusCode;

    public RestClientResponseException(String message) {
        super(CoffeeFaultType.REST_CLIENT_EXCEPTION, message);
    }

    public RestClientResponseException(String message, Throwable e) {
        super(CoffeeFaultType.REST_CLIENT_EXCEPTION, message, e);
    }

    public RestClientResponseException(Enum<?> faultTypeEnum, String message) {
        super(faultTypeEnum, message);
    }

    public RestClientResponseException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }

    public RestClientResponseException(Enum<?> faultTypeEnum, String message, Throwable e, Severity severity) {
        super(faultTypeEnum, message, e, severity);
    }

    /**
     * Getter for the field <code>className</code>.
     *
     * @return className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Setter for the field <code>className</code>.
     *
     * @param className
     *            className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Getter for the field <code>exception</code>.
     *
     * @return exception
     */
    public String getException() {
        return exception;
    }

    /**
     * Setter for the field <code>exception</code>.
     *
     * @param exception
     *            exception
     */
    public void setException(String exception) {
        this.exception = exception;
    }

    /**
     * Getter for the field <code>service</code>.
     *
     * @return service
     */
    public String getService() {
        return service;
    }

    /**
     * Setter for the field <code>service</code>.
     *
     * @param service
     *            service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Getter for the field <code>service</code>.
     *
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Setter for the field <code>service</code>.
     *
     * @param statusCode
     *            the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
