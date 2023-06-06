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

import javax.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.grpc.metrics.api.IMetricsInterceptor;
import hu.icellmobilsoft.coffee.grpc.metrics.api.ServerMetricsInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.metrics.impl.bundle.MetricsBundle;
import hu.icellmobilsoft.coffee.grpc.metrics.impl.common.AbstractMetricsInterceptor;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * gRPC server interceptor that will collect metrics using microprofile-metrics api
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
@ServerMetricsInterceptorQualifier
@Dependent
public class ServerMetricsInterceptor extends AbstractMetricsInterceptor implements ServerInterceptor, IMetricsInterceptor {

    /**
     * Metric key of Coffee gRPC server communication: Count of requests received
     */
    public static final String METADATA_NAME_REQUEST_RECEIVED = "coffee_grpc_server_requests_received_messages";
    /**
     * Metric key of Coffee gRPC server communication: Count of sent responses
     */
    public static final String METADATA_NAME_RESPONSE_SENT = "coffee_grpc_server_responses_sent_messages";
    /**
     * Metric key of Coffee gRPC server communication: Request-Response durations in seconds
     */
    public static final String METADATA_NAME_TIMER = "coffee_grpc_server_processing_duration_seconds";

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata requestMetadata,
            ServerCallHandler<ReqT, RespT> next) {

        MetricsBundle metricBundle = createMetricBundle(call.getMethodDescriptor());

        MetricsServerCall<ReqT, RespT> monitoringCall = new MetricsServerCall<ReqT, RespT>(call, metricBundle);

        return new MetricsServerCallListener<ReqT>(next.startCall(monitoringCall, requestMetadata), metricBundle, monitoringCall::getResponseCode);
    }

    @Override
    protected String getRequestMetadataName() {
        return METADATA_NAME_REQUEST_RECEIVED;
    }

    @Override
    protected String getResponseMetadataName() {
        return METADATA_NAME_RESPONSE_SENT;
    }

    @Override
    protected String getTimerMetadataName() {
        return METADATA_NAME_TIMER;
    }

}
