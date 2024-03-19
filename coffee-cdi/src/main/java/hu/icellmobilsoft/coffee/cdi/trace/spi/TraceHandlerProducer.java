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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionSupplier;

/**
 * Produces the underlying tracing implementation if exists, otherwise creates a default implementation without trace handling
 *
 * @author czenczl
 * @since 2.5.0
 */
@ApplicationScoped
public class TraceHandlerProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public TraceHandlerProducer() {
        super();
    }

    /**
     * Producer method to supply the provided tracing implementation
     *
     * @return IOpenTraceHandler implementation
     */
    @Produces
    @ApplicationScoped
    public ITraceHandler produceTraceHandler() {
        Instance<ITraceHandler> openTraceHandler = CDI.current().select(ITraceHandler.class, new TraceHandlerQualifier.Literal());
        if (openTraceHandler.isResolvable()) {
            return openTraceHandler.get();
        }

        // default implementation
        ITraceHandler noTraceHandler = new ITraceHandler() {
            @Override
            public <T> T runWithTraceNoException(Supplier<T> function, Traced traced, String operation) {
                return function.get();
            }

            @Override
            public void runWithTraceNoException(Runnable function, Traced traced, String operation) {
                function.run();
            }

            @Override
            public <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation) throws BaseException {
                return function.get();
            }

            @Override
            public void runWithTrace(BaseExceptionRunner function, Traced traced, String operation) throws BaseException {
                function.run();
            }
        };

        return noTraceHandler;

    }

}
