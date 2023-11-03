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

import java.lang.reflect.Method;
import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.Context;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;

/**
 * Default interceptor for {@link Traced} binding
 * 
 * @author czenczl
 * @since 1.3.0
 */
@Traced
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION)
public class OpenTraceInterceptor {

    private static final Logger LOGGER = Logger.getLogger(OpenTraceInterceptor.class);

    @Inject
    private OpenTraceResolver openTraceResolver;

    /**
     * Context propagation key
     */
    public static Context.Key<Span> OPEN_TRACE_GRPC_CONTEXT_KEY = Context.key("openTraceContextKey");

    /**
     * Default constructor, constructs a new object.
     */
    public OpenTraceInterceptor() {
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
            LOGGER.debug("ctx is null, skip OpenTraceInterceptor");
        }
        Tracer tracer = openTraceResolver.resolveTracer();

        Optional<SpanBuilder> spanBuilderO = createSpanBuilder(ctx, tracer);

        // no span builder skip tracing
        if (spanBuilderO.isEmpty()) {
            return ctx.proceed();
        }

        return handleSpanCreation(ctx, tracer, spanBuilderO.get());
    }

    private Object handleSpanCreation(InvocationContext ctx, Tracer tracer, SpanBuilder spanBuilder) throws Exception {
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        try {
            return ctx.proceed();
        } catch (Exception e) {
            OpenTraceUtil.tagException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

    private Optional<SpanBuilder> createSpanBuilder(InvocationContext ctx, Tracer tracer) {
        if (ctx.getParameters() == null) {
            return Optional.empty();
        }

        Method method = ctx.getMethod();
        Traced methodTraced = method.getAnnotation(Traced.class);
        String operationName = ctx.getTarget().getClass().getSuperclass().getCanonicalName();

        // gRPC propagated span
        Span span = OPEN_TRACE_GRPC_CONTEXT_KEY.get(Context.current());
        if (span != null) {
            tracer.activateSpan(span);
        }

        // in case of jedis component, extract operation name from method, and check if there is an active span
        if (checkJedisComponent(methodTraced.component())) {
            // jedis operation needs active span to join
            if (!isActiveSpan(tracer)) {
                LOGGER.debug("Skipping trace, no active span to join the jedis call.");
                return Optional.empty();
            }
            // jedis operation name by functionName parameter
            if (ctx.getParameters() != null && ctx.getParameters().length > 1) {
                operationName = String.valueOf(ctx.getParameters()[1]);
            }
        }
        return Optional.of(OpenTraceUtil.createSpanBuilder(tracer, methodTraced, operationName));

    }

    private boolean isActiveSpan(Tracer tracer) {
        return tracer.activeSpan() != null;
    }

    private boolean checkJedisComponent(String component) {
        return StringUtils.equals(component, hu.icellmobilsoft.coffee.cdi.trace.constants.Tags.Redis.Jedis.COMPONENT);
    }

}
