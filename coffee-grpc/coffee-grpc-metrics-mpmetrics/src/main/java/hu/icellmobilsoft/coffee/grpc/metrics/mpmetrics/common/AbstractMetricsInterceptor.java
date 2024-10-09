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
package hu.icellmobilsoft.coffee.grpc.metrics.mpmetrics.common;

import java.time.LocalDateTime;

import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Tag;

import hu.icellmobilsoft.coffee.grpc.metrics.api.constants.IGrpcMetricConstant;
import hu.icellmobilsoft.coffee.grpc.metrics.mpmetrics.bundle.MetricsBundle;
import io.grpc.MethodDescriptor;

/**
 * Abstract class for metric interceptors to help collect metrics. Handles request/response/duration metrics.
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
public abstract class AbstractMetricsInterceptor {

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractMetricsInterceptor() {
        super();
    }

    /**
     * Create metric bundle for the interceptors, metric based on mp-metrics
     * 
     * @param methodDescriptor
     *            to fill metric data
     * @return created MetricBundle with microprofile metric data
     */
    protected MetricsBundle createMetricBundle(MethodDescriptor<?, ?> methodDescriptor) {
        MetricsBundle metricBundle = new MetricsBundle();
        metricBundle.setStartTime(LocalDateTime.now());

        // microprofile metric
        MetricRegistry metricRegistry = CDI.current().select(MetricRegistry.class).get();

        // It might be necessary to use caching here for improved performance
        // request counter
        Tag method = new Tag(IGrpcMetricConstant.Tag.TAG_METHOD, methodDescriptor.getBareMethodName());
        Tag methodType = new Tag(IGrpcMetricConstant.Tag.TAG_METHOD_TYPE, methodDescriptor.getType().name());
        Tag serviceName = new Tag(IGrpcMetricConstant.Tag.TAG_SERVICE, methodDescriptor.getServiceName());
        Metadata requestMeta = Metadata.builder().withName(getRequestMetadataName()).withDescription(getRequestMetadataName())
                .withType(MetricType.COUNTER).build();
        metricBundle.setRequestCounter(metricRegistry.counter(requestMeta, method, methodType, serviceName));

        // response counter
        Metadata responseMeta = Metadata.builder().withName(getResponseMetadataName()).withDescription(getResponseMetadataName())
                .withType(MetricType.COUNTER).build();
        metricBundle.setResponseCounter(metricRegistry.counter(responseMeta, method, methodType, serviceName));

        // timer
        Metadata timerMeta = Metadata.builder().withName(getTimerMetadataName()).withDescription(getTimerMetadataName()).withType(MetricType.TIMER)
                .build();
        metricBundle.setTimerCodeFunction(
                (code) -> metricRegistry.timer(timerMeta, method, methodType, serviceName, new Tag(IGrpcMetricConstant.Tag.TAG_STATUS, code.name())));

        return metricBundle;

    }

    /**
     * Gets the request metadata name from interceptor logic
     * 
     * @return request metadata name
     */
    protected abstract String getRequestMetadataName();

    /**
     * Gets the response metadata name from interceptor logic
     * 
     * @return response metadata name
     */
    protected abstract String getResponseMetadataName();

    /**
     * Gets the timer metadata name from interceptor logic
     * 
     * @return timer metadata name
     */
    protected abstract String getTimerMetadataName();

}
