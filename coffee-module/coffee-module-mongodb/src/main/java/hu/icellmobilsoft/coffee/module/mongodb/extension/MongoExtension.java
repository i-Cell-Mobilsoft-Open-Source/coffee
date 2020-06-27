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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ProcessInjectionTarget;

import hu.icellmobilsoft.coffee.module.mongodb.service.MongoService;

/**
 * Mongo CDI extension activator class
 * 
 * @author czenczl
 *
 */
public class MongoExtension implements javax.enterprise.inject.spi.Extension {

    private static final Logger log = Logger.getLogger(MongoExtension.class.getName());

    private List<Type> mongoServiceTypes = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abd, BeanManager beanManager) {
        // mongoservice implementation not found
        if (mongoServiceTypes.isEmpty()) {
            return;
        }
        log.info("MongoExtension is active, found MongoService implementations: " + mongoServiceTypes.size());

        // find producer template
        AnnotatedMethod<? super MongoServiceProducerFactory> producerMethodTemplate = findProducerMethodTemplate(beanManager);

        // get MongoServiceProducerFactory
        Bean producerFactory = beanManager.getBeans(MongoServiceProducerFactory.class, new Default.Literal()).iterator().next();

        // create producer attributes based on the template
        final BeanAttributes<?> producerAttributes = beanManager.createBeanAttributes(producerMethodTemplate);

        // iterate mongoService impl classes and generate producers
        for (Type type : mongoServiceTypes) {
            Bean<?> bean = beanManager.createBean(new DelegatingBeanAttributes<Object>(producerAttributes) {
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
     * @param pit
     */
    @SuppressWarnings("unchecked")
    public <T> void processInjectionTarget(final @Observes ProcessInjectionTarget<T> pit) {
        AnnotatedType<T> at = pit.getAnnotatedType();
        Class<T> pType = (Class<T>) at.getBaseType();

        if (!(pType.getGenericSuperclass() instanceof ParameterizedType)) {
            return;
        }

        ParameterizedType superType = (ParameterizedType) pType.getGenericSuperclass();

        if (superType.getRawType() == MongoService.class) {
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
        return mongoServiceProducerFactoryType.getMethods().stream().filter(m -> m.getJavaMember().getReturnType() == MongoService.class).findFirst()
                .get();
    }

}
