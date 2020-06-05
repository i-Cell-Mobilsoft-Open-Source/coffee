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
import java.util.ArrayList;
import java.util.List;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.dto.exception.enums.Severity;

/**
 * <p>BaseException class.</p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = 1L;

    private Enum<?> faultType = CoffeeFaultType.OPERATION_FAILED;

    private Severity severity = Severity.CRITICAL;

    private List<Serializable> messageParameters;

    /**
     * <p>Constructor for BaseException.</p>
     */
    public BaseException(String message) {
        this(CoffeeFaultType.OPERATION_FAILED, message, null, null);
    }

    /**
     * <p>Constructor for BaseException.</p>
     */
    public BaseException(String message, Throwable e) {
        this(CoffeeFaultType.OPERATION_FAILED, message, e, null);
    }

    /**
     * <p>Constructor for BaseException.</p>
     */
    public BaseException(Enum<?> faultTypeEnum, String message) {
        this(faultTypeEnum, message, null, null);
    }

    /**
     * <p>Constructor for BaseException.</p>
     */
    public BaseException(Enum<?> faultTypeEnum, String message, Throwable e) {
        this(faultTypeEnum, message, e, null);
    }

    /**
     * <p>Constructor for BaseException.</p>
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
     */
    public void addMessageParameter(Serializable messageParameter) {
        getMessageParameters().add(messageParameter);
    }

    /**
     * <p>getFaultTypeEnum.</p>
     */
    public Enum<?> getFaultTypeEnum() {
        return faultType;
    }

    /**
     * <p>Setter for the field <code>faultType</code>.</p>
     */
    public void setFaultType(Enum<?> faultTypeEnum) {
        if (faultTypeEnum != null) {
            this.faultType = faultTypeEnum;
        }
    }

    /**
     * <p>Getter for the field <code>severity</code>.</p>
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * <p>Setter for the field <code>severity</code>.</p>
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    /**
     * <p>Getter for the field <code>messageParameters</code>.</p>
     */
    public List<Serializable> getMessageParameters() {
        if (messageParameters == null) {
            messageParameters = new ArrayList<Serializable>();
        }
        return messageParameters;
    }

    /**
     * <p>Setter for the field <code>messageParameters</code>.</p>
     */
    public void setMessageParameters(List<Serializable> messageParameters) {
        this.messageParameters = messageParameters;
    }
}
