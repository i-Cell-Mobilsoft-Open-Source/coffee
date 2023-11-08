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
package hu.icellmobilsoft.coffee.module.mp.opentracing.extension;

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.spi.IOpenTraceHandler;
import hu.icellmobilsoft.coffee.cdi.trace.spi.OpenTraceHandlerQualifier;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;

/**
 * Provides opentracing functions for {@code Traced} annotation, where interceptor cannot be used or when working with dynamic proxies
 * 
 * @author czenczl
 * @since 2.1.0
 */
@ApplicationScoped
@OpenTraceHandlerQualifier
public class OpenTraceHandler implements IOpenTraceHandler {

    @Inject
    private OpenTraceResolver openTraceResolver;

    /**
     * Default constructor, constructs a new object.
     */
    public OpenTraceHandler() {
        super();
    }

    @Override
    public <T> T runWithTrace(Supplier<T> function, Traced traced, String operation) {
        Tracer tracer = openTraceResolver.resolveTracer();
        SpanBuilder spanBuilder = OpenTraceUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        try {
            return function.get();
        } catch (Exception e) {
            OpenTraceUtil.tagException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

}
