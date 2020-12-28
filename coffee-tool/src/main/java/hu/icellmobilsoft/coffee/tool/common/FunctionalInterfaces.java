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
 * @author imre.scheffer
 * @since 1.0.0
 */
public class FunctionalInterfaces {

    /**
     * This and the following ones are for a smart wrapping of all action calls inside the REST implementations
     * 
     * @param <R>
     *            return object type
     */
    @FunctionalInterface
    public interface BaseExceptionSupplier<R> {
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
    public interface BaseExceptionConsumer<T> {
        /**
         * Performs this operation on the given argument.
         *
         * @param t
         *            the input argument
         * @throws BaseException
         *             exception
         */
        void accept(T t) throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionRunner {
        void run() throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionFunction<T, R> {
        R apply(T t) throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionFunction2<T1, T2, R> {
        R apply(T1 t1, T2 t2) throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionFunction3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3) throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionFunction4<T1, T2, T3, T4, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4) throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionFunction5<T1, T2, T3, T4, T5, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) throws BaseException;
    }

    @FunctionalInterface
    public interface BaseExceptionFunction6<T1, T2, T3, T4, T5, T6, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) throws BaseException;
    }
}
