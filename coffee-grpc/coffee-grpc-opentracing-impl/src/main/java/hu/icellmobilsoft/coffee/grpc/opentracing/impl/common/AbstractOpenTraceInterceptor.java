/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.opentracing.impl.common;

import java.util.StringJoiner;

import io.grpc.MethodDescriptor;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tags;

/**
 * 
 * Abstract opentracing interceptor logic
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.1.0
 */

public abstract class AbstractOpenTraceInterceptor {

    /**
     * Creates new span builder for opentracing
     * 
     * @param methodDescriptor
     *            gRPC method info
     * @param tracer
     *            microprofile opentracing tracer
     * @return trace span builder
     */
    protected SpanBuilder createSpanBuilder(MethodDescriptor<?, ?> methodDescriptor, Tracer tracer) {

        StringJoiner joiner = new StringJoiner(":");
        joiner.add(methodDescriptor.getType().name());
        joiner.add(methodDescriptor.getServiceName());
        joiner.add(methodDescriptor.getBareMethodName());

        SpanBuilder spanBuilder = tracer.buildSpan(joiner.toString());
        spanBuilder.withTag(Tags.SPAN_KIND.getKey(), getSpanKind());
        spanBuilder.withTag(Tags.COMPONENT.getKey(), hu.icellmobilsoft.coffee.cdi.trace.constants.Tags.Grpc.COMPONENT);

        return spanBuilder;
    }

    /**
     * Gets the span kind form interceptor logic
     * 
     * @return trace {@link Tags#SPAN_KIND} name
     */
    protected abstract String getSpanKind();

}
