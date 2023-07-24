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

/**
 * Marker type to handle dynamic opentracing implementation
 * 
 * @author czenczl
 * @since 2.1.0
 */
public interface IOpenTraceHandler {

    /**
     * Wrapping function in trace context
     * 
     * @param <T>
     *            generic type for Supplier
     * @param function
     *            the function to wrap in trace context
     * @param traced
     *            holds information about tracing tags
     * @param operation
     *            name of Span
     * @return the function result
     */
    <T> T runWithTrace(Supplier<T> function, Traced traced, String operation);
}
