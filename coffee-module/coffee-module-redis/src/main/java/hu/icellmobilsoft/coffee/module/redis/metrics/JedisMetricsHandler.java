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
package hu.icellmobilsoft.coffee.module.redis.metrics;

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.Tag;

import hu.icellmobilsoft.coffee.cdi.metric.constants.JedisMetricsConstants;
import hu.icellmobilsoft.coffee.cdi.metric.spi.IJedisMetricsHandler;
import hu.icellmobilsoft.coffee.cdi.metric.spi.MetricsHandlerQualifier;
import redis.clients.jedis.JedisPool;

/**
 * Provides metrics for {@link JedisPool}
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
@ApplicationScoped
@MetricsHandlerQualifier
public class JedisMetricsHandler implements IJedisMetricsHandler {

    @Inject
    private MetricRegistry metricRegistry;

    /**
     * Default constructor, constructs a new object.
     */
    public JedisMetricsHandler() {
        super();
    }

    @Override
    public void addMetric(String configKey, String poolConfigKey, Supplier<Number> activeConnectionSupplier, Supplier<Number> idleConnectionSupplier) {
        // config
        Tag configKeyTag = new Tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_CONFIG_KEY, configKey);
        Tag poolConfigKeyTag = new Tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_POOL_CONFIG_KEY, poolConfigKey);

        Metadata metadataActive = Metadata.builder()
                .withName(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_ACTIVE)
                .withDescription(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_ACTIVE_DESCRIPTION)
                .withType(MetricType.GAUGE)
                .build();
        metricRegistry.gauge(metadataActive, activeConnectionSupplier, configKeyTag, poolConfigKeyTag);

        Metadata metadataIdle = Metadata.builder()
                .withName(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_IDLE)
                .withDescription(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_IDLE_DESCRIPTION)
                .withType(MetricType.GAUGE)
                .build();
        metricRegistry.gauge(metadataIdle, idleConnectionSupplier, configKeyTag, poolConfigKeyTag);
    }
}
