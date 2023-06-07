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
package hu.icellmobilsoft.coffee.grpc.metrics.impl.client;

import hu.icellmobilsoft.coffee.grpc.metrics.impl.bundle.MetricsBundle;
import io.grpc.ClientCall;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;

/**
 * gRPC client call that will collect metrics using microprofile-metrics api
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
public class MetricsClientCall<ReqT, RespT> extends ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT> {

    private MetricsBundle metricBundle;

    /**
     * Creates a new client call to collect metrics
     * 
     * @param delegate
     *            original call
     * @param metricBundle
     *            counter and timer function container
     */
    public MetricsClientCall(ClientCall<ReqT, RespT> delegate, MetricsBundle metricBundle) {
        super(delegate);
        this.metricBundle = metricBundle;
    }

    @Override
    public void start(Listener<RespT> responseListener, Metadata headers) {
        super.start(new MetricsClientCallListener<>(responseListener, metricBundle), headers);
    }

    @Override
    public void sendMessage(ReqT message) {
        metricBundle.getRequestCounter().inc();
        super.sendMessage(message);
    }

}
