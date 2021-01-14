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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;
import io.opentracing.tag.Tags;

/**
 * Interceptor for {@link Traced} binding
 * <p>
 * based on io.opentracing.contrib.interceptors.OpenTracingInterceptor
 * 
 * @author czenczl
 * @since 1.3.0
 */
@Traced
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION)
public class OpenTraceInterceptor {

    public static final String SPAN_CONTEXT = "__opentracing_span_context";
    private static final Logger LOGGER = Logger.getLogger(OpenTraceInterceptor.class.getName());

    @Inject
    Instance<Tracer> tracerInstance;

    private volatile Tracer tracer = null;

    @AroundInvoke
    public Object wrap(InvocationContext ctx) throws Exception {
        Tracer tracer = getTracer();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(getOperationName(ctx.getMethod()));

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

        Scope scope = spanBuilder.startActive(true);
        try {
            LOGGER.debug("Adding span context into the invocation context.");
            ctx.getContextData().put(SPAN_CONTEXT, scope.span().context());

            if (contextParameterIndex >= 0) {
                LOGGER.debug("Overriding the original span context with our new context.");
                for (int i = 0; i < ctx.getParameters().length; i++) {
                    if (ctx.getParameters()[contextParameterIndex] instanceof Span) {
                        ctx.getParameters()[contextParameterIndex] = scope.span();
                    }

                    if (ctx.getParameters()[contextParameterIndex] instanceof SpanContext) {
                        ctx.getParameters()[contextParameterIndex] = scope.span().context();
                    }
                }
            }

            return ctx.proceed();
        } catch (Exception e) {
            logException(scope.span(), e);
            throw e;
        } finally {
            scope.close();
        }
    }

    // uses volatile read and synchronized block to avoid possible duplicate creation of Tracer in multi-threaded env
    public Tracer getTracer() {
        Tracer val = tracer;
        if (val != null) {
            return val;
        }
        synchronized (this) {
            if (tracer == null) {
                if (null != tracerInstance && !tracerInstance.isUnsatisfied()) {
                    tracer = this.tracerInstance.get();
                } else {
                    tracer = TracerResolver.resolveTracer();
                }
            }
            return tracer;
        }
    }

    private String getOperationName(Method method) {
        Traced classTraced = method.getDeclaringClass().getAnnotation(Traced.class);
        Traced methodTraced = method.getAnnotation(Traced.class);
        if (methodTraced != null && methodTraced.operationName().length() > 0) {
            return methodTraced.operationName();
        } else if (classTraced != null && classTraced.operationName().length() > 0) {
            return classTraced.operationName();
        }
        return String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
    }

    private void logException(Span span, Exception e) {
        Map<String, Object> errorLogs = new HashMap<String, Object>(2);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", e);
        span.log(errorLogs);
        Tags.ERROR.set(span, true);
    }

}
