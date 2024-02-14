/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.quarkus.extension.deltaspike.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.function.Function;

import jakarta.enterprise.inject.Default;

import org.apache.deltaspike.data.impl.handler.QueryHandler;

import hu.icellmobilsoft.coffee.deltaspike.data.extension.RepositoryExtension;
import io.quarkus.arc.SyntheticCreationalContext;
import io.quarkus.runtime.annotations.Recorder;

/**
 * Recorder for Deltaspike data
 *
 * @since 2.6.0
 * @author speter555
 */
@Recorder
public class DeltaspikeDataRecorder {

    /**
     * Default constructor
     */
    public DeltaspikeDataRecorder() {
        // Default constructor for java 21
    }

    /**
     * Create Repository proxy instances
     * 
     * @param classType
     *            repository class
     * @return function of instance creation
     */
    public Function<SyntheticCreationalContext<Object>, Object> createRepository(Class<?> classType) {
        return tSyntheticCreationalContext -> {
            InvocationHandler handler = tSyntheticCreationalContext.getInjectedReference(QueryHandler.class, new Default.Literal());

            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[] { classType }, handler);
        };
    }

    /**
     * Create RepositoryExtension with repository classes list
     * 
     * @param repositoryClasses
     *            reposiotry classes lsit
     * @return function of instance creation
     */
    public Function<SyntheticCreationalContext<RepositoryExtension>, RepositoryExtension> createRepositoryExtension(
            ArrayList<Class<?>> repositoryClasses) {

        return new Function<SyntheticCreationalContext<RepositoryExtension>, RepositoryExtension>() {

            @Override
            public RepositoryExtension apply(SyntheticCreationalContext<RepositoryExtension> faultTypeClassesSyntheticCreationalContext) {
                return new RepositoryExtension(repositoryClasses);
            }
        };
    }
}
