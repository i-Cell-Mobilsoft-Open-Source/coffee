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
package hu.icellmobilsoft.coffee.grpc.client.config;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Producer for creating GrpcClientConfig
 *
 * @author Imre Scheffer
 * @since 2.1.0
 */
@ApplicationScoped
public class GrpcClientConfigProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcClientConfigProducer() {
        super();
    }

    /**
     * Creates {@link GrpcClientConfig} for the injected config key
     *
     * @param injectionPoint
     *            injection metadata
     * @return GrpcClientConfig
     */
    @Produces
    @Dependent
    @GrpcClient(configKey = "")
    public GrpcClientConfig produceGrpcServerConfig(InjectionPoint injectionPoint) {
        Optional<GrpcClient> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcClient.class);
        String configKey = annotation.map(GrpcClient::configKey).orElse(null);
        GrpcClientConfig grpcClientConfig = CDI.current().select(GrpcClientConfig.class).get();
        grpcClientConfig.setConfigKey(configKey);
        return grpcClientConfig;
    }
}
