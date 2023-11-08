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
package hu.icellmobilsoft.coffee.grpc.client.extension;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.grpc.client.config.GrpcClientConfig;
import hu.icellmobilsoft.coffee.grpc.client.interceptor.ClientRequestInterceptor;
import hu.icellmobilsoft.coffee.grpc.client.interceptor.ClientResponseInterceptor;
import hu.icellmobilsoft.coffee.grpc.metrics.api.ClientMetricsInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.metrics.api.IMetricsInterceptor;
import hu.icellmobilsoft.coffee.grpc.traces.api.ClientTracesInterceptorQualifier;
import hu.icellmobilsoft.coffee.grpc.traces.api.ITracesInterceptor;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Factory class for grpc producer template
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@ApplicationScoped
public class ManagedChannelProducer {

    @Inject
    private Logger log;

    @Inject
    private GrpcClientConfig grpcClientConfig;

    private Map<String, ManagedChannel> managedChannelInstances = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public ManagedChannelProducer() {
        super();
    }

    /**
     * produce ManagedChannel
     * 
     * @param injectionPoint
     *            the injection point
     * @return ManagedChannel
     */
    @Produces
    @Dependent
    @GrpcClient(configKey = "")
    public ManagedChannel produceManagedChannel(InjectionPoint injectionPoint) {
        Optional<GrpcClient> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcClient.class);
        String configKey = annotation.map(GrpcClient::configKey).orElse(null);

        return getInstance(configKey);
    }

    private synchronized ManagedChannel getInstance(String configKey) {
        return managedChannelInstances.computeIfAbsent(configKey, v -> createManagedChannel(configKey));
    }

    private ManagedChannel createManagedChannel(String configKey) {
        log.info("Creating ManagedChannel for configKey:[{0}]", configKey);
        grpcClientConfig.setConfigKey(configKey);
        String host = grpcClientConfig.getHost();
        int port = grpcClientConfig.getPort();
        try {

            // TODO usePlaintext config
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();

            configureChannelBuilder(channelBuilder);

            return channelBuilder.build();
        } catch (Exception e) {
            log.error(MessageFormat.format("Exception on initializing ManagedChannel for configKey [{0}], host: [{1}], port: [{2}]: [{3}]", configKey,
                    host, port, e.getLocalizedMessage()), e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Customize GRPC Channel Builder: interceptor activating, settings ...
     * 
     * @param channelBuilder
     *            initialized GRPC Channel Builder
     * @throws BaseException
     *             on error
     */
    protected void configureChannelBuilder(ManagedChannelBuilder<?> channelBuilder) throws BaseException {
        // request/response interceptor
        channelBuilder.intercept(new ClientRequestInterceptor());
        channelBuilder.intercept(new ClientResponseInterceptor());

        // metric
        Instance<IMetricsInterceptor> instanceMetric = CDI.current().select(IMetricsInterceptor.class,
                new ClientMetricsInterceptorQualifier.Literal());
        if (instanceMetric.isResolvable()) {
            channelBuilder.intercept((ClientInterceptor) instanceMetric.get());
        } else {
            log.warn("Could not find Metric interceptor implementation for gRPC client.");
        }

        // tracing
        Instance<ITracesInterceptor> instanceTracing = CDI.current().select(ITracesInterceptor.class, new ClientTracesInterceptorQualifier.Literal());
        if (instanceTracing.isResolvable()) {
            channelBuilder.intercept((ClientInterceptor) instanceTracing.get());
        } else {
            log.warn("Could not find Tracing interceptor implementation for gRPC client.");
        }
    }
}
