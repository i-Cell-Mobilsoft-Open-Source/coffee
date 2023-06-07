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
package hu.icellmobilsoft.coffee.grpc.metrics.impl.server;

import hu.icellmobilsoft.coffee.grpc.metrics.impl.bundle.MetricsBundle;
import io.grpc.ForwardingServerCall;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.Status;
import io.grpc.Status.Code;

/**
 * 
 * Forwarding server call to handle metrics. Handles response counter metrics.
 * 
 * @author czenczl
 * @since 2.1.0
 * 
 * @param <ReqT>
 *            The type of message received.
 * @param <RespT>
 *            The type of message sent.
 */
public class MetricsServerCall<ReqT, RespT> extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {

    private Code responseCode = Code.UNKNOWN;

    private MetricsBundle metricBundle;

    /**
     * Creates a new server call to collect metrics
     * 
     * @param delegate
     *            original call
     * @param metricBundle
     *            counter and timer function container
     */
    public MetricsServerCall(ServerCall<ReqT, RespT> delegate, MetricsBundle metricBundle) {
        super(delegate);
        this.metricBundle = metricBundle;
    }

    /**
     * Gets the server response code to use with mertics data
     * 
     * @return server response code
     */
    public Code getResponseCode() {
        return this.responseCode;
    }

    @Override
    protected ServerCall<ReqT, RespT> delegate() {
        return super.delegate();
    }

    @Override
    public MethodDescriptor<ReqT, RespT> getMethodDescriptor() {
        return super.getMethodDescriptor();
    }

    @Override
    public void sendMessage(RespT message) {
        super.sendMessage(message);
    }

    @Override
    public void close(Status status, io.grpc.Metadata trailers) {
        // set status code for metric
        this.responseCode = status.getCode();
        // inc response counter because communication close
        metricBundle.getResponseCounter().inc();
        super.close(status, trailers);
    }

}
