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

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

/**
 * Helps creating Spans, and add exception information to spans
 * 
 * @author czenczl
 * @since 2.5.0
 */
public class TelemetryUtil {

    private TelemetryUtil() {
    };

    /**
     * Creates a SpanBuilder by Traced annotation and operationName
     * 
     * @param tracer
     *            the tracer provider
     * @param traced
     *            the trace information to build the span
     * @param operationName
     *            Span operation name
     * @return the initialized SpanBuilder
     */
    public static SpanBuilder createSpanBuilder(Tracer tracer, Traced traced, String operationName) {
        if (tracer == null || traced == null || StringUtils.isBlank(operationName)) {
            throw new IllegalArgumentException("One of the mandatory parameters missing: tracer, traced or operationName.");
        }

        SpanBuilder spanBuilder = tracer.spanBuilder(operationName);
        spanBuilder.setSpanKind(SpanKind.valueOf(traced.kind()));
        return spanBuilder;
    }

    /**
     * Fill span attributes with span attribute
     * 
     * @param span
     *            the span will filled with attributes
     * @param traced
     *            traced method
     */
    public static void fillSpan(Span span, Traced traced) {
        if (span == null || traced == null) {
            throw new IllegalArgumentException("One of the mandatory parameters missing: span, traced.");
        }
        span.setAttribute(SpanAttribute.COMPONENT_KEY, traced.component());
        span.setAttribute(SpanAttribute.DB_TYPE_KEY, traced.dbType());
    }

    /**
     * Record the span with exception
     * 
     * @param span
     *            the span will record with error
     * @param exception
     *            the error object exception value
     */
    public static void recordException(Span span, Exception exception) {
        if (span == null || exception == null) {
            throw new IllegalArgumentException("One of the mandatory parameters missing: tracer, exception.");
        }
        span.setStatus(StatusCode.ERROR);
        // in this way the full stack trace will appear in tracing, which can be cause performance problems
        // consider using event instead
        // span.addEvent(exception.getLocalizedMessage());
        span.recordException(exception);
    }

}
