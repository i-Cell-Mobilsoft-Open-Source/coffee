/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package hu.icellmobilsoft.coffee.deltaspike.data.extension;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeShutdown;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.data.api.AbstractEntityRepository;
import org.apache.deltaspike.data.api.AbstractFullEntityRepository;
import org.apache.deltaspike.data.api.Repository;
import org.apache.deltaspike.data.impl.handler.QueryHandler;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * The main extension class for Repositories based on dynamic proxy mechanics. <br/>
 * 
 * <b>{@code @Observes ProcessAnnotatedType<X>}</b>: Looks for types annotated with {@link Repository}. Repositories are validated and preprocessed -
 * all the methods on the repository are checked and analyzed for better runtime performance.<br/>
 * 
 * <b>{@code @Observes AfterBeanDiscovery}</b>: Create proxies for Repository classes, configuring {@link QueryHandler}
 * 
 * @author czenczl
 * @since 2.0.0
 *
 */
public class RepositoryExtension implements Extension {
    private static final Logger LOGGER = Logger.getLogger(RepositoryExtension.class);

    private final ArrayList<Class<?>> repositoryClasses = new ArrayList<Class<?>>();

    /**
     * Collect repository classes by {@link Repository} stereotype
     * 
     * @param <X>
     *            The class being annotated
     * @param event
     *            The container fires an event of this type for each Java class or interface it discovers in a bean archive, before it reads the
     *            declared annotations.
     */
    public <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event) {
        // veto abstract repository
        if (isAbstractRepository(event.getAnnotatedType())) {
            event.veto();
        } else if (isRepository(event.getAnnotatedType())) {
            Class<X> repositoryClass = event.getAnnotatedType().getJavaClass();

            LOGGER.info("Repository annotation detected on [{0}]", event.getAnnotatedType());

            repositoryClasses.add(repositoryClass);
        }
    }

    /**
     * 
     * Handles proxy creation for Repository interfaces with central InvocationHandler: {@link QueryHandler}
     * 
     * @param abd
     *            event fired by the CDI container when it has fully completed the bean discovery proces
     * @param beanManager
     *            object to interact directly with the CDI container
     */
    public void createProxyInstances(@Observes final AfterBeanDiscovery abd, final BeanManager beanManager) {
        repositoryClasses.forEach(type -> {
            abd.addBean().id("CoffeeRepository#" + type.getName()) //
                    .scope(ApplicationScoped.class) //
                    .types(type, Object.class) //
                    .beanClass(type).qualifiers(Default.Literal.INSTANCE, Any.Literal.INSTANCE) //
                    .createWith(ctx -> {
                        // inject fázisban amikor az implementáló projekten hasznalva van repository, akkor itt proxy objektumokon keresztül kezeljük
                        // a hivast egy központi InvocationHandler segítségével
                        // a proxy letrehozás lényegében kiváltja régi asm által létrehozott fix implementációkat.
                        // ezen a ponton lényegeben megadjuk hogy minden repository interface-ben talalhato metodus keresztul menjen a QueryHandler
                        // osztalyon
                        final InvocationHandler handler = (InvocationHandler) beanManager.getReference(
                                beanManager.resolve(beanManager.getBeans(QueryHandler.class, new Default.Literal())), QueryHandler.class,
                                beanManager.createCreationalContext(null));

                        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { type }, handler);
                    });
        });

    }

    private <X> boolean isRepository(AnnotatedType<X> annotatedType) {
        return (annotatedType.isAnnotationPresent(Repository.class) || annotatedType.getJavaClass().isAnnotationPresent(Repository.class))
                && !InvocationHandler.class.isAssignableFrom(annotatedType.getJavaClass());
    }

    private <X> boolean isAbstractRepository(AnnotatedType<X> annotated) {
        Class<X> javaClass = annotated.getJavaClass();
        return javaClass.equals(AbstractEntityRepository.class) || javaClass.equals(AbstractFullEntityRepository.class);
    }

    /**
     * get the collected repository classes
     * 
     * @return collected repository classes
     */
    public ArrayList<Class<?>> getRepositoryClasses() {
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();

        if (!repositoryClasses.isEmpty()) {
            result.addAll(repositoryClasses);
        }

        return result;
    }

    /**
     * cleanup stored classes
     * 
     * @param beforeShutdown
     *            event before termination
     */
    protected void cleanup(@Observes BeforeShutdown beforeShutdown) {
        repositoryClasses.clear();
    }
}
