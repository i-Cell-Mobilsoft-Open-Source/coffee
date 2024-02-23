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
package hu.icellmobilsoft.coffee.module.mp.telemetry.extension;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.spi.ITraceHandler;
import hu.icellmobilsoft.coffee.cdi.trace.spi.TraceHandlerQualifier;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.util.function.FunctionalInterfaces.BaseExceptionSupplier;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;

/**
 * Provides telemetry functions for {@code Traced} annotation, where interceptor cannot be used or when working with dynamic proxies
 *
 * @author czenczl
 * @since 2.5.0
 */
@ApplicationScoped
@TraceHandlerQualifier
public class TelemetryHandler implements ITraceHandler {

    @Inject
    private Tracer tracer;

    /**
     * Default constructor, constructs a new object.
     */
    public TelemetryHandler() {
        super();
    }

    @Override
    public <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation) throws BaseException {
        SpanBuilder spanBuilder = TelemetryUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.startSpan();
        TelemetryUtil.fillSpan(span, traced);
        try {
            return function.get();
        } catch (RuntimeException | BaseException e) {
            TelemetryUtil.recordException(span, e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    public void runWithTrace(BaseExceptionRunner function, Traced traced, String operation) throws BaseException {
        SpanBuilder spanBuilder = TelemetryUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.startSpan();
        TelemetryUtil.fillSpan(span, traced);
        try {
            function.run();
        } catch (RuntimeException | BaseException e) {
            TelemetryUtil.recordException(span, e);
            throw e;
        } finally {
            span.end();
        }
    }

}
