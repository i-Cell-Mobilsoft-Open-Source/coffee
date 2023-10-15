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
import java.util.Set;

import jakarta.enterprise.inject.spi.BeanAttributes;

/**
 * Delegate class for Bean creation
 * 
 * @author czenczl
 * @since 2.1.0
 *
 * @param <T>
 *            the class of the bean instance
 */
public class GrpcDelegatingBeanAttributes<T> implements BeanAttributes<T> {

    private final BeanAttributes<?> beanAttributes;

    /**
     * Instantiates the class with the delegate object
     * 
     * @param delegate
     *            the delegate object
     */
    public GrpcDelegatingBeanAttributes(final BeanAttributes<?> delegate) {
        beanAttributes = delegate;
    }

    @Override
    public String getName() {
        return beanAttributes.getName();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return beanAttributes.getQualifiers();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return beanAttributes.getScope();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return beanAttributes.getStereotypes();
    }

    @Override
    public Set<Type> getTypes() {
        return beanAttributes.getTypes();
    }

    @Override
    public boolean isAlternative() {
        return beanAttributes.isAlternative();
    }

    @Override
    public String toString() {
        return beanAttributes.toString();
    }

}
