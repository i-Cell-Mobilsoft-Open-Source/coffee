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

import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall;
import io.grpc.Metadata;
import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * 
 * Forwarding server call to handle tracing
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 1.14.0
 * 
 * @param <ReqT>
 *            The type of message received.
 * @param <RespT>
 *            The type of message sent.
 */
public class OpenTraceClientCall<ReqT, RespT> extends SimpleForwardingClientCall<ReqT, RespT> {

    private Span span;
    private Scope scope;

    /**
     * Creates client call to handle tracing
     * 
     * @param delegate
     *            origin
     * @param span
     *            opentracing span to finish
     * @param scope
     *            opentracing scope to close
     */
    public OpenTraceClientCall(ClientCall<ReqT, RespT> delegate, Span span, Scope scope) {
        super(delegate);
        this.span = span;
        this.scope = scope;
    }

    @Override
    public void start(final Listener<RespT> responseListener, final Metadata headers) {
        super.start(new OpenTraceClientCallListener<>(responseListener, span, scope), headers);
    }

}
