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

import io.grpc.ClientCall.Listener;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Status;
import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * Forwarding server call listener to handle trace interceptor forwarding
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.1.0
 * @param <ReqT>
 *            The type of message received.
 */
public class OpenTraceClientCallListener<ReqT> extends SimpleForwardingClientCallListener<ReqT> {

    private Span span;
    private Scope scope;

    /**
     * Creates client listener to handle tracing
     * 
     * @param delegate
     *            origin
     * @param span
     *            opentracing span to finish
     * @param scope
     *            opentracing scope to close
     */
    public OpenTraceClientCallListener(Listener<ReqT> delegate, Span span, Scope scope) {
        super(delegate);
        this.span = span;
        this.scope = scope;
    }

    @Override
    public void onClose(final Status status, final Metadata trailers) {
        span.finish();
        scope.close();
        super.onClose(status, trailers);
    }

}
