/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

/**
 * Base class for versionable audit entities.
 *
 * @param <T>
 *            The type of the creator and modifier user
 *
 * @author attila.kiss
 * @since 4.0.0
 */
@MappedSuperclass
public abstract class AbstractVersionableAuditEntity<T extends Serializable> extends AbstractAuditEntity<T> implements IVersionable {

    private static final long serialVersionUID = 1L;

    /**
     * The version of the entity for optimistic lock checking
     */
    @Column(name = "X__VERSION")
    @NotNull
    @Version
    private long version;

    /**
     * Transient interval version
     */
    @Transient
    private Long internalVersion;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractVersionableAuditEntity() {
        super();
    }

    /**
     * Getter for the field {@code version}.
     *
     * @return version
     */
    @Override
    public long getVersion() {
        return version;
    }

    /**
     * Setter for the field {@code version}.
     *
     * @param version
     *            version
     */
    @Override
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

}
