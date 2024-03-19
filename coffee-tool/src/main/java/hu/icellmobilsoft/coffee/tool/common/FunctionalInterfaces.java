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
package hu.icellmobilsoft.coffee.tool.common;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Common class for @FunctionalInterface classes
 *
 * @deprecated A wrapper {@link FunctionalInterfaces} meg fog szünni, mert felesleges. A benne foglalt funkcionális interfészek helyett azok ős
 *             interfészeit kell használni a {@code coffee-se-function} modul {@code hu.icellmobilsoft.coffee.se.function} package-ből.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Deprecated(since = "2.7.0")
public class FunctionalInterfaces {

    /**
     * Default constructor, constructs a new object.
     */
    public FunctionalInterfaces() {
        super();
    }

    /**
     * This and the following ones are for a smart wrapping of all action calls inside the REST implementations
     *
     * @param <R>
     *            return object type
     */
    @FunctionalInterface
    public interface BaseExceptionSupplier<R> extends hu.icellmobilsoft.coffee.se.function.BaseExceptionSupplier<R> {
        /**
         * Gets a result.
         *
         * @return a result
         * @throws BaseException
         *             exception
         */
        @Override
        R get() throws BaseException;
    }

    /**
     * Consumer {@link BaseException}-t dobó operációkhoz
     *
     * @param <T>
     *            input objektum tipusa
     * @see java.util.function.Consumer
     */
    @FunctionalInterface
    public interface BaseExceptionConsumer<T> extends hu.icellmobilsoft.coffee.se.function.BaseExceptionConsumer<T> {
        /**
         * Performs this operation on the given argument.
         *
         * @param t
         *            the input argument
         * @throws BaseException
         *             exception
         */
        @Override
        void accept(T t) throws BaseException;
    }

    /**
     * Represents a function without parameter and return value
     */
    @FunctionalInterface
    public interface BaseExceptionRunner extends hu.icellmobilsoft.coffee.se.function.BaseExceptionRunner {
        /**
         * Executes the function
         *
         * @throws BaseException
         *             exception
         */
        @Override
        void run() throws BaseException;
    }

    /**
     * Represents a function that accepts one argument and produces a result.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #apply(Object)}.
     *
     * @param <T>
     *            the type of the input to the function
     * @param <R>
     *            the type of the result of the function
     */
    @FunctionalInterface
    public interface BaseExceptionFunction<T, R> extends hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction<T, R> {
        /**
         * Applies this function to the given argument.
         *
         * @param t
         *            the function argument
         * @return the function result
         * @throws BaseException
         *             exception
         */
        @Override
        R apply(T t) throws BaseException;
    }

    /**
     * Represents a function that accepts two arguments and produces a result. This is a specialization of {@link BaseExceptionFunction}.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #apply(Object, Object)}.
     *
     * @param <T1>
     *            the type of the first argument to the function
     * @param <T2>
     *            the type of the second argument to the function
     * @param <R>
     *            the type of the result of the function
     */
    @FunctionalInterface
    public interface BaseExceptionFunction2<T1, T2, R> extends hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction2<T1, T2, R> {
        /**
         * Applies this function to the given arguments.
         *
         * @param t1
         *            the first function argument
         * @param t2
         *            the second function argument
         * @return the function result
         * @throws BaseException
         *             exception
         */
        @Override
        R apply(T1 t1, T2 t2) throws BaseException;
    }

    /**
     * Represents a function that accepts three arguments and produces a result. This is a specialization of {@link BaseExceptionFunction}.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #apply(Object, Object, Object)}.
     *
     * @param <T1>
     *            the type of the first argument to the function
     * @param <T2>
     *            the type of the second argument to the function
     * @param <T3>
     *            the type of the third argument to the function
     * @param <R>
     *            the type of the result of the function
     */
    @FunctionalInterface
    public interface BaseExceptionFunction3<T1, T2, T3, R> extends hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction3<T1, T2, T3, R> {
        /**
         * Applies this function to the given arguments.
         *
         * @param t1
         *            the first function argument
         * @param t2
         *            the second function argument
         * @param t3
         *            the third function argument
         * @return the function result
         * @throws BaseException
         *             exception
         */
        @Override
        R apply(T1 t1, T2 t2, T3 t3) throws BaseException;
    }

    /**
     * Represents a function that accepts four arguments and produces a result. This is a specialization of {@link BaseExceptionFunction}.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a> whose functional method is {@link #apply(Object, Object, Object, Object)}.
     *
     * @param <T1>
     *            the type of the first argument to the function
     * @param <T2>
     *            the type of the second argument to the function
     * @param <T3>
     *            the type of the third argument to the function
     * @param <T4>
     *            the type of the fourth argument to the function
     * @param <R>
     *            the type of the result of the function
     */
    @FunctionalInterface
    public interface BaseExceptionFunction4<T1, T2, T3, T4, R>
            extends hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction4<T1, T2, T3, T4, R> {
        /**
         * Applies this function to the given arguments.
         *
         * @param t1
         *            the first function argument
         * @param t2
         *            the second function argument
         * @param t3
         *            the third function argument
         * @param t4
         *            the fourth function argument
         * @return the function result
         * @throws BaseException
         *             exception
         */
        @Override
        R apply(T1 t1, T2 t2, T3 t3, T4 t4) throws BaseException;
    }

    /**
     * Represents a function that accepts fifth arguments and produces a result. This is a specialization of {@link BaseExceptionFunction}.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a> whose functional method is
     * {@link #apply(Object, Object, Object, Object, Object)}.
     *
     * @param <T1>
     *            the type of the first argument to the function
     * @param <T2>
     *            the type of the second argument to the function
     * @param <T3>
     *            the type of the third argument to the function
     * @param <T4>
     *            the type of the fourth argument to the function
     * @param <T5>
     *            the type of the fifth argument to the function
     * @param <R>
     *            the type of the result of the function
     */
    @FunctionalInterface
    public interface BaseExceptionFunction5<T1, T2, T3, T4, T5, R>
            extends hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction5<T1, T2, T3, T4, T5, R> {
        /**
         * Applies this function to the given arguments.
         *
         * @param t1
         *            the first function argument
         * @param t2
         *            the second function argument
         * @param t3
         *            the third function argument
         * @param t4
         *            the fourth function argument
         * @param t5
         *            the fifth function argument
         * @return the function result
         * @throws BaseException
         *             exception
         */
        @Override
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) throws BaseException;
    }

    /**
     * Represents a function that accepts six arguments and produces a result. This is a specialization of {@link BaseExceptionFunction}.
     *
     * <p>
     * This is a <a href="package-summary.html">functional interface</a> whose functional method is
     * {@link #apply(Object, Object, Object, Object, Object, Object)}.
     *
     * @param <T1>
     *            the type of the first argument to the function
     * @param <T2>
     *            the type of the second argument to the function
     * @param <T3>
     *            the type of the third argument to the function
     * @param <T4>
     *            the type of the fourth argument to the function
     * @param <T5>
     *            the type of the fifth argument to the function
     * @param <T6>
     *            the type of the sixth argument to the function
     * @param <R>
     *            the type of the result of the function
     */
    @FunctionalInterface
    public interface BaseExceptionFunction6<T1, T2, T3, T4, T5, T6, R>
            extends hu.icellmobilsoft.coffee.se.function.BaseExceptionFunction6<T1, T2, T3, T4, T5, T6, R> {
        /**
         * Applies this function to the given arguments.
         *
         * @param t1
         *            the first function argument
         * @param t2
         *            the second function argument
         * @param t3
         *            the third function argument
         * @param t4
         *            the fourth function argument
         * @param t5
         *            the fifth function argument
         * @param t6
         *            the sixth function argument
         * @return the function result
         * @throws BaseException
         *             exception
         */
        @Override
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) throws BaseException;
    }
}
