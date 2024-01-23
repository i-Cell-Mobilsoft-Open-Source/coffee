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

import java.util.function.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.metric.constants.JedisMetricsConstants;
import hu.icellmobilsoft.coffee.cdi.metric.spi.IJedisMetricsHandler;
import hu.icellmobilsoft.coffee.cdi.metric.spi.MetricsHandlerQualifier;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Provides metrics for Jedis connection pool
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 *
 */
@ApplicationScoped
@MetricsHandlerQualifier
public class JedisMicrometerHandler implements IJedisMetricsHandler {

    @Inject
    private MeterRegistry meterRegistry;

    /**
     * Default constructor, constructs a new object.
     */
    public JedisMicrometerHandler() {
        super();
    }

    @Override
    public void addMetric(String configKey, String poolConfigKey, Supplier<Number> activeConnectionSupplier,
            Supplier<Number> idleConnectionSupplier) {

        Gauge.builder(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_ACTIVE, activeConnectionSupplier)
                .description(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_ACTIVE_DESCRIPTION)
                .tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_CONFIG_KEY, configKey)
                .tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_POOL_CONFIG_KEY, poolConfigKey).register(meterRegistry);

        Gauge.builder(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_IDLE, idleConnectionSupplier)
                .description(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_IDLE_DESCRIPTION)
                .tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_CONFIG_KEY, configKey)
                .tag(JedisMetricsConstants.Tag.COFFEE_JEDIS_POOL_CONFIG_KEY, poolConfigKey).register(meterRegistry);
    }
}
