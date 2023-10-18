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
package hu.icellmobilsoft.coffee.jpa.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import hu.icellmobilsoft.coffee.cdi.health.constants.HealthConstant;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.health.HealthUtil;

/**
 * To support microprofile-health mechanics, this class can check whether the database connection pool usage is below the desired threshold.
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
@ApplicationScoped
public class DatabasePoolHealth {

    @Inject
    private Config config;

    @Inject
    @RegistryType(type = MetricRegistry.Type.VENDOR)
    private MetricRegistry vendorRegistry;

    /**
     * Default constructor, constructs a new object.
     */
    public DatabasePoolHealth() {
        super();
    }

    /**
     * Checking the database connection pool usage
     * 
     * @param builderName
     *            the name of the health check response
     * @return The created {@link HealthCheckResponse} contains information about whether the database connection pool usage is below the desired
     *         threshold.
     * @throws BaseException
     *             if wrong config provided
     */
    public HealthCheckResponse checkDatabasePoolUsage(String builderName) throws BaseException {
        return checkDatabasePoolUsage(builderName, null);
    }

    /**
     * Checking the database connection pool usage
     * 
     * @param builderName
     *            the name of the health check response
     * @param nodeId
     *            unique identifier for health check
     * @return The created {@link HealthCheckResponse} contains information about whether the database connection pool usage is below the desired
     *         threshold.
     * @throws BaseException
     *             if wrong config provided
     */
    public HealthCheckResponse checkDatabasePoolUsage(String builderName, String nodeId) throws BaseException {
        if (StringUtils.isBlank(builderName)) {
            throw new InvalidParameterException("builderName is mandatory!");
        }
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name(builderName);
        builder.withData(HealthConstant.Common.NODE_NAME, HealthUtil.getNodeId(nodeId));

        // ENV config, default 100, 100 = turned off
        Integer usagePercentTreshold = config
                .getOptionalValue(DatabaseHealthConstant.Database.Pool.DATASOURCE_POOL_USAGE_TRESHOLD_PERCENT, Integer.class)
                .orElse(100);

        Integer maxPoolSize = config.getOptionalValue(DatabaseHealthConstant.Database.Pool.DATASOURCE_MAX_POOL_SIZE, Integer.class).orElse(60);

        double poolInUse = poolInUse();
        double usagePercent = (poolInUse / maxPoolSize) * 100;
        builder.withData(DatabaseHealthConstant.Database.Pool.DATASOURCE_POOL_USAGE_PERCENT, usagePercent + "%");
        builder.withData(DatabaseHealthConstant.Database.Pool.DATASOURCE_POOL_USAGE_TRESHOLD_PERCENT, usagePercentTreshold + "%");

        if (usagePercent > usagePercentTreshold) {
            builder.down();
        } else {
            builder.up();
        }

        return builder.build();
    }

    private double poolInUse() {
        try {
            MetricID inUseId = new MetricID(
                    DatabaseHealthConstant.Database.Wildfly.Metric.WILDFLY_DATASOURCES_POOL_IN_USE_COUNT,
                    new Tag(DatabaseHealthConstant.Database.Wildfly.Metric.DATA_SOURCE_TAG, DatabaseHealthConstant.Database.DEFAULT_DATASOURCE_NAME));
            Gauge<?> inUseGauge = vendorRegistry.getGauge(inUseId);
            Double inUse = (Double) inUseGauge.getValue();
            return inUse;
        } catch (Exception e) {
            Logger.getLogger(DatabasePoolHealth.class).error("Exception when trying to get vendor specific pool metric!", e);
            return 0;
        }
    }

}
