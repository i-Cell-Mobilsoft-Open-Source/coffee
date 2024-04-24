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

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanAttributes;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;

import org.apache.commons.lang3.reflect.TypeUtils;

import hu.icellmobilsoft.coffee.grpc.client.GrpcClient;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.stub.AbstractBlockingStub;

/**
 * Extension for gRPC client injection
 * 
 * @author czenczl
 * @since 2.1.0
 */
public class GrpcClientExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(GrpcClientExtension.class);

    private Map<Type, Annotation> grpcClientMap = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcClientExtension() {
        super();
    }

    /**
     * Creates CDI producer beans for gRPC clients
     * 
     * @param abd
     *            event fired by the CDI container when it has fully completed the bean discovery process
     * @param beanManager
     *            object to interact directly with the CDI container
     */
    @SuppressWarnings("unchecked")
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, BeanManager beanManager) {
        LOGGER.info("gRPC client extension is active");

        // find producer template
        AnnotatedMethod<? super GrpcClientProducerFactory> producerMethodTemplate = findProducerMethodTemplate(beanManager);
        final BeanAttributes<?> producerAttributes = beanManager.createBeanAttributes(producerMethodTemplate);

        // get grpc client template
        @SuppressWarnings("rawtypes")
        Bean producerFactory = beanManager.getBeans(GrpcClientProducerFactory.class, new Default.Literal()).iterator().next();

        for (Type type : grpcClientMap.keySet()) {
            Bean<?> bean = beanManager.createBean(new GrpcDelegatingBeanAttributes<>(producerAttributes) {
                @Override
                public final Set<Type> getTypes() {
                    final Set<Type> types = new HashSet<>();
                    types.add(Object.class);
                    types.add(type);
                    return types;
                }

                @Override
                public Class<? extends Annotation> getScope() {
                    // A producer method with a parameterized return type with a type variable must be declared @Dependent scoped
                    return Dependent.class;
                }

            }, GrpcClientProducerFactory.class, beanManager.getProducerFactory(producerMethodTemplate, producerFactory));

            // add producer bean
            abd.addBean(bean);
        }
        LOGGER.info("Found gRPC clients: [{0}]", grpcClientMap.keySet().size());

    }

    /**
     * Collect {@code GrpcClient} injections
     * 
     * @param <T>
     *            generic
     * @param <X>
     *            generic
     * @param pip
     *            The container fires an event of this type for every injection point of every Java EE component class supporting injection that may
     *            be instantiated by the container at runtime
     */
    public <T, X> void processInjectionTarget(final @Observes ProcessInjectionPoint<T, X> pip) {
        InjectionPoint ip = pip.getInjectionPoint();

        for (Annotation annotation : ip.getQualifiers()) {
            if (annotation.annotationType() == GrpcClient.class) {
                Type type = ip.getType();
                if (TypeUtils.isAssignable(type, AbstractBlockingStub.class)) {
                    LOGGER.debug("Found @Inject @GrpcClient AbstractBlockingStub: [{0}]", type);
                    GrpcClient grpcClient = (GrpcClient) annotation;
                    grpcClientMap.put(ip.getType(), grpcClient);
                }
            }
        }

    }

    private AnnotatedMethod<? super GrpcClientProducerFactory> findProducerMethodTemplate(BeanManager beanManager) {
        // get producer template method
        AnnotatedType<GrpcClientProducerFactory> factory = beanManager.createAnnotatedType(GrpcClientProducerFactory.class);
        Set<AnnotatedMethod<? super GrpcClientProducerFactory>> methods = factory.getMethods();

        // find method by return type
        return methods.stream().filter(m -> m.getJavaMember().getReturnType() == AbstractBlockingStub.class).findFirst().get();
    }

}
