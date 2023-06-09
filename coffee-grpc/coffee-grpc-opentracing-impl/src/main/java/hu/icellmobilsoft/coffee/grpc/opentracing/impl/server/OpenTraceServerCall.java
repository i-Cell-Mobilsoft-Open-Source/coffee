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

import java.util.HashMap;
import java.util.Map;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.tag.Tags;

/**
 * 
 * Forwarding server call to handle tracing
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.1.0
 * 
 * @param <ReqT>
 *            The type of message received.
 * @param <RespT>
 *            The type of message sent.
 */
public class OpenTraceServerCall<ReqT, RespT> extends SimpleForwardingServerCall<ReqT, RespT> {

    private Span span;
    private Scope scope;

    /**
     * Creates new opentrace server call to handle tracing
     * 
     * @param delegate
     *            origin
     * @param span
     *            opentracing span to finish
     * @param scope
     *            opentracing scope to close
     */
    public OpenTraceServerCall(ServerCall<ReqT, RespT> delegate, Span span, Scope scope) {
        super(delegate);
        this.span = span;
        this.scope = scope;
    }

    @Override
    public void close(Status status, Metadata trailers) {
        super.close(status, trailers);

        if (!status.isOk()) {
            tagError(span, status.getCode());
        }

        span.finish();
        scope.close();

    }

    private void tagError(Span span, Code code) {
        Map<String, Object> errorLogs = new HashMap<>(2);
        errorLogs.put("event", Tags.ERROR.getKey());
        errorLogs.put("error.object", code);
        span.log(errorLogs);
        Tags.ERROR.set(span, true);
    }

}
