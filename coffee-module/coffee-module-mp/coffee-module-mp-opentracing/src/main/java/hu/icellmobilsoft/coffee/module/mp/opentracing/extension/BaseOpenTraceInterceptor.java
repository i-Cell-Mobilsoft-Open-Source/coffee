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

import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.contrib.interceptors.OpenTracingInterceptor;
import io.opentracing.tag.Tags;

/**
 * 
 * Resolve {@link Tracer} implementation <br>
 * based on {@link io.opentracing.contrib.interceptors.OpenTracingInterceptor}
 * 
 * @author czenczl
 * @since 1.7.0
 */
public abstract class BaseOpenTraceInterceptor {

    /**
     * handle snap join to parent based on {@link io.opentracing.contrib.interceptors.OpenTracingInterceptor#SPAN_CONTEXT}
     */
    public static final String SPAN_CONTEXT = OpenTracingInterceptor.SPAN_CONTEXT;

    private static final Logger LOGGER = Logger.getLogger(BaseOpenTraceInterceptor.class);

    @Inject
    private OpenTraceResolver openTraceResolver;

    /**
     * Get the underlying {@link Tracer} implementation
     * 
     * @return {@link Tracer}
     */
    protected Tracer getTracer() {
        return openTraceResolver.resolveTracer();
    }

    /**
     * Handle span creation based on interception context data
     * 
     * @param ctx
     *            {@link InvocationContext}
     * @param spanBuilder
     *            {@link SpanBuilder}
     * @return {@link InvocationContext#proceed()}
     * @throws Exception
     *             if error
     */
    protected Object handleSpan(InvocationContext ctx, Tracer.SpanBuilder spanBuilder) throws Exception {
        Tracer tracer = openTraceResolver.resolveTracer();
        int contextParameterIndex = -1;
        for (int i = 0; i < ctx.getParameters().length; i++) {
            Object parameter = ctx.getParameters()[i];
            if (parameter instanceof SpanContext) {
                LOGGER.debug("Found parameter as span context. Using it as the parent of this new span");
                spanBuilder.asChildOf((SpanContext) parameter);
                contextParameterIndex = i;
                break;
            }

            if (parameter instanceof Span) {
                LOGGER.debug("Found parameter as span. Using it as the parent of this new span");
                spanBuilder.asChildOf((Span) parameter);
                contextParameterIndex = i;
                break;
            }
        }

        if (contextParameterIndex < 0) {
            LOGGER.debug("No parent found. Trying to get span context from context data");
            Object ctxParentSpan = ctx.getContextData().get(SPAN_CONTEXT);
            if (ctxParentSpan instanceof SpanContext) {
                LOGGER.debug("Found span context from context data.");
                SpanContext parentSpan = (SpanContext) ctxParentSpan;
                spanBuilder.asChildOf(parentSpan);
            }
        }

        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        try {
            LOGGER.debug("Adding span context into the invocation context.");
            ctx.getContextData().put(SPAN_CONTEXT, span.context());

            if (contextParameterIndex >= 0) {
                LOGGER.debug("Overriding the original span context with our new context.");
                for (int i = 0; i < ctx.getParameters().length; i++) {
                    if (ctx.getParameters()[contextParameterIndex] instanceof Span) {
                        ctx.getParameters()[contextParameterIndex] = span;
                    }

                    if (ctx.getParameters()[contextParameterIndex] instanceof SpanContext) {
                        ctx.getParameters()[contextParameterIndex] = span.context();
                    }
                }
            }

            return finish(ctx);
        } catch (Exception e) {
            logException(span, e);
            throw e;
        } finally {
            span.finish();
            scope.close();
        }
    }

    /**
     * call {@link InvocationContext#proceed()}
     * 
     * @param ctx
     *            {@link InvocationContext}
     * @return {@link InvocationContext#proceed()}
     * @throws Exception
     *             if error
     */
    protected Object finish(InvocationContext ctx) throws Exception {
        return ctx.proceed();
    }

    /**
     * check the thread has active span active span
     * 
     * @return true if the thread has active span active span
     */
    protected boolean isActiveSpan() {
        return getTracer().activeSpan() != null;
    }

    private void logException(Span span, Exception e) {
        Map<String, Object> errorLogs = new HashMap<String, Object>(2);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", e);
        span.log(errorLogs);
        Tags.ERROR.set(span, true);
    }

}
