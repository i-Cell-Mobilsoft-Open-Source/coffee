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

import java.lang.reflect.Method;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

/**
 * Default interceptor for {@link Traced} binding
 * 
 * @author czenczl
 * @since 2.5.0
 */
@Traced
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION)
public class TelemetryInterceptor {

    private static final Logger LOGGER = Logger.getLogger(TelemetryInterceptor.class);

    @Inject
    private Tracer tracer;

    /**
     * Default constructor, constructs a new object.
     */
    public TelemetryInterceptor() {
        super();
    }

    /**
     * Intercept and handle span creation with called method name
     * 
     * @param ctx
     *            {@link InvocationContext} context
     * @return InvocationContext {@link InvocationContext#proceed()}
     * @throws Exception
     *             if error
     */
    @AroundInvoke
    public Object wrap(InvocationContext ctx) throws Exception {
        if (ctx == null) {
            LOGGER.debug("ctx is null, skip TelemetryInterceptor");
        }

        Traced traced = getTracedAnnotation(ctx);

        // in case of jedis operation without root context, now we skip trace (like when consuming message with xreadgroup)
        if (checkJedisComponent(traced.component())) {
            if (!isActiveSpan()) {
                LOGGER.debug("Skipping trace, no active span to join the jedis call: [{0}]", traced.component());
                return ctx.proceed();
            }
        }

        Optional<SpanBuilder> spanBuilderO = createSpanBuilder(ctx, traced);

        // cannot construct span builder, skip tracing
        if (spanBuilderO.isEmpty()) {
            return ctx.proceed();
        }

        SpanBuilder spanBuilder = spanBuilderO.get();
        return trace(ctx, spanBuilder, traced);

    }

    private Optional<SpanBuilder> createSpanBuilder(InvocationContext ctx, Traced traced) {
        if (ctx.getParameters() == null) {
            return Optional.empty();
        }

        String operationName = getOperationName(ctx);
        if (checkJedisComponent(traced.component())) {
            // jedis operation name by functionName parameter
            if (ctx.getParameters().length > 1) {
                operationName = String.valueOf(ctx.getParameters()[1]);
            }
        }

        return Optional.of(TelemetryUtil.createSpanBuilder(tracer, traced, operationName));

    }

    private Object trace(InvocationContext ctx, SpanBuilder spanBuilder, Traced traced) throws Exception {
        Span span = spanBuilder.startSpan();
        TelemetryUtil.fillSpan(span, traced);
        Scope scope = null;
        try {
            // consumer root context creation
            if (checkStreamConsumer(traced.component())) {
                scope = attachSpan(span);
            }
            return ctx.proceed();
        } catch (Exception e) {
            TelemetryUtil.recordException(span, e);
            throw e;
        } finally {
            span.end();
            // close scope in case of root context (like consumer loop lifecycle)
            if (scope != null) {
                scope.close();
            }
        }
    }

    private Scope attachSpan(Span span) {
        Context context = Context.current();
        context = context.with(span);
        return context.makeCurrent();
    }

    private boolean isActiveSpan() {
        Context context = Context.current();
        Span span = Span.fromContext(context);
        return span != null ? span.getSpanContext().isValid() : false;
    }

    private String getOperationName(InvocationContext ctx) {
        Method method = ctx.getMethod();
        return ctx.getTarget().getClass().getSuperclass().getCanonicalName() + "." + method.getName();
    }

    private Traced getTracedAnnotation(InvocationContext ctx) {
        Method method = ctx.getMethod();
        return method.getAnnotation(Traced.class);
    }

    private boolean checkJedisComponent(String component) {
        return StringUtils.equals(component, SpanAttribute.Redis.Jedis.COMPONENT);
    }

    private boolean checkStreamConsumer(String component) {
        return StringUtils.equals(component, SpanAttribute.Redis.Stream.COMPONENT);
    }

}
