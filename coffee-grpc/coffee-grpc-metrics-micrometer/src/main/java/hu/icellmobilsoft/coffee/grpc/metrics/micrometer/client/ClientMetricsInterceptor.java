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
package hu.icellmobilsoft.coffee.grpc.metrics.micrometer.client;

import jakarta.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.grpc.metrics.api.ClientMetricsInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.metrics.api.IMetricsInterceptor;
import hu.icellmobilsoft.coffee.grpc.metrics.api.constants.IGrpcMetricConstant;
import hu.icellmobilsoft.coffee.grpc.metrics.micrometer.bundle.MetricsBundle;
import hu.icellmobilsoft.coffee.grpc.metrics.micrometer.common.AbstractMetricsInterceptor;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.MethodDescriptor;

/**
 * gRPC server interceptor that will collect metrics using micrometer api
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.5.0
 *
 */
@ClientMetricsInterceptorQualifier
@Dependent
public class ClientMetricsInterceptor extends AbstractMetricsInterceptor implements ClientInterceptor, IMetricsInterceptor {

    /**
     * Default constructor, constructs a new object.
     */
    public ClientMetricsInterceptor() {
        super();
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        MetricsBundle metricBundle = createMetricBundle(method);

        return new MetricsClientCall<>(next.newCall(method, callOptions), metricBundle);
    }

    @Override
    protected String getRequestMetadataName() {
        return IGrpcMetricConstant.Client.METADATA_NAME_REQUEST_SENT;
    }

    @Override
    protected String getResponseMetadataName() {
        return IGrpcMetricConstant.Client.METADATA_NAME_RESPONSE_RECEIVED;
    }

    @Override
    protected String getTimerMetadataName() {
        return IGrpcMetricConstant.Client.METADATA_NAME_TIMER;
    }

}
