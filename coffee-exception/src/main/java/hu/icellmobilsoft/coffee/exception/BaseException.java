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
package hu.icellmobilsoft.coffee.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.icellmobilsoft.coffee.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.enums.Severity;

/**
 * <p>
 * BaseException class.
 * </p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * The fault type of the exception.
     *
     * @see CoffeeFaultType
     * @see hu.icellmobilsoft.coffee.exception.annotation.FaultTypeCode
     */
    private Enum<?> faultType = CoffeeFaultType.OPERATION_FAILED;

    /**
     * The severity of the exception.
     */
    private Severity severity = Severity.CRITICAL;

    /**
     * The parameters of the exception message.
     */
    private List<Serializable> messageParameters;

    /**
     * Constructor for BaseException.
     *
     * @param message
     *            message
     */
    public BaseException(String message) {
        this(CoffeeFaultType.OPERATION_FAILED, message, null, null);
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
        this(CoffeeFaultType.OPERATION_FAILED, message, e, null);
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
        super(message, e);
        if (faultTypeEnum != null) {
            this.faultType = faultTypeEnum;
        }
        this.severity = severity;
    }

    /**
     * Add one message parameter to the message parameter list.
     *
     * @param messageParameter
     *            messageParameter
     */
    public void addMessageParameter(Serializable messageParameter) {
        getMessageParameters().add(messageParameter);
    }

    /**
     * getFaultTypeEnum.
     *
     * @return faultType
     */
    public Enum<?> getFaultTypeEnum() {
        return faultType;
    }

    /**
     * Setter for the field <code>faultType</code>.
     *
     * @param faultTypeEnum
     *            faultTypeEnum
     */
    public void setFaultType(Enum<?> faultTypeEnum) {
        if (faultTypeEnum != null) {
            this.faultType = faultTypeEnum;
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

    /**
     * Getter for the field <code>messageParameters</code>.
     *
     * @return messageParameters
     */
    public List<Serializable> getMessageParameters() {
        if (messageParameters == null) {
            messageParameters = new ArrayList<Serializable>();
        }
        return messageParameters;
    }

    /**
     * Setter for the field <code>messageParameters</code>.
     *
     * @param messageParameters
     *            messageParameters
     */
    public void setMessageParameters(List<Serializable> messageParameters) {
        this.messageParameters = messageParameters;
    }
}
