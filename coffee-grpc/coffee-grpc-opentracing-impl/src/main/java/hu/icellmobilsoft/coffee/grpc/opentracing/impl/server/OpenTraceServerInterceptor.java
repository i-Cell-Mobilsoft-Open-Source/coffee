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
package hu.icellmobilsoft.coffee.grpc.opentracing.impl.server;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.coffee.grpc.opentracing.impl.common.AbstractOpenTraceInterceptor;
import hu.icellmobilsoft.coffee.grpc.traces.api.ITracesInterceptor;
import hu.icellmobilsoft.coffee.grpc.traces.api.ServerTracesInterceptorQualifier;
import hu.icellmobilsoft.coffee.module.mp.opentracing.extension.OpenTraceInterceptor;
import hu.icellmobilsoft.coffee.module.mp.opentracing.extension.OpenTraceResolver;
import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.Tracer.SpanBuilder;
import io.opentracing.tag.Tags;

/**
 * gRPC server interceptor that handle tracing data collection
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.1.0
 */
@ServerTracesInterceptorQualifier
@Dependent
public class OpenTraceServerInterceptor extends AbstractOpenTraceInterceptor implements ServerInterceptor, ITracesInterceptor {

    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata requestMetadata, ServerCallHandler<ReqT, RespT> next) {

        OpenTraceResolver openTraceResolver = CDI.current().select(OpenTraceResolver.class).get();
        Tracer tracer = openTraceResolver.resolveTracer();

        SpanBuilder spanBuilder = createSpanBuilder(call.getMethodDescriptor(), tracer);

        // not use span if one currently active
        spanBuilder.ignoreActiveSpan();

        // start trace
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        OpenTraceServerCall<ReqT, RespT> tracingServerCall = new OpenTraceServerCall<>(call, span, scope);

        Context context = Context.current().withValue(OpenTraceInterceptor.OPEN_TRACE_GRPC_CONTEXT_KEY, span);
        return Contexts.interceptCall(context, tracingServerCall, requestMetadata, next);
    }

    @Override
    protected String getSpanKind() {
        return Tags.SPAN_KIND_SERVER;
    }

}
