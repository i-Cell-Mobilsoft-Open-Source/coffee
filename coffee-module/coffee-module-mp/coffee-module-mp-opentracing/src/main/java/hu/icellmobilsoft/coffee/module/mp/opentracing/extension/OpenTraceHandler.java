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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.spi.ITraceHandler;
import hu.icellmobilsoft.coffee.cdi.trace.spi.TraceHandlerQualifier;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionSupplier;
import io.opentracing.References;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;

/**
 * Provides opentracing functions for {@code Traced} annotation, where interceptor cannot be used or when working with dynamic proxies
 *
 * @author czenczl
 * @since 2.1.0
 */
@ApplicationScoped
@TraceHandlerQualifier
public class OpenTraceHandler implements ITraceHandler {

    @Inject
    private OpenTraceResolver openTraceResolver;

    /**
     * Default constructor, constructs a new object.
     */
    public OpenTraceHandler() {
        super();
    }

    @Override
    public <T> T runWithTraceNoException(Supplier<T> function, Traced traced, String operation) {
        Tracer tracer = openTraceResolver.resolveTracer();
        SpanBuilder spanBuilder = OpenTraceUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        try {
            return function.get();
        } catch (RuntimeException e) {
            OpenTraceUtil.tagException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

    @Override
    public void runWithTraceNoException(Runnable function, Traced traced, String operation) {
        Tracer tracer = openTraceResolver.resolveTracer();
        SpanBuilder spanBuilder = OpenTraceUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        try {
            function.run();
        } catch (RuntimeException e) {
            OpenTraceUtil.tagException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

    @Override
    public <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation) throws BaseException {
        return runWithTrace(function, traced, operation, null);
    }

    @Override
    public <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation, Map<String, String> parentContextCarrier)
            throws BaseException {
        Tracer tracer = openTraceResolver.resolveTracer();
        Span span = createSpan(traced, operation, parentContextCarrier, tracer);
        Scope scope = tracer.activateSpan(span);
        try {
            return function.get();
        } catch (RuntimeException | BaseException e) {
            OpenTraceUtil.tagException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

    @Override
    public void runWithTrace(BaseExceptionRunner function, Traced traced, String operation) throws BaseException {
        runWithTrace(function, traced, operation, null);
    }

    @Override
    public void runWithTrace(BaseExceptionRunner function, Traced traced, String operation, Map<String, String> parentContextCarrier)
            throws BaseException {
        Tracer tracer = openTraceResolver.resolveTracer();
        Span span = createSpan(traced, operation, parentContextCarrier, tracer);
        Scope scope = tracer.activateSpan(span);
        try {
            function.run();
        } catch (RuntimeException | BaseException e) {
            OpenTraceUtil.tagException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

    @Override
    public void inject(Map<String, String> carrier) {
        Tracer tracer = openTraceResolver.resolveTracer();
        tracer.inject(tracer.activeSpan().context(), Format.Builtin.TEXT_MAP_INJECT, carrier::put);
    }

    private Span createSpan(Traced traced, String operation, Map<String, String> parentContextCarrier, Tracer tracer) {
        SpanBuilder spanBuilder = OpenTraceUtil.createSpanBuilder(tracer, traced, operation);
        if (parentContextCarrier != null) {
            SpanContext extracted = extract(parentContextCarrier);
            String traceParentType = getTraceParentType(parentContextCarrier);
            if ("P".equals(traceParentType)) {
                spanBuilder.asChildOf(extracted);
            } else if ("L".equals(traceParentType)) {
                spanBuilder.addReference(References.FOLLOWS_FROM, extracted);
            }
        }
        return spanBuilder.start();
    }

    private SpanContext extract(Map<String, String> carrier) {
        return openTraceResolver.resolveTracer().extract(Format.Builtin.TEXT_MAP_EXTRACT, new TextMapAdapter(carrier));
    }

    private String getTraceParentType(Map<String, String> parentContextCarrier) {
        return Optional.ofNullable(parentContextCarrier).map(m -> m.get("traceparenttype")).orElse(null);
    }

}
