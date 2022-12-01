/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.mongodb.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import jakarta.enterprise.inject.spi.ProcessInjectionTarget;

import hu.icellmobilsoft.coffee.module.mongodb.service.MongoService;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Mongo CDI extension activator class
 * 
 * @author czenczl
 * @since 1.1.0
 * 
 */
public class MongoExtension implements jakarta.enterprise.inject.spi.Extension {

    private static final Logger LOGGER = Logger.getLogger(MongoExtension.class);

    private final List<Type> mongoServiceTypes = new ArrayList<>();

    /**
     * Creates CDI producer beans for the found mongo service implementations
     * 
     * @param abd
     *            event fired by the CDI container when it has fully completed the bean discovery proces
     * @param beanManager
     *            object to interact directly with the CDI container
     */
    @SuppressWarnings("unchecked")
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, BeanManager beanManager) {
        // mongoservice implementation not found
        if (mongoServiceTypes.isEmpty()) {
            return;
        }

        // jboss logger (later we will use java.util.logging.Logger)
        LOGGER.info("MongoExtension is active, found MongoService implementations: [{0}]", mongoServiceTypes.size());

        // find producer template
        AnnotatedMethod<? super MongoServiceProducerFactory> producerMethodTemplate = findProducerMethodTemplate(beanManager);

        // get MongoServiceProducerFactory
        Bean producerFactory = beanManager.getBeans(MongoServiceProducerFactory.class, new Default.Literal()).iterator().next();

        // create producer attributes based on the template
        final BeanAttributes<?> producerAttributes = beanManager.createBeanAttributes(producerMethodTemplate);

        // iterate mongoService impl classes and generate producers
        for (Type type : mongoServiceTypes) {
            Bean<?> bean = beanManager.createBean(new DelegatingBeanAttributes<>(producerAttributes) {
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

            }, MongoServiceProducerFactory.class, beanManager.getProducerFactory(producerMethodTemplate, producerFactory));

            // add producer bean
            abd.addBean(bean);
        }

        // free memory
        mongoServiceTypes.clear();
    }

    /**
     * processInjectionTarget CDI event, collect MongoService types
     * 
     * @param <T>
     *            The managed bean class
     * @param pit
     *            Java EE component class supporting injection that may be instantiated by the container at runtime.
     */
    @SuppressWarnings("unchecked")
    public <T> void processInjectionTarget(final @Observes ProcessInjectionTarget<T> pit) {
        AnnotatedType<T> at = pit.getAnnotatedType();
        Class<T> pType = (Class<T>) at.getBaseType();

        // get base MongoService class
        Type baseType = MongoExtensionUtil.getMongoServiceBase(pType);

        if (baseType != null) {
            mongoServiceTypes.add(pType);
        }

    }

    @SuppressWarnings("unused")
    private AnnotatedMethod<? super MongoServiceProducerFactory> findProducerMethodTemplate(BeanManager beanManager) {
        // get producer template method
        AnnotatedType<MongoServiceProducerFactory> mongoServiceProducerFactoryType = beanManager
                .createAnnotatedType(MongoServiceProducerFactory.class);
        Set<AnnotatedMethod<? super MongoServiceProducerFactory>> methods = mongoServiceProducerFactoryType.getMethods();

        // find method by return type
        return methods.stream().filter(m -> m.getJavaMember().getReturnType() == MongoService.class).findFirst().get();
    }

}
