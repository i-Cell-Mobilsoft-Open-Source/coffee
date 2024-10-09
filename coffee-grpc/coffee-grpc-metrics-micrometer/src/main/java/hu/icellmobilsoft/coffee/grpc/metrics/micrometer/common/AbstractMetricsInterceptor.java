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
package hu.icellmobilsoft.coffee.grpc.metrics.micrometer.common;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.enterprise.inject.spi.CDI;

import hu.icellmobilsoft.coffee.grpc.metrics.api.constants.IGrpcMetricConstant;
import hu.icellmobilsoft.coffee.grpc.metrics.micrometer.bundle.MetricsBundle;
import io.grpc.MethodDescriptor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Builder;

/**
 * Abstract class for metric interceptors to help collect metrics. Handles request/response/duration metrics.
 * 
 * @author czenczl
 * @author Imre Scheffer
 * @since 2.5.0
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
        MeterRegistry meterRegistry = CDI.current().select(MeterRegistry.class).get();

        Tag method = Tag.of(IGrpcMetricConstant.Tag.TAG_METHOD, methodDescriptor.getBareMethodName());
        Tag methodType = Tag.of(IGrpcMetricConstant.Tag.TAG_METHOD_TYPE, methodDescriptor.getType().name());
        Tag serviceName = Tag.of(IGrpcMetricConstant.Tag.TAG_SERVICE, methodDescriptor.getServiceName());
        Iterable<Tag> tags = List.of(method, methodType, serviceName);

        // It might be necessary to use caching here for improved performance
        // request counter
        Counter requestCounter = Counter.builder(getRequestMetadataName()).description(getRequestMetadataName()).tags(tags).register(meterRegistry);
        metricBundle.setRequestCounter(requestCounter);

        // response counter
        Counter responseCounter = Counter.builder(getResponseMetadataName()).description(getResponseMetadataName()).tags(tags)
                .register(meterRegistry);
        metricBundle.setResponseCounter(responseCounter);

        // timer
        Builder timerBuilder = Timer.builder(getTimerMetadataName()).description(getTimerMetadataName()).tags(tags);
        metricBundle.setTimerCodeFunction((code) -> timerBuilder.tag(IGrpcMetricConstant.Tag.TAG_STATUS, code.name()).register(meterRegistry));

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
