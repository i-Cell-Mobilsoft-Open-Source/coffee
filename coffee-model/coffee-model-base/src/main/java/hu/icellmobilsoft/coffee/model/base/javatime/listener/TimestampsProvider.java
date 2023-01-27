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
package hu.icellmobilsoft.coffee.model.base.javatime.listener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import hu.icellmobilsoft.coffee.model.base.AbstractProvider;
import hu.icellmobilsoft.coffee.model.base.exception.ProviderException;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.CreatedOn;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.ModifiedOn;
import jakarta.enterprise.context.Dependent;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Set java 8 timestamps on marked properties.
 *
 *
 * @author mark.petrenyi
 * @author zsolt.vasi
 * @since 1.0.0
 */
@Dependent
public class TimestampsProvider extends AbstractProvider {

    /**
     * Persist entity @CreatedOn property before persist with the System current time value
     *
     * @param entity
     *            Object entity to persist
     */
    @PrePersist
    public void prePersist(Object entity) {
        updateTimestamps(entity, CreatedOn.class);
    }

    /**
     * Persist entity @ModifiedOn property before persist with the System current time value
     *
     *
     * @param entity
     *            Object entity to persist
     */
    @PreUpdate
    public void preUpdate(Object entity) {
        updateTimestamps(entity, ModifiedOn.class);
    }

    private void updateTimestamps(Object entity, Class<? extends Annotation> annotationClass) {
        long sysTime = System.currentTimeMillis();

        Pair<List<Field>, List<Method>> pair = getAllFieldsAndMethods(entity.getClass());
        List<Field> allFields = pair.getLeft();
        for (Field field : allFields) {
            if (field.isAnnotationPresent(annotationClass)) {
                setValue(entity, field.getType(), sysTime, field);
            }
        }
        for (Method method : pair.getRight()) {
            if (method.isAnnotationPresent(annotationClass)) {
                Field field = getFieldByMethod(method, allFields);
                setValue(entity, field.getType(), sysTime, field);
            }
        }
    }

    private void setValue(Object entity, Class<?> fieldClass, long systime, Field field) {
        Object object = null;
        try {
            if (isCalendarClass(fieldClass)) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(systime);
                object = cal;
            } else if (isDateClass(fieldClass)) {
                object = fieldClass.getConstructor(Long.TYPE).newInstance(systime);
            } else if (isOffsetDateTimeClass(fieldClass)) {
                object = OffsetDateTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault());
            } else if (isOffsetTimeClass(fieldClass)) {
                object = OffsetTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault());
            } else if (isLocalDateTimeClass(fieldClass)) {
                object = LocalDateTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault());
            } else if (isLocalDateClass(fieldClass)) {
                object = LocalDateTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault()).toLocalDate();
            } else if (isInstantClass(fieldClass)) {
                object = Instant.ofEpochMilli(systime);
            } else {
                throw new IllegalArgumentException("Annotated fieldClass is not a date class: " + fieldClass);
            }
            field.setAccessible(true);
            field.set(entity, object);
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException exception) {
            throw new ProviderException("Failed to write value [" + object + "] to field [" + field + "], fieldClass [" + fieldClass + "], entity ["
                    + entity.getClass() + "]: " + exception.getLocalizedMessage(), exception);
        }
    }

    private boolean isCalendarClass(Class<?> field) {
        return Calendar.class.isAssignableFrom(field);
    }

    private boolean isDateClass(Class<?> field) {
        return Date.class.isAssignableFrom(field);
    }

    private boolean isOffsetDateTimeClass(Class<?> field) {
        return OffsetDateTime.class.isAssignableFrom(field);
    }

    private boolean isOffsetTimeClass(Class<?> field) {
        return OffsetTime.class.isAssignableFrom(field);
    }

    private boolean isLocalDateTimeClass(Class<?> field) {
        return LocalDateTime.class.isAssignableFrom(field);
    }

    private boolean isLocalDateClass(Class<?> field) {
        return LocalDate.class.isAssignableFrom(field);
    }

    private boolean isInstantClass(Class<?> field) {
        return Instant.class.isAssignableFrom(field);
    }

}
