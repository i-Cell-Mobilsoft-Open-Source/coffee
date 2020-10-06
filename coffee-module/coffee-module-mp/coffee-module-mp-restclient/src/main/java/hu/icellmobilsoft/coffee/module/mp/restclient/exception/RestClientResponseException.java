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
package hu.icellmobilsoft.coffee.module.mp.restclient.exception;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
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

    public RestClientResponseException(String message) {
        super(message);
    }

    public RestClientResponseException(String message, Throwable e) {
        super(message, e);
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

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    /**
     * Creates {@link RestClientResponseException} from {@link BaseExceptionResultType}.
     *
     * @param baseExceptionResultType
     *          {@link BaseExceptionResultType}
     * @return
     *          {@link RestClientResponseException} initialized with data from input {@link BaseExceptionResultType}.
     */
    public static RestClientResponseException fromExceptionResult(BaseExceptionResultType baseExceptionResultType) {
        RestClientResponseException exception = new RestClientResponseException(FaultTypeParser.parseFaultType(baseExceptionResultType.getFaultType()), baseExceptionResultType.getMessage());
        exception.setService(baseExceptionResultType.getService());
        exception.setClassName(baseExceptionResultType.getClassName());
        exception.setException(baseExceptionResultType.getException());
        if (baseExceptionResultType.isSetCausedBy()) {
            exception.initCause(fromExceptionResult(baseExceptionResultType.getCausedBy()));
        }
        return exception;
    }
}
