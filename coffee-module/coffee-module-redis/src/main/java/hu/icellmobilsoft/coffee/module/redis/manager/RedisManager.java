/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.manager;

import java.text.MessageFormat;
import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction3;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction4;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction5;
import redis.clients.jedis.Jedis;

/**
 * Redis műveleteket összefogó osztaly
 * 
 * 
 * @author czenczl
 * @since 1.7.0
 *
 */
@Dependent
public class RedisManager {

    public static final String JEDIS_NOT_INITIALIZED_MSG = "jedis is not initialized";

    @Inject
    private Logger log;

    private String configKey;
    private Instance<Jedis> jedisInstance;
    private Jedis jedis;

    /**
     * Adds log entry for method enter if trace is enabled.
     *
     * @param functionName
     *            method info format {@link String} with method and param names, and placeholders for param values
     * @param params
     *            params for redis function
     */
    protected void logEnter(String functionName, Object... params) {
        if (log.isTraceEnabled()) {
            log.trace(">>" + getCalledMethodWithParamsType(functionName, params));
        }
    }

    /**
     * Adds log entry for method return if trace is enabled.
     * 
     * @param functionName
     *            referenced function name
     * @param params
     *            params for redis function
     */
    protected void logReturn(String functionName, Object... params) {
        if (log.isTraceEnabled()) {
            log.trace("<<" + getCalledMethodWithParamsType(functionName, params));
        }
    }

    /**
     * Build string from functionName and parameters
     * 
     * @param functionName
     *            called redis function name
     * @param params
     *            params for redis function
     * @return concatenated log string
     */
    protected String getCalledMethodWithParamsType(String functionName, Object... params) {
        StringBuilder functionInfo = new StringBuilder(" ").append(functionName).append("(");
        int index = 0;
        if (params != null) {
            for (Object param : params) {
                if (index > 0) {
                    functionInfo.append(", ");
                }
                if (param != null) {
                    functionInfo.append(param.getClass().getTypeName());
                } else {
                    functionInfo.append(param);
                }
                index++;
            }
        }
        return functionInfo.toString() + ")";
    }

    /**
     * Creates {@link TechnicalException} with {@link CoffeeFaultType#REDIS_OPERATION_FAILED} fault type.
     *
     * @param e
     *            cause {@link Exception}
     * @param functionInfo
     *            method info format {@link String} with method and param type names
     * @return {@link TechnicalException} with message from functionInfo.
     */
    protected TechnicalException repositoryFailed(Exception e, String functionInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error occurred when calling redis operation ").append(functionInfo).append(" : [").append(e.getLocalizedMessage()).append("]");
        String msg = MessageFormat.format(sb.toString(), functionInfo);
        return new TechnicalException(CoffeeFaultType.REDIS_OPERATION_FAILED, msg, e);
    }

    /**
     * Runs the business logic method call in order to handle common logging and exception handling.
     * 
     * @param <R>
     *            response object type
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <R> Optional<R> run(BaseExceptionFunction<Jedis, R> function, String functionName) throws BaseException {
        if (jedis == null) {
            throw new TechnicalException(CoffeeFaultType.REDIS_OPERATION_FAILED, JEDIS_NOT_INITIALIZED_MSG);
        }
        logEnter(functionName);
        try {
            R response = function.apply(jedis);
            logReturn(functionName);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            throw repositoryFailed(e, functionName);
        }
    }

    /**
     * Runs the business logic method call in order to handle common logging and exception handling.
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter type of the function call
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, R> Optional<R> run(BaseExceptionFunction2<Jedis, P1, R> function, String functionName, P1 p1) throws BaseException {
        if (jedis == null) {
            throw new TechnicalException(CoffeeFaultType.REDIS_OPERATION_FAILED, JEDIS_NOT_INITIALIZED_MSG);
        }
        logEnter(functionName, p1);
        if (isNullOrBlankAnyParameter(p1)) {
            throw newInvalidParameterException(functionName);
        }
        try {
            R response = function.apply(jedis, p1);
            logReturn(functionName, p1.getClass().getTypeName());
            return Optional.ofNullable(response);
        } catch (Exception e) {
            throw repositoryFailed(e, functionName);
        }
    }

    /**
     * Runs the business logic method call in order to handle common logging and exception handling.
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter type of the function call
     * @param <P2>
     *            second parameter type of the function call
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @param p2
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, P2, R> Optional<R> run(BaseExceptionFunction3<Jedis, P1, P2, R> function, String functionName, P1 p1, P2 p2) throws BaseException {
        if (jedis == null) {
            throw new TechnicalException(CoffeeFaultType.REDIS_OPERATION_FAILED, JEDIS_NOT_INITIALIZED_MSG);
        }
        logEnter(functionName, p1, p2);
        if (isNullOrBlankAnyParameter(p1, p2)) {
            throw newInvalidParameterException(functionName);
        }
        try {
            R response = function.apply(jedis, p1, p2);
            logReturn(functionName, p1);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            throw repositoryFailed(e, functionName);
        }
    }

    /**
     * Runs the business logic method call in order to handle common logging and exception handling.
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter type of the function call
     * @param <P2>
     *            second parameter type of the function call
     * @param <P3>
     *            third parameter type of the function call
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @param p2
     *            parameter of the function call
     * @param p3
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, P2, P3, R> Optional<R> run(BaseExceptionFunction4<Jedis, P1, P2, P3, R> function, String functionName, P1 p1, P2 p2, P3 p3)
            throws BaseException {
        if (jedis == null) {
            throw new TechnicalException(CoffeeFaultType.REDIS_OPERATION_FAILED, JEDIS_NOT_INITIALIZED_MSG);
        }
        logEnter(functionName, p1, p2, p3);
        if (isNullOrBlankAnyParameter(p1, p2, p3)) {
            throw newInvalidParameterException(functionName);
        }
        try {
            R response = function.apply(jedis, p1, p2, p3);
            logReturn(functionName, p1, p2, p3);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            throw repositoryFailed(e, functionName);
        }
    }

    /**
     * Runs the business logic method call in order to handle common logging and exception handling.
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter type of the function call
     * @param <P2>
     *            second parameter type of the function call
     * @param <P3>
     *            third parameter type of the function call
     * @param <P4>
     *            fourth parameter type of the function call
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @param p2
     *            parameter of the function call
     * @param p3
     *            parameter of the function call
     * @param p4
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, P2, P3, P4, R> Optional<R> run(BaseExceptionFunction5<Jedis, P1, P2, P3, P4, R> function, String functionName, P1 p1, P2 p2, P3 p3,
            P4 p4) throws BaseException {
        if (jedis == null) {
            throw new TechnicalException(CoffeeFaultType.REDIS_OPERATION_FAILED, JEDIS_NOT_INITIALIZED_MSG);
        }
        logEnter(functionName, p1, p2, p3, p4);
        if (isNullOrBlankAnyParameter(p1, p2, p3, p4)) {
            throw newInvalidParameterException(functionName);
        }
        try {
            R response = function.apply(jedis, p1, p2, p3, p4);
            logReturn(functionName, p1);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            throw repositoryFailed(e, functionName);
        }
    }

    /**
     * Runs the business logic method call in order to handle jedis connection init and close, common logging and exception handling.
     * 
     * @param <R>
     *            response object type
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <R> Optional<R> runWithConnection(BaseExceptionFunction<Jedis, R> function, String functionName) throws BaseException {
        try {
            initConnection();
            return run(function, functionName);
        } finally {
            closeConnection();
        }
    }

    /**
     * Runs the business logic method call in order to handle jedis connection init and close, common logging and exception handling with one
     * parameter
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, R> Optional<R> runWithConnection(BaseExceptionFunction2<Jedis, P1, R> function, String functionName, P1 p1) throws BaseException {
        try {
            initConnection();
            return run(function, functionName, p1);
        } finally {
            closeConnection();
        }
    }

    /**
     * Runs the business logic method call in order to handle jedis connection init and close, common logging and exception handling with two
     * parameters
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter
     * @param <P2>
     *            second parameter
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @param p2
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, P2, R> Optional<R> runWithConnection(BaseExceptionFunction3<Jedis, P1, P2, R> function, String functionName, P1 p1, P2 p2)
            throws BaseException {
        try {
            initConnection();
            return run(function, functionName, p1, p2);
        } finally {
            closeConnection();
        }
    }

    /**
     * Runs the business logic method call in order to handle jedis connection init and close, common logging and exception handling with three
     * parameters
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter
     * @param <P2>
     *            second parameter
     * @param <P3>
     *            third parameter
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @param p2
     *            parameter of the function call
     * @param p3
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, P2, P3, R> Optional<R> runWithConnection(BaseExceptionFunction4<Jedis, P1, P2, P3, R> function, String functionName, P1 p1, P2 p2,
            P3 p3) throws BaseException {
        try {
            initConnection();
            return run(function, functionName, p1, p2, p3);
        } finally {
            closeConnection();
        }
    }

    /**
     * Runs the business logic method call in order to handle jedis connection init and close, common logging and exception handling with four
     * parameters
     * 
     * @param <R>
     *            response object type
     * @param <P1>
     *            first parameter
     * @param <P2>
     *            second parameter
     * @param <P3>
     *            third parameter
     * @param <P4>
     *            fourth parameter
     * @param function
     *            the function doing business logic
     * @param functionName
     *            the function name
     * @param p1
     *            parameter of the function call
     * @param p2
     *            parameter of the function call
     * @param p3
     *            parameter of the function call
     * @param p4
     *            parameter of the function call
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    public <P1, P2, P3, P4, R> Optional<R> runWithConnection(BaseExceptionFunction5<Jedis, P1, P2, P3, P4, R> function, String functionName, P1 p1,
            P2 p2, P3 p3, P4 p4) throws BaseException {
        try {
            initConnection();
            return run(function, functionName, p1, p2, p3, p4);
        } finally {
            closeConnection();
        }
    }

    /**
     * Initialize jedis
     */
    public void initConnection() {
        if (jedis == null) {
            jedisInstance = CDI.current().select(Jedis.class, new RedisConnection.Literal(configKey));
            jedis = jedisInstance.get();
        }
    }

    /**
     * Close the initialized jedis
     */
    public void closeConnection() {
        if (jedis != null) {
            jedisInstance.destroy(jedis);
        }
    }

    /**
     * set config key for jedis initialization
     * 
     * @param configKey
     *            for jedis initialization
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private BaseException newInvalidParameterException(String functionName) {
        return new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS,
                "At least one incoming parameter in " + functionName + " is null or blank!");
    }

    private boolean isNullOrBlankAnyParameter(Object... params) {
        for (Object param : params) {
            if (param == null || (param instanceof String && StringUtils.isBlank((String) param))) {
                return true;
            }
        }
        return false;
    }

}
