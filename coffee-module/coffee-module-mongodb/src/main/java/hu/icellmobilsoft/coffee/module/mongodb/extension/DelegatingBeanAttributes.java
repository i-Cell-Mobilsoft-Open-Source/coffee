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
import java.util.Set;

import javax.enterprise.inject.spi.BeanAttributes;

/**
 * Delegate class for Bean creation
 * 
 * @author czenczl
 *
 * @param <T>
 */
public class DelegatingBeanAttributes<T> implements BeanAttributes<T> {

    private final BeanAttributes<?> delegate;

    public DelegatingBeanAttributes(final BeanAttributes<?> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return this.delegate.getQualifiers();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return this.delegate.getScope();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return this.delegate.getStereotypes();
    }

    @Override
    public Set<Type> getTypes() {
        return this.delegate.getTypes();
    }

    @Override
    public boolean isAlternative() {
        return this.delegate.isAlternative();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

}
