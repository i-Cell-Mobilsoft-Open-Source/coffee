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

import jakarta.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.grpc.metrics.api.ClientMetricsInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.metrics.api.IMetricsInterceptor;
import hu.icellmobilsoft.coffee.grpc.metrics.impl.bundle.MetricsBundle;
import hu.icellmobilsoft.coffee.grpc.metrics.impl.common.AbstractMetricsInterceptor;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.MethodDescriptor;

/**
 * gRPC server interceptor that will collect metrics using microprofile-metrics api
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@ClientMetricsInterceptorQualifier
@Dependent
public class ClientMetricsInterceptor extends AbstractMetricsInterceptor implements ClientInterceptor, IMetricsInterceptor {

    /**
     * Metric key of Coffee gRPC client communication: Count of sent responses
     */
    public static final String METADATA_NAME_REQUEST_SENT = "coffee_grpc_client_requests_sent_messages";
    /**
     * Metric key of Coffee gRPC client communication: Count of requests received
     */
    public static final String METADATA_NAME_RESPONSE_RECEIVED = "coffee_grpc_client_responses_received_messages";
    /**
     * Metric key of Coffee gRPC client communication: Request-Response durations in seconds
     */
    public static final String METADATA_NAME_TIMER = "coffee_grpc_client_processing_duration_seconds";

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        MetricsBundle metricBundle = createMetricBundle(method);

        return new MetricsClientCall<>(next.newCall(method, callOptions), metricBundle);
    }

    @Override
    protected String getRequestMetadataName() {
        return METADATA_NAME_REQUEST_SENT;
    }

    @Override
    protected String getResponseMetadataName() {
        return METADATA_NAME_RESPONSE_RECEIVED;
    }

    @Override
    protected String getTimerMetadataName() {
        return METADATA_NAME_TIMER;
    }

}
