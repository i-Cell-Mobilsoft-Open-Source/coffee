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
package hu.icellmobilsoft.coffee.rest.action;

import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.common.KeyValueBasicType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseRequestType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResultType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * Base class for all other business logic action class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class AbstractBaseAction {

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractBaseAction() {
        super();
    }

    /**
     * Creates a new {@link ContextType} for responses.
     *
     * @return {@code ContextType} object
     */
    // A context.timestamp vegett nem lehet az Coffee-ben (Date vs OffsetDateTime)
    public abstract ContextType createContext();

    /**
     * Creates new {@link BaseResponse} for responses.
     *
     * @return {@code BaseResponse} object
     */
    public BaseResponse createBaseResponse() {
        BaseResponse baseResponse = new BaseResponse();
        handleSuccessResultType(baseResponse);
        return baseResponse;
    }

    /**
     * Creates a new {@link BaseResponse} for responses with specific context.
     *
     * @param context
     *            specific context
     * @return {@code BaseResponse} object
     */
    public BaseResponse createBaseResponse(ContextType context) {
        BaseResponse baseResponse = new BaseResponse();
        handleSuccessResultType(baseResponse, context);
        return baseResponse;
    }

    /**
     * Marks {@link BaseResultType} to success. If context not exists, creating new.
     *
     * @param baseResultType
     *            object to mark
     */
    public void handleSuccessResultType(BaseResultType baseResultType) {
        handleResultType(baseResultType, null, FunctionCodeType.OK);
    }

    /**
     * Marks {@link BaseResultType} to success with request context.
     *
     * @param baseResultType
     *            result
     * @param baseRequestType
     *            request
     */
    public void handleSuccessResultType(BaseResultType baseResultType, BaseRequestType baseRequestType) {
        baseResultType.setContext(baseRequestType.getContext());
        handleSuccessResultType(baseResultType);
    }

    /**
     * Marks given {@link BaseResultType} to success with specific context.
     *
     * @param baseResultType
     *            result
     * @param context
     *            specific context
     */
    public void handleSuccessResultType(BaseResultType baseResultType, ContextType context) {
        baseResultType.setContext(context);
        handleSuccessResultType(baseResultType);
    }

    /**
     * Marks given {@link BaseResultType} to fail. If context does not exist, creates a new one.
     *
     * @param baseResultType
     *            given {@link BaseResultType}
     * @param message
     *            result message
     */
    public void handleUnsuccessResultType(BaseResultType baseResultType, String message) {
        handleResultType(baseResultType, message, FunctionCodeType.ERROR);

    }

    private void handleResultType(BaseResultType baseResultType, String message, FunctionCodeType functionCodeType) {
        if (baseResultType.getContext() == null) {
            baseResultType.setContext(createContext());
        }
        baseResultType.setFuncCode(functionCodeType);
        baseResultType.setMessage(message);
    }

    /**
     * Creates custom {@link KeyValueBasicType} object with input key and value.
     *
     * @param key
     *            key
     * @param value
     *            value
     * @return new {@code KeyValueBasicType}
     */
    public static KeyValueBasicType getKeyValue(String key, String value) {
        KeyValueBasicType param = new KeyValueBasicType();
        param.setKey(key);
        param.setValue(value);
        return param;
    }

    /**
     * Creates new exception object for invalid parameter.
     *
     * @param msg
     *            exception message
     * @return new Exception object
     * @deprecated Use {@link InvalidParameterException} instead, forRemoval = true, since = "1.13.0"
     */
    @Deprecated(forRemoval = true, since = "1.13.0")
    public static BaseException newInvalidParameterException(String msg) {
        return new BaseException(CoffeeFaultType.INVALID_INPUT, msg);
    }
}
