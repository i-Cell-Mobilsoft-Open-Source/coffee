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
package hu.icellmobilsoft.coffee.grpc.opentracing.impl.client;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.coffee.grpc.opentracing.impl.common.AbstractOpenTraceInterceptor;
import hu.icellmobilsoft.coffee.grpc.traces.api.ClientTracesInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.traces.api.ITracesInterceptor;
import hu.icellmobilsoft.coffee.module.mp.opentracing.extension.OpenTraceResolver;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.MethodDescriptor;
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
@ClientTracesInterceptorQualifier
@Dependent
public class OpenTraceClientInterceptor extends AbstractOpenTraceInterceptor implements ClientInterceptor, ITracesInterceptor {

    /**
     * Default constructor, constructs a new object.
     */
    public OpenTraceClientInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        OpenTraceResolver openTraceResolver = CDI.current().select(OpenTraceResolver.class).get();
        Tracer tracer = openTraceResolver.resolveTracer();

        SpanBuilder spanBuilder = createSpanBuilder(method, tracer);

        // start trace
        Span span = spanBuilder.start();
        Scope scope = tracer.activateSpan(span);
        ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);
        return new OpenTraceClientCall<ReqT, RespT>(clientCall, span, scope);
    }

    @Override
    protected String getSpanKind() {
        return Tags.SPAN_KIND_CLIENT;
    }

}
