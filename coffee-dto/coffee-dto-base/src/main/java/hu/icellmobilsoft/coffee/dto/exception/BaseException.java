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

import java.util.Objects;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.dto.exception.enums.Severity;

/**
 * <p>
 * BaseException class.
 * </p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Deprecated(since = "2.7.0")
public class BaseException extends hu.icellmobilsoft.coffee.se.api.exception.BaseException {

    private static final CoffeeFaultType DEFAULT_FAULT_TYPE = CoffeeFaultType.OPERATION_FAILED;

    private static final long serialVersionUID = 1L;

    private Severity severity = Severity.CRITICAL;

    /**
     * Constructor for BaseException.
     *
     * @param message
     *            message
     */
    public BaseException(String message) {
        this(DEFAULT_FAULT_TYPE, message, null, null);
    }

    /**
     * Constructor for BaseException.
     *
     * @param message
     *            message
     * @param e
     *            e
     */
    public BaseException(String message, Throwable e) {
        this(DEFAULT_FAULT_TYPE, message, e, null);
    }

    /**
     * Constructor for BaseException.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     */
    public BaseException(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null, null);
    }

    /**
     * Constructor for BaseException.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     * @param message
     *            message
     * @param e
     *            e
     */
    public BaseException(Enum<?> faultTypeEnum, String message, Throwable e) {
        this(faultTypeEnum, message, e, null);
    }

    /**
     * Constructor for BaseException.
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
    public BaseException(Enum<?> faultTypeEnum, String message, Throwable e, Severity severity) {
        super(faultTypeEnum, message, e);
        if (Objects.isNull(faultTypeEnum)) {
            setFaultType(DEFAULT_FAULT_TYPE);
        }
        this.severity = severity;
    }

    /**
     * Setter for the field <code>faultType</code>.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     */
    @Override
    public void setFaultType(Enum<?> faultTypeEnum) {
        if (Objects.nonNull(faultTypeEnum)) {
            super.setFaultType(faultTypeEnum);
        }
    }

    /**
     * Getter for the field <code>severity</code>.
     *
     * @return severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Setter for the field <code>severity</code>.
     *
     * @param severity
     *            severity
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

}
