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

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.cdi.trace.spi.ITraceHandler;
import hu.icellmobilsoft.coffee.cdi.trace.spi.TraceHandlerQualifier;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionRunner;
import hu.icellmobilsoft.coffee.se.function.BaseExceptionSupplier;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;

/**
 * Provides telemetry functions for {@code Traced} annotation, where interceptor cannot be used or when working with dynamic proxies
 *
 * @author czenczl
 * @since 2.5.0
 */
@ApplicationScoped
@TraceHandlerQualifier
public class TelemetryHandler implements ITraceHandler {

    private final TextMapGetter<Map<String, String>> TEXT_MAP_GETTER = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, String> carrier, String key) {
            return carrier.get(key);
        }
    };

    @Inject
    private Tracer tracer;

    /**
     * Default constructor, constructs a new object.
     */
    public TelemetryHandler() {
        super();
    }

    @Override
    public <T> T runWithTraceNoException(Supplier<T> function, Traced traced, String operation) {
        SpanBuilder spanBuilder = TelemetryUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.startSpan();
        TelemetryUtil.fillSpan(span, traced);
        try {
            return function.get();
        } catch (RuntimeException e) {
            TelemetryUtil.recordException(span, e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    public void runWithTraceNoException(Runnable function, Traced traced, String operation) {
        SpanBuilder spanBuilder = TelemetryUtil.createSpanBuilder(tracer, traced, operation);
        Span span = spanBuilder.startSpan();
        TelemetryUtil.fillSpan(span, traced);
        try {
            function.run();
        } catch (RuntimeException e) {
            TelemetryUtil.recordException(span, e);
            throw e;
        } finally {
            span.end();
        }
    }

    @Override
    public <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation) throws BaseException {
        return runWithTrace(function, traced, operation, null);
    }

    @Override
    public <T> T runWithTrace(BaseExceptionSupplier<T> function, Traced traced, String operation, Map<String, String> parentContextCarrier)
            throws BaseException {
        String traceParentType = getTraceParentType(parentContextCarrier);
        Span span = createSpan(traced, operation, parentContextCarrier, traceParentType);

        try (Scope ignored = getScope(traced, traceParentType, span)) {
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
        runWithTrace(function, traced, operation, null);
    }

    @Override
    public void runWithTrace(BaseExceptionRunner function, Traced traced, String operation, Map<String, String> parentContextCarrier)
            throws BaseException {
        String traceParentType = getTraceParentType(parentContextCarrier);
        Span span = createSpan(traced, operation, parentContextCarrier, traceParentType);

        try (Scope ignored = getScope(traced, traceParentType, span);) {
            function.run();
        } catch (RuntimeException | BaseException e) {
            TelemetryUtil.recordException(span, e);
            throw e;
        } finally {
            span.end();
        }
    }

    private Scope getScope(Traced traced, String traceParentType, Span span) {
        Scope scope = null;
        if ("P".equals(traceParentType)) {
            scope = span.makeCurrent();
        } else if (checkStreamConsumer(traced.component())) {
            scope = attachSpan(span);
        }
        return scope;
    }

    @Override
    public void inject(Map<String, String> carrier) {
        W3CTraceContextPropagator.getInstance().inject(Context.current(), carrier, Map::put);
    }

    private Span createSpan(Traced traced, String operation, Map<String, String> parentContextCarrier, String traceParentType) {
        SpanBuilder spanBuilder = TelemetryUtil.createSpanBuilder(tracer, traced, operation);
        if ("P".equals(traceParentType)) {
            spanBuilder.setParent(extract(parentContextCarrier));
        } else if ("L".equals(traceParentType)) {
            spanBuilder.addLink(Span.fromContext(extract(parentContextCarrier)).getSpanContext());
        }

        Span span = spanBuilder.startSpan();
        TelemetryUtil.fillSpan(span, traced);
        return span;
    }

    private String getTraceParentType(Map<String, String> parentContextCarrier) {
        return Optional.ofNullable(parentContextCarrier).map(m -> m.get("traceparenttype")).orElse(null);
    }

    private boolean checkStreamConsumer(String component) {
        return StringUtils.equals(component, SpanAttribute.Redis.Stream.COMPONENT);
    }

    private Scope attachSpan(Span span) {
        Context context = Context.current();
        context = context.with(span);
        return context.makeCurrent();
    }

    private Context extract(Map<String, String> carrier) {
        return W3CTraceContextPropagator.getInstance().extract(Context.current(), carrier, TEXT_MAP_GETTER);
    }

}
