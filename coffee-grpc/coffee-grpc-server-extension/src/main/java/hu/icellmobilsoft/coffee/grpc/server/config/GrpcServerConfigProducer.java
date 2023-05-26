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
package hu.icellmobilsoft.coffee.grpc.server.config;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Producer for creating ManagedRedisConfig
 *
 * @author czenczl
 * @since 1.14.0
 */
@ApplicationScoped
public class GrpcServerConfigProducer {

    /**
     * Creates {@link GrpcServerConfig} for the injected config key
     *
     * @param injectionPoint
     *            injection metadata
     * @return GrpcServerConfig
     */
    @Produces
    @Dependent
    @GrpcServerConnection(configKey = "")
    public GrpcServerConfig produceGrpcServerConfig(InjectionPoint injectionPoint) {
        Optional<GrpcServerConnection> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcServerConnection.class);
        String configKey = annotation.map(GrpcServerConnection::configKey).orElse(null);
        GrpcServerConfig grpcServerConfig = CDI.current().select(GrpcServerConfig.class).get();
        grpcServerConfig.setConfigKey(configKey);
        return grpcServerConfig;
    }

}
