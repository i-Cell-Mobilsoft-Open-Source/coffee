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
package hu.icellmobilsoft.coffee.model.base.audit;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.data.api.audit.CurrentUser;
import org.apache.deltaspike.data.impl.audit.AuditPropertyException;
import org.apache.deltaspike.data.impl.audit.PrePersistAuditListener;
import org.apache.deltaspike.data.impl.audit.PreUpdateAuditListener;
import org.apache.deltaspike.data.impl.property.Property;
import org.apache.deltaspike.data.impl.property.query.AnnotatedPropertyCriteria;
import org.apache.deltaspike.data.impl.property.query.PropertyQueries;
import org.apache.deltaspike.data.impl.property.query.PropertyQuery;

import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;

/**
 * Perist entity @CreatedBy property before persist with the value provided by @CurrentUser
 *
 * Update entity @ModifiedBy property before update with the value provided by @CurrentUser
 *
 * @author czenczl
 * @since 1.0.0
 */
public class AuditProvider implements PrePersistAuditListener, PreUpdateAuditListener {

    @Inject
    private BeanManager manager;

    /** {@inheritDoc} */
    @Override
    public void prePersist(Object entity) {
        PropertyQuery<Object> createdByQuery = PropertyQueries.<Object> createQuery(entity.getClass())
                .addCriteria(new AnnotatedPropertyCriteria(CreatedBy.class));
        for (Property<Object> property : createdByQuery.getWritableResultList()) {
            Object value = resolvePrincipal(entity, property);
            setProperty(entity, property, value);
        }

        PropertyQuery<Object> modifiedByQuery = PropertyQueries.<Object> createQuery(entity.getClass())
                .addCriteria(new AnnotatedPropertyCriteria(ModifiedBy.class));
        for (Property<Object> property : modifiedByQuery.getWritableResultList()) {
            ModifiedBy annotation = property.getAnnotatedElement().getAnnotation(ModifiedBy.class);
            if (annotation.onCreate()) {
                Object newValue = resolvePrincipal(entity, property);
                setProperty(entity, property, newValue);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void preUpdate(Object entity) {
        PropertyQuery<Object> query = PropertyQueries.<Object> createQuery(entity.getClass())
                .addCriteria(new AnnotatedPropertyCriteria(ModifiedBy.class));
        for (Property<Object> property : query.getWritableResultList()) {
            Object newValue = resolvePrincipal(entity, property);
            setProperty(entity, property, newValue);
        }
    }

    private void setProperty(Object entity, Property<Object> property, Object value) {
        try {
            property.setValue(entity, value);
        } catch (Exception e) {
            throw new AuditPropertyException(
                    "Failed to write value [" + value + "] to entity [" + entity.getClass() + "]: " + e.getLocalizedMessage(), e);
        }
    }

    private Object resolvePrincipal(Object entity, Property<Object> property) {
        CurrentUser principal = AnnotationInstanceProvider.of(CurrentUser.class);
        Class<?> propertyClass = property.getJavaClass();
        Set<Bean<?>> beans = manager.getBeans(propertyClass, principal);
        if (!beans.isEmpty() && beans.size() == 1) {
            Bean<?> bean = beans.iterator().next();
            Object result = manager.getReference(bean, propertyClass, manager.createCreationalContext(bean));
            return result;
        }
        throw new IllegalArgumentException("Principal " + (beans.isEmpty() ? "not found" : "not unique"));
    }

}
