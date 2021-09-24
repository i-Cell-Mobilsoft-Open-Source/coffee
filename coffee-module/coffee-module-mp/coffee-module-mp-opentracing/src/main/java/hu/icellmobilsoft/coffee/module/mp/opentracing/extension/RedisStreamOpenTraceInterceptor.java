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

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.RedisStreamConsumerTraced;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;

/**
 * Interceptor for {@link RedisStreamConsumerTraced} binding
 * 
 * @author czenczl
 * @since 1.3.0
 */
@RedisStreamConsumerTraced
@Interceptor
@Priority(value = Interceptor.Priority.APPLICATION)
public class RedisStreamOpenTraceInterceptor extends BaseOpenTraceInterceptor {

    /**
     * Intercept and handle span creation with redisStream consumer name
     * 
     * @param ctx
     *            {@link InvocationContext} context
     * @return InvocationContext {@link InvocationContext#proceed()}
     * @throws Exception
     *             if error
     */
    @AroundInvoke
    public Object wrap(InvocationContext ctx) throws Exception {
        Tracer tracer = getTracer();
        // class name represent onStream consumer
        String className = ctx.getTarget().getClass().getSuperclass().getCanonicalName();
        Tracer.SpanBuilder spanBuilder = tracer.buildSpan(className);
        spanBuilder.withTag(Tags.COMPONENT.getKey(), "redis-stream");
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CONSUMER);
        spanBuilder.withTag(Tags.DB_TYPE.getKey(), "redis");
        return handleSpan(ctx, spanBuilder);
    }

}
