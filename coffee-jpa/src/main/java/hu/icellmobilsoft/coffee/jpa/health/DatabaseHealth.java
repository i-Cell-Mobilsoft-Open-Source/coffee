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

import java.sql.Connection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import hu.icellmobilsoft.coffee.cdi.health.constants.HealthConstant;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.health.HealthUtil;

/**
 * To support microprofile-health mechanics, this class can check whether the database is reachable within a given timeout.
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
@ApplicationScoped
public class DatabaseHealth {

    private ExecutorService executor;

    /**
     * init executor
     */
    @PostConstruct
    public void init() {
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * 
     * Checking whether the database is reachable
     * 
     * @param dataBaseResourceConfig
     *            configuration to connect the database
     * @return the created {@link HealthCheckResponse} contains information about whether the database is reachable and the connection details
     * @throws BaseException
     *             on error
     */
    public HealthCheckResponse checkDatabaseConnection(DatabaseHealthResourceConfig dataBaseResourceConfig) throws BaseException {
        return checkDatabaseConnection(dataBaseResourceConfig, null);
    }

    /**
     * Checking whether the database is reachable
     * 
     * @param dataBaseResourceConfig
     *            configuration to connect the database
     * @param nodeId
     *            unique identifier for health check
     * @return the created {@link HealthCheckResponse} contains information about whether the database is reachable and the connection details
     * @throws BaseException
     *             on error
     */
    public HealthCheckResponse checkDatabaseConnection(DatabaseHealthResourceConfig dataBaseResourceConfig, String nodeId) throws BaseException {
        if (dataBaseResourceConfig == null) {
            throw new InvalidParameterException("dataBaseResourceConfig is mandatory!");
        }
        if (StringUtils.isBlank(dataBaseResourceConfig.getBuilderName())) {
            throw new InvalidParameterException("builderName is mandatory!");
        }
        if (StringUtils.isBlank(dataBaseResourceConfig.getDatasourceUrl())) {
            throw new InvalidParameterException("datasourceUrl is mandatory!");
        }

        String builderName = dataBaseResourceConfig.getBuilderName();
        String datasourceUrl = dataBaseResourceConfig.getDatasourceUrl();

        // handle not mandatory configs with defaults
        String datasourcePrefix = StringUtils.isBlank(dataBaseResourceConfig.getDatasourcePrefix())
                ? DatabaseHealthConstant.Database.Wildfly.DEFAULT_DATASOURCE_PREFIX
                : dataBaseResourceConfig.getDatasourcePrefix();

        String dsName = StringUtils.isBlank(dataBaseResourceConfig.getDsName()) ? DatabaseHealthConstant.Database.DEFAULT_DATASOURCE_NAME
                : dataBaseResourceConfig.getDsName();

        long connectTimeoutSec = dataBaseResourceConfig.getConnectTimeoutSec() == null ? HealthConstant.Common.DEFAULT_CONNECT_TIMEOUT_SEC
                : dataBaseResourceConfig.getConnectTimeoutSec();

        HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name(builderName);
        builder.withData(HealthConstant.Common.NODE_NAME, HealthUtil.getNodeId(nodeId));
        builder.withData(HealthConstant.Common.URL, datasourceUrl);

        DataSource datasource = getDataSource(datasourcePrefix, dsName);
        if (datasource == null) {
            builder.down();
            return builder.build();
        }

        try {
            connectWithTimeout(datasource, connectTimeoutSec, TimeUnit.SECONDS);
            builder.up();
        } catch (InterruptedException e) {
            builder.down();
            Thread.currentThread().interrupt();
            Logger.getLogger(DatabaseHealth.class).error("Error occurred while establishing connection: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Logger.getLogger(DatabaseHealth.class).error("Error occurred while establishing connection: " + e.getLocalizedMessage(), e);
            builder.down();
        }
        return builder.build();
    }

    private DataSource getDataSource(String datasourcePrefix, String dsName) {
        try {
            Context initContext = new InitialContext();
            return (DataSource) initContext.lookup(datasourcePrefix + dsName);
        } catch (NamingException e) {
            Logger.getLogger(DatabaseHealth.class).error("Error occured while getting datasource: " + e.getLocalizedMessage(), e);
        }
        return null;
    }

    private Connection connectWithTimeout(DataSource datasource, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Future<Connection> future = executor.submit(() -> {
            try (Connection connection = datasource.getConnection()) {
                connection.isValid(1);
                return connection;
            }
        });
        return future.get(timeout, unit);
    }

}
