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
package hu.icellmobilsoft.coffee.model.base;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Blob;
import java.sql.Clob;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import hu.icellmobilsoft.coffee.model.base.generator.CoffeeIdGenerator;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Base class for all entities.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class AbstractEntity implements IIdentifiedEntity<String>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identity field of the entity
     */
    @Id
    @CoffeeIdGenerator
    @Column(name = "X__ID", length = 30)
    private String id;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractEntity() {
        super();
    }

    /**
     * Getter for the field {@code id}.
     *
     * @return id
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Setter for the field {@code id}.
     *
     * @param id
     *            id
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * toString.
     */
    @Override
    public String toString() {
        ToStringBuilder s = new ToStringBuilder(this);
        for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(this)) {

            String name = property.getName();
            Class<?> propertyType = property.getPropertyType();

            if (ClassUtils.isAssignable(propertyType, AbstractEntity.class)) {
                // Do not traverse dependency relationships, do not lazy-load
                s.append(name, propertyType.getSimpleName());
            } else if (propertyType == byte[].class || propertyType == Blob.class || propertyType == Clob.class || propertyType == InputStream.class
                    || propertyType == OutputStream.class || propertyType == Reader.class || propertyType == Writer.class) {
                // Do not handle large amounts of data, do not hassle with streams.
                s.append(name, propertyType.getSimpleName());
            } else {
                try {
                    s.append(name, property.getReadMethod().invoke(this));
                } catch (Exception e) {
                    Logger.getLogger(getClass()).warn("Error in toString for property [{0}]: [{1}]", name, e.getLocalizedMessage());
                }
            }
        }
        return s.toString();
    }

}
