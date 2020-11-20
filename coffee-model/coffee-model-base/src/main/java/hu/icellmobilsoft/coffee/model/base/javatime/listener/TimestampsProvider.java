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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.deltaspike.data.impl.audit.AuditPropertyException;
import org.apache.deltaspike.data.impl.audit.PrePersistAuditListener;
import org.apache.deltaspike.data.impl.audit.PreUpdateAuditListener;
import org.apache.deltaspike.data.impl.property.Property;
import org.apache.deltaspike.data.impl.property.query.AnnotatedPropertyCriteria;
import org.apache.deltaspike.data.impl.property.query.PropertyQueries;

import hu.icellmobilsoft.coffee.model.base.javatime.annotation.CreatedOn;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.ModifiedOn;

/**
 * Set java 8 timestamps on marked properties.
 *
 * TimestampsProvider nem tehető alternative-ra illetve csak Calendar/Date típusokat tud kezelni:<br>
 * https://issues.apache.org/jira/browse/DELTASPIKE-1229
 *
 * @author mark.petrenyi
 * @see org.apache.deltaspike.data.impl.audit.TimestampsProvider
 * @since 1.0.0
 */
public class TimestampsProvider implements PrePersistAuditListener, PreUpdateAuditListener {

    /** {@inheritDoc} */
    @Override
    public void prePersist(Object entity) {
        updateTimestamps(entity, CreatedOn.class);
    }

    /** {@inheritDoc} */
    @Override
    public void preUpdate(Object entity) {
        updateTimestamps(entity, ModifiedOn.class);
    }

    private void updateTimestamps(Object entity, Class<? extends Annotation> annotationClass) {
        long sysTime = System.currentTimeMillis();

        List<Property<Object>> writableResultList = getWritableProperties(entity, annotationClass);
        for (Property<Object> property : writableResultList) {
            setProperty(entity, property, sysTime);
        }
    }

    private List<Property<Object>> getWritableProperties(Object entity, Class<? extends Annotation> annotationClass) {
        return PropertyQueries.createQuery(entity.getClass())//
                .addCriteria(new AnnotatedPropertyCriteria(annotationClass))//
                .getWritableResultList();
    }

    private void setProperty(Object entity, Property<Object> property, long sysTime) {
        Object value = sysTime;
        try {
            value = now(property.getJavaClass(), sysTime);
            property.setValue(entity, value);
        } catch (Exception e) {
            throw new AuditPropertyException("Failed to write value [" + value + "] to property [" + property.getName() + "] on entity ["
                    + entity.getClass() + "]: " + e.getLocalizedMessage(), e);
        }
    }

    private Object now(Class<?> field, long systime) throws Exception {
        if (isCalendarClass(field)) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(systime);
            return cal;
        } else if (isDateClass(field)) {
            return field.getConstructor(Long.TYPE).newInstance(systime);
        } else if (isOffsetDateTimeClass(field)) {
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault());
        } else if (isOffsetTimeClass(field)) {
            return OffsetTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault());
        } else if (isLocalDateTimeClass(field)) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault());
        } else if (isLocalDateClass(field)) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(systime), ZoneId.systemDefault()).toLocalDate();
        } else if (isInstantClass(field)) {
            return Instant.ofEpochMilli(systime);
        }
        throw new IllegalArgumentException("Annotated field is not a date class: " + field);
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
