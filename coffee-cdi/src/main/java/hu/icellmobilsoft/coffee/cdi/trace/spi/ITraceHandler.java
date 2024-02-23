/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.cdi.trace.spi;

import java.util.function.Supplier;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionSupplier;

/**
 * Marker type to handle dynamic tracing implementation
 *
 * @author czenczl
 * @since 2.5.0
 */
public interface ITraceHandler {

    /**
     * Wrapping function in trace context.
     *
     * @param <T>
     *            generic type for {@link Supplier}
     * @param function
     *            the function to wrap in trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            name of Span
     * @return the function result
     */
    <T> T runWithTraceNoException(Supplier<T> function, Traced traced, String operation);

    /**
     * Wrapping function in trace context.
     *
     * @param <T>
     *            generic type for {@link BaseExceptionSupplier}
     * @param function
     *            the function to wrap in trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            name of Span
     * @return the function result
     * @throws BaseException
     *             on error
     */
    <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation) throws BaseException;

    /**
     * Wrapping function in trace context.
     *
     * @param function
     *            the function to wrap in trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            name of Span
     * @throws BaseException
     *             on error
     */
    void runWithTrace(BaseExceptionRunner function, Traced traced, String operation) throws BaseException;

}
