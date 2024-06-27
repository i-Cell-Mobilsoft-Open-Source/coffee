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
package hu.icellmobilsoft.coffee.grpc.server.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.grpc.server.GrpcServerManager;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * To support microprofile-health mechanics, this class can check whether the gRPC server is reachable.
 *
 * @author karoly.tamas
 * @since 2.7.1
 */
@ApplicationScoped
public class GrpcHealth {

    @Inject
    private GrpcServerManager grpcServerManager;

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcHealth() {
        super();
    }

    /**
     * Checking whether the gRPC server is reachable
     *
     * @param builderName the name of the health check response
     * @return The created {@link HealthCheckResponse} contains information about whether the gRPC server is reachable.
     * @throws BaseException if check failed
     */
    public HealthCheckResponse check(String builderName) throws BaseException {
        if (StringUtils.isBlank(builderName)) {
            throw new InvalidParameterException("builderName is mandatory!");
        }
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name(builderName);

        try {
            if (grpcServerManager.isStarted()) {
                builder.up();
            } else {
                builder.down();
            }
        } catch (Exception e) {
            Logger.getLogger(GrpcHealth.class).error("Error occurred: " + e.getLocalizedMessage(), e);
            builder.down();
        }
        return builder.build();

    }
}
