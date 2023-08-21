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
package hu.icellmobilsoft.coffee.module.etcd.health;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import hu.icellmobilsoft.coffee.cdi.health.constants.HealthConstant;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.etcd.config.DefaultEtcdConfigImpl;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.health.HealthUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;

/**
 * To support microprofile-health mechanics, this class can check whether the etcd is reachable within a given timeout.
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
@ApplicationScoped
public class EtcdHealth {

    @Inject
    private DefaultEtcdConfigImpl etcdConfig;

    /**
     * Checking whether the etcd is reachable
     * 
     * @param builderName
     *            the name of the health check response
     * @return The created {@link HealthCheckResponse} contains information about whether the etcd server is reachable.
     * @throws BaseException
     *             if wrong config provided
     */
    public HealthCheckResponse checkConnection(String builderName) throws BaseException {
        return checkConnection(builderName, null);
    }

    /**
     * Checking whether the etcd is reachable
     * 
     * @param builderName
     *            the name of the health check response
     * @param nodeId
     *            unique identifier for health check
     * @return The created {@link HealthCheckResponse} contains information about whether the etcd server is reachable.
     * @throws BaseException
     *             if wrong config provided
     */
    public HealthCheckResponse checkConnection(String builderName, String nodeId) throws BaseException {
        if (StringUtils.isBlank(builderName)) {
            throw new InvalidParameterException("builderName is mandatory!");
        }
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name(builderName);
        builder.withData(HealthConstant.Common.NODE_NAME, HealthUtil.getNodeId(nodeId));
        String[] url = etcdConfig.getUrl();
        builder.withData(HealthConstant.Common.URL, String.join(",", url));

        try (Client c = Client.builder().endpoints(url).build()) {
            c.getKVClient()
                    .get(ByteSequence.from("0", StandardCharsets.UTF_8))
                    .get(HealthConstant.Common.DEFAULT_CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS);
            builder.up();
        } catch (InterruptedException e) {
            builder.down();
            Thread.currentThread().interrupt();
            Logger.getLogger(EtcdHealth.class).error("Error occurred while establishing connection: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Logger.getLogger(EtcdHealth.class).error("Error occurred while establishing connection: " + e.getLocalizedMessage(), e);
            builder.down();
        }
        return builder.build();

    }
}
