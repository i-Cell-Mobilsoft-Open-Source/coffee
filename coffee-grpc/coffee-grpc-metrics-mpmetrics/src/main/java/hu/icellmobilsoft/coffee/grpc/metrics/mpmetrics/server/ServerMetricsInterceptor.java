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
package hu.icellmobilsoft.coffee.grpc.metrics.mpmetrics.server;

import jakarta.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.grpc.metrics.api.IMetricsInterceptor;
import hu.icellmobilsoft.coffee.grpc.metrics.api.ServerMetricsInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.metrics.api.constants.IGrpcMetricConstant;
import hu.icellmobilsoft.coffee.grpc.metrics.mpmetrics.bundle.MetricsBundle;
import hu.icellmobilsoft.coffee.grpc.metrics.mpmetrics.common.AbstractMetricsInterceptor;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * gRPC server interceptor that will collect metrics using microprofile-metrics api
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@ServerMetricsInterceptorQualifier
@Dependent
public class ServerMetricsInterceptor extends AbstractMetricsInterceptor implements ServerInterceptor, IMetricsInterceptor {

    /**
     * Default constructor, constructs a new object.
     */
    public ServerMetricsInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata requestMetadata,
            ServerCallHandler<ReqT, RespT> next) {

        MetricsBundle metricBundle = createMetricBundle(call.getMethodDescriptor());

        MetricsServerCall<ReqT, RespT> monitoringCall = new MetricsServerCall<ReqT, RespT>(call, metricBundle);

        return new MetricsServerCallListener<ReqT>(next.startCall(monitoringCall, requestMetadata), metricBundle, monitoringCall::getResponseCode);
    }

    @Override
    protected String getRequestMetadataName() {
        return IGrpcMetricConstant.Server.METADATA_NAME_REQUEST_RECEIVED;
    }

    @Override
    protected String getResponseMetadataName() {
        return IGrpcMetricConstant.Server.METADATA_NAME_RESPONSE_SENT;
    }

    @Override
    protected String getTimerMetadataName() {
        return IGrpcMetricConstant.Server.METADATA_NAME_TIMER;
    }

}
