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

/**
 * Produces the underlying tracing implementation if exists, otherwise creates a default implementation without trace handling
 * 
 * @author czenczl
 * @since 2.1.0
 */
@ApplicationScoped
public class OpenTraceHandlerProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public OpenTraceHandlerProducer() {
        super();
    }

    /**
     * Producer method to supply the provided tracing implementation
     * 
     * @return IOpenTraceHandler implementation
     */
    @Produces
    @ApplicationScoped
    public IOpenTraceHandler produceOpenTraceHandler() {
        Instance<IOpenTraceHandler> openTraceHandler = CDI.current().select(IOpenTraceHandler.class, new OpenTraceHandlerQualifier.Literal());
        if (openTraceHandler.isResolvable()) {
            return (IOpenTraceHandler) openTraceHandler.get();
        }

        // default implementation
        IOpenTraceHandler noTraceHandler = new IOpenTraceHandler() {
            @Override
            public <T> T runWithTrace(Supplier<T> function, Traced traced, String operation) {
                return function.get();
            }
        };

        return noTraceHandler;

    }

}
