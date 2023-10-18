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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.apache.commons.lang3.tuple.Pair;

import hu.icellmobilsoft.coffee.model.base.AbstractProvider;
import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.CurrentUser;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;
import hu.icellmobilsoft.coffee.model.base.exception.ProviderException;

/**
 * Persist entity @CreatedBy property before persist with the value provided by @CurrentUser
 *
 * Update entity @ModifiedBy property before update with the value provided by @CurrentUser
 *
 * @author czenczl
 * @author zsolt.vasi
 * @since 1.0.0
 */
@Dependent
public class AuditProvider extends AbstractProvider {

    @Inject
    private BeanManager manager;

    /**
     * Default constructor, constructs a new object.
     */
    public AuditProvider() {
        super();
    }

    /**
     * Persist entity @CreatedBy (@ModifiedBy) property before persist with the value provided by @CurrentUser
     *
     * @param entity
     *            Object entity to persist
     */
    @PrePersist
    public void prePersist(Object entity) {
        Pair<List<Field>, List<Method>> pair = getAllFieldsAndMethods(entity.getClass());
        List<Field> allFields = pair.getLeft();
        for (Field field : allFields) {
            setPropertyIfAnnotated(entity, field, CreatedBy.class);
            if (field.isAnnotationPresent(ModifiedBy.class) && field.getAnnotation(ModifiedBy.class).onCreate()) {
                Object value = resolvePrincipal(field.getType());
                setProperty(entity, field, value);
            }
        }
        for (Method method : pair.getRight()) {
            setPropertyIfGetterAnnotated(entity, allFields, method, CreatedBy.class);
            if (method.isAnnotationPresent(ModifiedBy.class) && method.getAnnotation(ModifiedBy.class).onCreate()) {
                Object value = resolvePrincipal(method.getReturnType());
                Field field = getFieldByMethod(method, allFields);
                setProperty(entity, field, value);
            }
        }
    }

    private void setPropertyIfGetterAnnotated(Object entity, List<Field> allFields, Method method, Class<? extends Annotation> annotationClass) {
        if (method.isAnnotationPresent(annotationClass)) {
            Object value = resolvePrincipal(method.getReturnType());
            Field field = getFieldByMethod(method, allFields);
            setProperty(entity, field, value);
        }
    }

    private void setPropertyIfAnnotated(Object entity, Field field, Class<? extends Annotation> annotationClass) {
        if (field.isAnnotationPresent(annotationClass)) {
            Object value = resolvePrincipal(field.getType());
            setProperty(entity, field, value);
        }
    }

    /**
     * Update entity @ModifiedBy property before update with the value provided by @CurrentUser
     *
     * @param entity
     *            Object entity to update
     */
    @PreUpdate
    public void preUpdate(Object entity) {
        Pair<List<Field>, List<Method>> pair = getAllFieldsAndMethods(entity.getClass());
        List<Field> allFields = pair.getLeft();
        for (Field field : allFields) {
            setPropertyIfAnnotated(entity, field, ModifiedBy.class);
        }
        List<Method> allMethods = pair.getRight();
        for (Method method : allMethods) {
            setPropertyIfGetterAnnotated(entity, allFields, method, ModifiedBy.class);
        }
    }

    private void setProperty(Object entity, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (Exception e) {
            throw new ProviderException(
                    "Failed to write value [" + value + "] to field[" + field + "], entity [" + entity.getClass() + "]: " + e.getLocalizedMessage(),
                    e);
        }
    }

    private Object resolvePrincipal(Class<?> propertyClass) {
        Set<Bean<?>> beans = manager.getBeans(propertyClass, () -> CurrentUser.class);
        if (!beans.isEmpty() && beans.size() == 1) {
            Bean<?> bean = beans.iterator().next();
            return manager.getReference(bean, propertyClass, manager.createCreationalContext(bean));
        }
        throw new IllegalArgumentException("Principal " + (beans.isEmpty() ? "not found" : "not unique"));
    }

}
