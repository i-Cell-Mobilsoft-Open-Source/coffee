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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import io.grpc.ManagedChannel;

/**
 * Factory class for grpc producer template
 * 
 * @author czenczl
 * @since 1.14.0
 *
 */
@ApplicationScoped
public class GrpcClientProducerFactory {

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
    public io.grpc.stub.AbstractBlockingStub grpcClientTemplateProducer(final InjectionPoint injectionPoint)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        Optional<GrpcClient> annotation = AnnotationUtil.getAnnotation(injectionPoint, GrpcClient.class);
        String configKey = annotation.map(GrpcClient::configKey).orElse(null);

        if (StringUtils.isBlank(configKey)) {
            throw new IllegalStateException("configKey is required!");
        }

        @SuppressWarnings("unchecked")
        Class<? extends io.grpc.stub.AbstractBlockingStub> pType = (Class<? extends io.grpc.stub.AbstractBlockingStub>) injectionPoint.getAnnotated()
                .getBaseType();

        // only need for blocking stub
        Method stubCreate = pType.getDeclaringClass().getMethod("newBlockingStub", io.grpc.Channel.class);

        Instance<ManagedChannel> instance = CDI.current().select(ManagedChannel.class, new GrpcClient.Literal(configKey));
        ManagedChannel channel = instance.get();
        io.grpc.stub.AbstractBlockingStub blockingStub = (io.grpc.stub.AbstractBlockingStub) stubCreate.invoke(null, channel);

        instance.destroy(channel);

        return blockingStub;
    }

}
