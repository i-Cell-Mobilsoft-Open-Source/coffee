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
package hu.icellmobilsoft.coffee.rest.rest;

import javax.inject.Inject;
import javax.ws.rs.PathParam;

import org.apache.deltaspike.core.util.ProxyUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction3;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction4;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionSupplier;

/**
 * Base REST service for all REST endpoint
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public abstract class BaseRestService {

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * BaseException dobasra hasznaljuk. Hogyha a parameter BaseException tipusu akkor tovabb adja, egyebkent letrehoz egy TechnicalException-t
     *
     * @param e
     *            Exception
     * @return BaseException
     */
    protected BaseException baseException(Exception e) {
        if (e instanceof BaseException) {
            return (BaseException) e;
        } else {
            return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>baseExceptionWithLogging.</p>
     */
    protected BaseException baseExceptionWithLogging(Exception e) {
        log.error(getOriginalClassName() + ": " + e.getLocalizedMessage(), e);
        return baseException(e);
    }

    /**
     * Wraps call to parameter-less method
     *
     * @param supplier
     *            the function doing the real business logic
     * @param methodName
     *            the REST method name to log
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <RESPONSE> RESPONSE wrapNoParam(BaseExceptionSupplier<RESPONSE> supplier, String methodName) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName);
        logEnter(methodInfo);
        try {
            return supplier.get();
        } finally {
            logReturn(methodInfo);
        }
    }

    /**
     * Wraps call to a one-parameter method to be called with null parameter
     *
     * @param function
     *            the function doing the real business logic
     * @param methodName
     *            the REST method name to log
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <T, RESPONSE> RESPONSE wrapNullParam(BaseExceptionFunction<T, RESPONSE> function, String methodName) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName);
        logEnter(methodInfo);
        try {
            return function.apply(null);
        } finally {
            logReturn(methodInfo);
        }
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one {@link PathParam}
     *
     * @param function
     *            the function doing the real business logic
     * @param param
     *            parameter value to pass to the business logic
     * @param methodName
     *            the REST method name to log
     * @param paramName
     *            the REST method parameter name to log
     * @return what the function returns
     * @throws BaseException
     */
    protected <PARAM, RESPONSE> RESPONSE wrapPathParam1(BaseExceptionFunction<PARAM, RESPONSE> function, PARAM param, String methodName,
            String paramName) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, paramName);
        logEnter(methodInfo, param);
        try {
            return function.apply(param);
        } finally {
            logReturn(methodInfo, param);
        }
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two {@link PathParam}s
     *
     * @param function
     *            the function doing the real business logic
     * @param param
     *            parameter value to pass to the business logic
     * @param methodName
     *            the REST method name to log
     * @param paramName
     *            the REST method parameter name to log
     * @return what the function returns
     * @throws BaseException
     */
    protected <PARAM1, PARAM2, RESPONSE> RESPONSE wrapPathParam2(BaseExceptionFunction2<PARAM1, PARAM2, RESPONSE> function, PARAM1 param1,
            PARAM2 param2, String methodName, String param1Name, String param2Name) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, param1Name, param2Name);
        logEnter(methodInfo, param1, param2);
        try {
            return function.apply(param1, param2);
        } finally {
            logReturn(methodInfo, param1, param2);
        }
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three {@link PathParam}s
     *
     * @param function
     *            the function doing the real business logic
     * @param param
     *            parameter value to pass to the business logic
     * @param methodName
     *            the REST method name to log
     * @param paramName
     *            the REST method parameter name to log
     * @return what the function returns
     * @throws BaseException
     */
    protected <PARAM1, PARAM2, PARAM3, RESPONSE> RESPONSE wrapPathParam3(BaseExceptionFunction3<PARAM1, PARAM2, PARAM3, RESPONSE> function,
            PARAM1 param1, PARAM2 param2, PARAM3 param3, String methodName, String param1Name, String param2Name, String param3Name)
            throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, param1Name, param2Name, param3Name);
        logEnter(methodInfo, param1, param2, param3);
        try {
            return function.apply(param1, param2, param3);
        } finally {
            logReturn(methodInfo, param1, param2, param3);
        }
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three {@link PathParam}s
     *
     * @param function
     *            the function doing the real business logic
     * @param param
     *            parameter value to pass to the business logic
     * @param methodName
     *            the REST method name to log
     * @param paramName
     *            the REST method parameter name to log
     * @return what the function returns
     * @throws BaseException
     */
    protected <PARAM1, PARAM2, PARAM3, PARAM4, RESPONSE> RESPONSE wrapPathParam4(
            BaseExceptionFunction4<PARAM1, PARAM2, PARAM3, PARAM4, RESPONSE> function, PARAM1 param1, PARAM2 param2, PARAM3 param3, PARAM4 param4,
            String methodName, String param1Name, String param2Name, String param3Name, String param4Name) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, param1Name, param2Name, param3Name, param4Name);
        logEnter(methodInfo, param1, param2, param3, param4);
        try {
            return function.apply(param1, param2, param3, param4);
        } finally {
            logReturn(methodInfo, param1, param2, param3, param4);
        }
    }

    /**
     * Returning log sign.
     *
     * @param methodInfo
     * @param params
     */
    protected void logReturn(String methodInfo, Object... params) {
        log.trace("<<" + methodInfo, params);
    }

    /**
     * Entering log sign.
     *
     * @param methodInfo
     * @param params
     */
    protected void logEnter(String methodInfo, Object... params) {
        log.trace(">>" + methodInfo, params);
    }

    /**
     * 
     * @param methodName
     *            the REST method name e.g getCustomerInfoByUserId
     * @param paramNames
     *            the REST param names of {@link PathParam}s e.g userId,balanceId
     * @return e.g. " getCustomerInfoByUserId(userId: [{0}])"
     */
    private String getCalledMethodWithOnlyPathParams(String methodName, String... paramNames) {
        return getCalledMethodWithParamsBase(methodName, paramNames) + ")";
    }

    /**
     * <p>getCalledMethodWithParamsBase.</p>
     */
    protected String getCalledMethodWithParamsBase(String methodName, String... paramNames) {
        StringBuilder methodInfo = new StringBuilder(" ").append(getOriginalClassName()).append(".").append(methodName).append("(");
        int index = 0;
        for (String paramName : paramNames) {
            if (index > 0) {
                methodInfo.append(", ");
            }
            methodInfo.append(paramName).append(": [{").append(index++).append("}]");
        }
        return methodInfo.toString();
    }

    /**
     * <p>getOriginalClassName.</p>
     */
    protected String getOriginalClassName() {
        return ProxyUtils.getUnproxiedClass(getClass()).getSimpleName();
    }
}
