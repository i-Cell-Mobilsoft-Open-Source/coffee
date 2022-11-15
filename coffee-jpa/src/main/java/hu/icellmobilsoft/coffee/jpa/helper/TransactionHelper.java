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
package hu.icellmobilsoft.coffee.jpa.helper;

import java.util.Objects;

import javax.enterprise.context.Dependent;

import org.apache.deltaspike.jpa.api.transaction.Transactional;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction3;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction4;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction5;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction6;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionSupplier;

/**
 * Helper class for managing transactions. <br>
 * This class is an alternative solution based on {@link org.apache.deltaspike.jpa.api.transaction.TransactionHelper}.
 *
 * @author csaba.balogh
 * @since 1.12.0
 */
@Dependent
public class TransactionHelper {

    /**
     * Execute the given {@link BaseExceptionSupplier} inside a {@link Transactional} block.
     *
     * @param baseExceptionSupplier
     *            the given {@link BaseExceptionSupplier}.
     * @return the return value of the executed {@link BaseExceptionSupplier}.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <R> R executeWithTransaction(BaseExceptionSupplier<R> baseExceptionSupplier) throws BaseException {
        if (Objects.isNull(baseExceptionSupplier)) {
            throw new InvalidParameterException("baseExceptionSupplier is NULL!");
        }
        return baseExceptionSupplier.get();
    }

    /**
     * Execute the given {@link BaseExceptionRunner} inside a {@link Transactional} block.
     * 
     * @param baseExceptionRunner
     *            the given {@link BaseExceptionRunner}.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public void executeWithTransaction(BaseExceptionRunner baseExceptionRunner) throws BaseException {
        if (Objects.isNull(baseExceptionRunner)) {
            throw new InvalidParameterException("baseExceptionRunner is NULL!");
        }
        baseExceptionRunner.run();
    }

    /**
     * Execute the given {@link BaseExceptionFunction} inside a {@link Transactional} block.
     * 
     * @param baseExceptionFunction
     *            the given {@link BaseExceptionFunction}.
     * @param p1
     *            the first parameter.
     * @return the return value of the executed {@link BaseExceptionFunction}.
     * @param <P1>
     *            the type of the first parameter.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <P1, R> R executeWithTransaction(BaseExceptionFunction<P1, R> baseExceptionFunction, P1 p1) throws BaseException {
        if (Objects.isNull(baseExceptionFunction)) {
            throw new InvalidParameterException("baseExceptionFunction is NULL!");
        }
        return baseExceptionFunction.apply(p1);
    }

    /**
     * Execute the given {@link BaseExceptionFunction2} inside a {@link Transactional} block.
     * 
     * @param baseExceptionFunction
     *            the given {@link BaseExceptionFunction2}.
     * @param p1
     *            the first parameter.
     * @param p2
     *            the second parameter.
     * @return the return value of the executed {@link BaseExceptionFunction2}.
     * @param <P1>
     *            the type of the first parameter.
     * @param <P2>
     *            the type of the second parameter.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <P1, P2, R> R executeWithTransaction(BaseExceptionFunction2<P1, P2, R> baseExceptionFunction, P1 p1, P2 p2) throws BaseException {
        if (Objects.isNull(baseExceptionFunction)) {
            throw new InvalidParameterException("baseExceptionFunction is NULL!");
        }
        return baseExceptionFunction.apply(p1, p2);
    }

    /**
     * Execute the given {@link BaseExceptionFunction3} inside a {@link Transactional} block.
     * 
     * @param baseExceptionFunction
     *            he given {@link BaseExceptionFunction3}.
     * @param p1
     *            the first parameter.
     * @param p2
     *            the second parameter.
     * @param p3
     *            the third parameter.
     * @return the return value of the executed {@link BaseExceptionFunction3}.
     * @param <P1>
     *            the type of the first parameter.
     * @param <P2>
     *            the type of the second parameter.
     * @param <P3>
     *            the type of the third parameter.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <P1, P2, P3, R> R executeWithTransaction(BaseExceptionFunction3<P1, P2, P3, R> baseExceptionFunction, P1 p1, P2 p2, P3 p3)
            throws BaseException {
        if (Objects.isNull(baseExceptionFunction)) {
            throw new InvalidParameterException("baseExceptionFunction is NULL!");
        }
        return baseExceptionFunction.apply(p1, p2, p3);
    }

    /**
     * Execute the given {@link BaseExceptionFunction4} inside a {@link Transactional} block.
     * 
     * @param baseExceptionFunction
     *            he given {@link BaseExceptionFunction4}.
     * @param p1
     *            the first parameter.
     * @param p2
     *            the second parameter.
     * @param p3
     *            the third parameter.
     * @param p4
     *            the fourth parameter.
     * @return the return value of the executed {@link BaseExceptionFunction4}.
     * @param <P1>
     *            the type of the first parameter.
     * @param <P2>
     *            the type of the second parameter.
     * @param <P3>
     *            the type of the third parameter.
     * @param <P4>
     *            the type of the fourth parameter.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <P1, P2, P3, P4, R> R executeWithTransaction(BaseExceptionFunction4<P1, P2, P3, P4, R> baseExceptionFunction, P1 p1, P2 p2, P3 p3, P4 p4)
            throws BaseException {
        if (Objects.isNull(baseExceptionFunction)) {
            throw new InvalidParameterException("baseExceptionFunction is NULL!");
        }
        return baseExceptionFunction.apply(p1, p2, p3, p4);
    }

    /**
     * Execute the given {@link BaseExceptionFunction5} inside a {@link Transactional} block.
     * 
     * @param baseExceptionFunction
     *            he given {@link BaseExceptionFunction5}.
     * @param p1
     *            the first parameter.
     * @param p2
     *            the second parameter.
     * @param p3
     *            the third parameter.
     * @param p4
     *            the fourth parameter.
     * @param p5
     *            the fifth parameter.
     * @return the return value of the executed {@link BaseExceptionFunction5}.
     * @param <P1>
     *            the type of the first parameter.
     * @param <P2>
     *            the type of the second parameter.
     * @param <P3>
     *            the type of the third parameter.
     * @param <P4>
     *            the type of the fourth parameter.
     * @param <P5>
     *            the type of the fifth parameter.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <P1, P2, P3, P4, P5, R> R executeWithTransaction(BaseExceptionFunction5<P1, P2, P3, P4, P5, R> baseExceptionFunction, P1 p1, P2 p2, P3 p3,
            P4 p4, P5 p5) throws BaseException {
        if (Objects.isNull(baseExceptionFunction)) {
            throw new InvalidParameterException("baseExceptionFunction is NULL!");
        }
        return baseExceptionFunction.apply(p1, p2, p3, p4, p5);
    }

    /**
     * Execute the given {@link BaseExceptionFunction6} inside a {@link Transactional} block.
     * 
     * @param baseExceptionFunction
     *            he given {@link BaseExceptionFunction6}.
     * @param p1
     *            the first parameter.
     * @param p2
     *            the second parameter.
     * @param p3
     *            the third parameter.
     * @param p4
     *            the fourth parameter.
     * @param p5
     *            the fifth parameter.
     * @param p6
     *            the sixth parameter.
     * @return the return value of the executed {@link BaseExceptionFunction6}.
     * @param <P1>
     *            the type of the first parameter.
     * @param <P2>
     *            the type of the second parameter.
     * @param <P3>
     *            the type of the third parameter.
     * @param <P4>
     *            the type of the fourth parameter.
     * @param <P5>
     *            the type of the fifth parameter.
     * @param <P6>
     *            the type of the sixth parameter.
     * @param <R>
     *            the type of the return value.
     * @throws BaseException
     *             in case of any exception occurs during the process.
     */
    @Transactional
    public <P1, P2, P3, P4, P5, P6, R> R executeWithTransaction(BaseExceptionFunction6<P1, P2, P3, P4, P5, P6, R> baseExceptionFunction, P1 p1, P2 p2,
            P3 p3, P4 p4, P5 p5, P6 p6) throws BaseException {
        if (Objects.isNull(baseExceptionFunction)) {
            throw new InvalidParameterException("baseExceptionFunction is NULL!");
        }
        return baseExceptionFunction.apply(p1, p2, p3, p4, p5, p6);
    }

}
