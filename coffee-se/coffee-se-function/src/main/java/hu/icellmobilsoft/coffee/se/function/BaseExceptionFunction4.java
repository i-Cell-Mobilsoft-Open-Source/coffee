/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.se.function;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

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
 *
 * @author attila-kiss-it
 * @since 2.7.0
 */
@FunctionalInterface
public interface BaseExceptionFunction4<T1, T2, T3, T4, R> {
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
    R apply(T1 t1, T2 t2, T3 t3, T4 t4) throws BaseException;
}
