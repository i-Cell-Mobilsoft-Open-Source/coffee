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
package hu.icellmobilsoft.coffee.module.mp.micrometer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.metric.MetricTag;
import hu.icellmobilsoft.coffee.cdi.metric.spi.IMetricsHandler;
import hu.icellmobilsoft.coffee.cdi.metric.spi.MetricsHandlerQualifier;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

/**
 * Provides metrics for microprofile-metrics implementation
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 *
 */
@ApplicationScoped
@MetricsHandlerQualifier
public class MicrometerHandler implements IMetricsHandler {

    @Inject
    private MeterRegistry meterRegistry;

    /**
     * Default constructor, constructs a new object.
     */
    public MicrometerHandler() {
        super();
    }

    @Override
    public Double searchGauge(String name, MetricTag... metricTags) {
        try {
            Collection<Tag> tags = new ArrayList<>();
            if (tags != null && metricTags.length > 0) {
                for (MetricTag metricTag : metricTags) {
                    tags.add(Tag.of(metricTag.getKey(), metricTag.getValue()));
                }
            }
            Gauge gauge = meterRegistry.find(name).tags(tags).gauge();
            return gauge != null ? gauge.value() : null;
        } catch (Exception e) {
            Logger.getLogger(MicrometerHandler.class).error(MessageFormat.format("Exception when trying to get [{0}] metric!", name), e);
            return null;
        }
    }
}
