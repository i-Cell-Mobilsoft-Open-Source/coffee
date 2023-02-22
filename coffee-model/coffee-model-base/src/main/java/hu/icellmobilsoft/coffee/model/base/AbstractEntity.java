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
import java.io.Serializable;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import jakarta.enterprise.inject.Vetoed;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

/**
 * Base class for all entities.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@MappedSuperclass
public abstract class AbstractEntity implements IVersionable, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The version of the entity for optimistic lock checking
     */
    @Column(name = "X__VERSION", precision = 20, scale = 0)
    @NotNull
    @Version
    private long version;

    /**
     * Transient interval version
     */
    @Transient
    private Long internalVersion;

    /**
     * Getter for the field {@code version}.
     * 
     * @return version
     */
    public long getVersion() {
        return version;
    }

    /**
     * Setter for the field {@code version}.
     * 
     * @param version
     *            version
     */
    public void setVersion(long version) {
        if (internalVersion == null || this.version != version) {
            internalVersion = this.version;
        }
        this.version = version;
    }

    /**
     * rollbackVersion.
     */
    public void rollbackVersion() {
        if (internalVersion != null) {
            version = internalVersion;
            internalVersion = null;
        }
    }

    /**
     * updateVersion.
     */
    public void updateVersion() {
        internalVersion = version;
    }

    /**
     * toString.
     */
    public String toString() {
        ToStringBuilder s = new ToStringBuilder(this);
        for (PropertyDescriptor property : PropertyUtils.getPropertyDescriptors(this)) {
            if (ClassUtils.isAssignable(property.getPropertyType(), AbstractEntity.class)) {
                // dependency kapcsolatokon ne menjen vegig, ne lazy-zzon
                s.append(property.getName(), property.getPropertyType());
            } else if (property.getPropertyType() == byte[].class) {
                s.append(property.getName(), property.getPropertyType().getSimpleName());
            } else {
                try {
                    s.append(property.getName(), property.getReadMethod().invoke(this));
                } catch (Exception e) {
                    Logger.getLogger(getClass()).warn("Error in toString for property [{0}]: [{1}]", property.getName(), e.getLocalizedMessage());
                }
            }
        }
        return s.toString();
        // return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
