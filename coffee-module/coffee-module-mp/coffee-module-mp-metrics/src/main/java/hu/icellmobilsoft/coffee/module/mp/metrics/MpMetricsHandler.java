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
package hu.icellmobilsoft.coffee.module.mp.metrics;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import hu.icellmobilsoft.coffee.cdi.metric.MetricTag;
import hu.icellmobilsoft.coffee.cdi.metric.spi.IMetricsHandler;
import hu.icellmobilsoft.coffee.cdi.metric.spi.MetricsHandlerQualifier;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Provides metrics for microprofile-metrics implementation
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 *
 */
@ApplicationScoped
@MetricsHandlerQualifier
public class MpMetricsHandler implements IMetricsHandler {

    @Inject
    @RegistryType(type = MetricRegistry.Type.VENDOR)
    private MetricRegistry vendorRegistry;

    /**
     * Default constructor, constructs a new object.
     */
    public MpMetricsHandler() {
        super();
    }

    @Override
    public Double searchGauge(String name, MetricTag... metricTags) {
        try {
            MetricID metricID = null;
            Collection<Tag> tags = new ArrayList<>();
            if (metricTags != null && metricTags.length > 0) {
                for (MetricTag metricTag : metricTags) {
                    tags.add(new Tag(metricTag.getKey(), metricTag.getValue()));
                }
                metricID = new MetricID(name, tags.toArray(new Tag[tags.size()]));
            } else {
                metricID = new MetricID(name);
            }
            Gauge<?> gauge = vendorRegistry.getGauge(metricID);
            return (Double) gauge.getValue();
        } catch (Exception e) {
            Logger.getLogger(MpMetricsHandler.class).error(MessageFormat.format("Exception when trying to get vendor specific [{0}] metric!", name),
                    e);
            return null;
        }
    }
}
