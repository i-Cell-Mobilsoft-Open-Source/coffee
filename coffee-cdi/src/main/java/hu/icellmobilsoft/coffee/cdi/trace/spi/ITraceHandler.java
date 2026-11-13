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

import java.util.Map;
import java.util.function.Supplier;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionSupplier;

/**
 * Marker type to handle dynamic tracing implementation
 *
 * @author czenczl
 * @since 2.5.0
 */
public interface ITraceHandler {

    /**
     * Wrapping function in trace context
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
     * Wrapping function in trace context
     *
     * @param function
     *            the function to wrap in trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            name of Span
     */
    void runWithTraceNoException(Runnable function, Traced traced, String operation);

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
     * Runs the specified function within a trace context using the supplied operation name and parent context carrier.
     *
     * @param <T>
     *            the result type returned by the function
     * @param function
     *            the function to run within the trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            the name of the span
     * @param parentContextCarrier
     *            the carrier containing the parent trace context to use for span propagation
     * @return the function result
     * @throws BaseException
     *             if the wrapped function fails
     */
    <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation, Map<String, String> parentContextCarrier)
            throws BaseException;

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

    /**
     * Runs the specified function within a trace context using the supplied operation name and parent context carrier.
     *
     * @param function
     *            the function to run within the trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            the name of the span
     * @param parentContextCarrier
     *            the carrier containing the parent trace context to use for span propagation
     * @throws BaseException
     *             if the wrapped function fails
     */
    void runWithTrace(BaseExceptionRunner function, Traced traced, String operation, Map<String, String> parentContextCarrier) throws BaseException;

    /**
     * Injects the current trace context into the specified carrier for propagation to downstream calls.
     *
     * @param carrier
     *            the carrier map to receive trace context entries
     */
    void inject(Map<String, String> carrier);

}
