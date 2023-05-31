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
package hu.icellmobilsoft.coffee.grpc.client.extension;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.grpc.client.interceptor.ClientRequestInterceptor;
import hu.icellmobilsoft.coffee.grpc.client.interceptor.ClientResponseInterceptor;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Factory class for grpc producer template
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
@ApplicationScoped
public class ManagedChannelProducer {

    @Inject
    private Logger log;

    @Inject
    private Config config;

    private Map<String, ManagedChannel> managedChannelInstances = new HashMap<>();

    /**
     * produce ManagedChannel
     * 
     * @param injectionPoint
     *            the injection point
     * @return ManagedChannel
     */
    @Produces
    @Dependent
    @GrpcClient(configKey = "")
    public ManagedChannel produceManagedChannel(InjectionPoint injectionPoint) {
        Optional<GrpcClient> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcClient.class);
        String configKey = annotation.map(GrpcClient::configKey).orElse(null);

        return getInstance(configKey);
    }

    private synchronized ManagedChannel getInstance(String configKey) {
        return managedChannelInstances.computeIfAbsent(configKey, v -> createManagedChannel(configKey));
    }

    private ManagedChannel createManagedChannel(String configKey) {
        log.info("Creating ManagedChannel for configKey:[{0}]", configKey);
        try {

            String host = config.getOptionalValue("coffee.grpc.client." + configKey + ".host", String.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Missing configuration property by `configKey` " + configKey + ", must be set with `host` parameter"));

            int port = config.getOptionalValue("coffee.grpc.client." + configKey + ".port", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Missing configuration property by `configKey` " + configKey + ", must be set with `port` parameter"));

            // TODO usePlaintext config
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();

            configureChannelBuilder(channelBuilder);

            return channelBuilder.build();
        } catch (Exception e) {
            log.error(MessageFormat.format("Exception on initializing ManagedChannel for configKey [{0}]: [{1}]", configKey, e.getLocalizedMessage()),
                    e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Customize GRPC Channel Builder: interceptor activating, settings ...
     * 
     * @param channelBuilder
     *            initialized GRPC Channel Builder
     * @throws BaseException
     *             on error
     */
    protected void configureChannelBuilder(ManagedChannelBuilder<?> channelBuilder) throws BaseException {
        // request/response interceptor
        channelBuilder.intercept(new ClientRequestInterceptor());
        channelBuilder.intercept(new ClientResponseInterceptor());
    }
}
