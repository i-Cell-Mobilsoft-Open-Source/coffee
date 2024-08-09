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
 * For operations that throw a consumer {@link BaseException}:
 *
 * @param <T>
 *            input objektum tipusa
 * @see java.util.function.Consumer
 *
 * @author attila-kiss-it
 * @since 2.7.0
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
