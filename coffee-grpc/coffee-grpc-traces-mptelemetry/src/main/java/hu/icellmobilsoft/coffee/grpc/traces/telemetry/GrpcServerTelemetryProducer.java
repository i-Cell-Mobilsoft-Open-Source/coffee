/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.traces.telemetry;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.grpc.traces.api.ClientTracesInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.traces.api.ServerTracesInterceptorQualifier;
import io.grpc.ClientInterceptor;
import io.grpc.ServerInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;

/**
 * Produces gRPC telemetry server interceptor
 * 
 * @author czenczl
 * @since 2.5.0
 *
 */
@ApplicationScoped
public class GrpcServerTelemetryProducer {

    @Inject
    private OpenTelemetry openTelemetry;

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcServerTelemetryProducer() {
        super();
    }

    /**
     * Returns a new {@link ServerInterceptor} to instrument gRPC calls
     * 
     * @return the server interceptor
     */
    @Produces
    @Dependent
    @ServerTracesInterceptorQualifier
    public ServerInterceptor produceServerInterceptor() {
        GrpcTelemetry grpcTelemetry = GrpcTelemetry.create(openTelemetry);
        return grpcTelemetry.newServerInterceptor();
    }

    /**
     * Returns a new {@link ClientInterceptor} to instrument gRPC calls
     * 
     * @return the client interceptor
     */
    @Produces
    @Dependent
    @ClientTracesInterceptorQualifier
    public ClientInterceptor produceClientInterceptor() {
        GrpcTelemetry grpcTelemetry = GrpcTelemetry.create(openTelemetry);
        return grpcTelemetry.newClientInterceptor();
    }

}
