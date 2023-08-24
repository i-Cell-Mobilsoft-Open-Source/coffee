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
package hu.icellmobilsoft.coffee.module.redis.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricType;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.redis.metrics.JedisMetricsConstants;
import hu.icellmobilsoft.coffee.module.redis.metrics.JedisMetricsHandler;
import redis.clients.jedis.JedisPool;

/**
 * 
 * Test for overriding @{JedisMetricsHandler}
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
@ApplicationScoped
@Alternative
public class CustomJedisMetricsHandler extends JedisMetricsHandler {

    @Inject
    private MetricRegistry metricRegistry;

    /**
     * Provides metrics for the specified connection pool.
     * 
     * @param configKey
     *            Redis connection config key
     * @param poolConfigKey
     *            Redis connection pool config key
     * @param jedisPool
     *            {@link JedisPool} handle connection
     * @throws BaseException
     *             if wrong config provided
     */
    @Override
    public void addMetric(String configKey, String poolConfigKey, JedisPool jedisPool) throws BaseException {
        if (StringUtils.isBlank(configKey)) {
            throw new InvalidParameterException("configKey is mandatory!");
        }
        if (StringUtils.isBlank(poolConfigKey)) {
            throw new InvalidParameterException("poolConfigKey is mandatory!");
        }
        if (jedisPool == null) {
            throw new InvalidParameterException("jedisPool is mandatory!");
        }

        Metadata metadataActive = Metadata.builder()
                .withName(JedisMetricsConstants.Gauge.COFFEE_JEDIS_POOL_ACTIVE)
                .withDescription(JedisMetricsConstants.Description.COFFEE_JEDIS_POOL_ACTIVE_DESCRIPTION)
                .withType(MetricType.GAUGE)
                .build();

        metricRegistry.gauge(metadataActive, jedisPool::getNumActive);
    }

}
