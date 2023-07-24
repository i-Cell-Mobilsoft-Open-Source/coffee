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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tags;

/**
 * Helps creating Spans, and add exception information to spans
 * 
 * @author czenczl
 * @since 2.1.0
 */
public class OpenTraceUtil {

    private OpenTraceUtil() {
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
        SpanBuilder spanBuilder = tracer.buildSpan(operationName);
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), traced.kind());
        spanBuilder.withTag(Tags.COMPONENT.getKey(), traced.component());
        spanBuilder.withTag(Tags.DB_TYPE.getKey(), traced.dbType());
        return spanBuilder;
    }

    /**
     * Tags the span with exception
     * 
     * @param span
     *            the span will tagged with error
     * @param exception
     *            the error object excepion value
     */
    public static void tagException(Span span, Exception exception) {
        if (span == null || exception == null) {
            throw new IllegalArgumentException("One of the mandatory parameters missing: tracer, exception.");
        }
        Map<String, Object> errorLogs = new HashMap<>(2);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", exception);
        span.log(errorLogs);
        Tags.ERROR.set(span, true);
    }
}
