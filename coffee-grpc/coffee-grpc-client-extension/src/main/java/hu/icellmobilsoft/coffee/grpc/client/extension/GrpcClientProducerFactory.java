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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractBlockingStub;

/**
 * Factory class for grpc producer template
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@ApplicationScoped
public class GrpcClientProducerFactory {

    /**
     * {@link CallOptions.Key} for configValue parameter
     */
    public static final CallOptions.Key<String> CONFIG_VALUE_KEY = CallOptions.Key.createWithDefault("configValue", StringUtils.EMPTY);

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcClientProducerFactory() {
        super();
    }

    /**
     * Producer template bean
     * 
     * @param injectionPoint
     *            Provides access to metadata about an injection point
     * @return concrete io.grpc.stub.AbstractBlockingStub implementation by extension
     * @throws NoSuchMethodException
     *             if a matching method is not found by the declared class of io.grpc.stub.AbstractBlockingStub
     * @throws SecurityException
     *             if checkPackageAccess() denies
     * @throws IllegalAccessException
     *             if 'newBlockingStub' Method is enforcing Java language access control and the underlying method is inaccessible.
     * @throws IllegalArgumentException
     *             if the number of actual and formal parameters differ
     * @throws InvocationTargetException
     *             if the 'newBlockingStub' throws an exception
     */
    @SuppressWarnings("rawtypes")
    @Dependent
    @GrpcClient(configKey = "")
    public AbstractBlockingStub grpcClientTemplateProducer(final InjectionPoint injectionPoint)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        Optional<GrpcClient> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcClient.class);
        String configKey = annotation.map(GrpcClient::configKey).orElse(null);

        if (StringUtils.isBlank(configKey)) {
            throw new IllegalStateException("configKey is required!");
        }

        @SuppressWarnings("unchecked")
        Class<? extends AbstractBlockingStub> pType = (Class<? extends AbstractBlockingStub>) injectionPoint.getAnnotated().getBaseType();

        // only need for blocking stub
        Method stubCreate = pType.getDeclaringClass().getMethod("newBlockingStub", Channel.class);

        Instance<ManagedChannel> instance = CDI.current().select(ManagedChannel.class, new GrpcClient.Literal(configKey));
        ManagedChannel channel = instance.get();
        AbstractBlockingStub blockingStub = (AbstractBlockingStub) stubCreate.invoke(null, channel);

        instance.destroy(channel);

        return (AbstractBlockingStub) blockingStub.withOption(CONFIG_VALUE_KEY, configKey);
    }

}
