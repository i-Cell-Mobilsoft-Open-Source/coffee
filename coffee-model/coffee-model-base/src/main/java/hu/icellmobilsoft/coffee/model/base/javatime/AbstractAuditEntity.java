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
package hu.icellmobilsoft.coffee.model.base.javatime;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.icellmobilsoft.coffee.model.base.AbstractEntity;
import hu.icellmobilsoft.coffee.model.base.IAuditEntity;
import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;
import hu.icellmobilsoft.coffee.model.base.audit.AuditProvider;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.CreatedOn;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.ModifiedOn;
import hu.icellmobilsoft.coffee.model.base.javatime.listener.TimestampsProvider;
import jakarta.enterprise.inject.Vetoed;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

/**
 * Base class for audited (X__INSDATE, X__MODDATE, X__INSUSER, X__MODUSER) entities.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@MappedSuperclass
@EntityListeners({ TimestampsProvider.class, AuditProvider.class })
public abstract class AbstractAuditEntity<T extends Serializable> extends AbstractEntity implements IAuditEntity<OffsetDateTime, T> {

    private static final long serialVersionUID = 1L;

    /**
     * Creation date of the entity
     */
    @CreatedOn
    @NotNull
    @Column(name = "X__INSDATE", updatable = false)
    private OffsetDateTime creationDate;

    /**
     * Last modification date of the entity
     */
    @ModifiedOn
    @Column(name = "X__MODDATE")
    private OffsetDateTime modificationDate;

    /**
     * Creator user of the entity
     */
    @CreatedBy
    @NotNull
    @Column(name = "X__INSUSER", length = 30, updatable = false)
    private T creatorUser;

    /**
     * The last modifier user of the entity
     */
    @ModifiedBy
    @Column(name = "X__MODUSER", length = 30)
    private T modifierUser;

    /**
     * Getter for the field {@code creationDate}.
     *
     * @return creationDate
     */
    public OffsetDateTime getCreationDate() {
        return this.creationDate;
    }

    /**
     * Setter for the field {@code creationDate}.
     *
     * @param creationDate
     *            creationDate
     */
    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Getter for the field {@code modificationDate}.
     *
     * @return modificationDate
     */
    public OffsetDateTime getModificationDate() {
        return this.modificationDate;
    }

    /**
     * Setter for the field {@code modificationDate}.
     *
     * @param modificationDate
     *            modificationDate
     */
    public void setModificationDate(OffsetDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * Getter for the field {@code creatorUser}.
     *
     * @return creatorUser
     */
    public T getCreatorUser() {
        return this.creatorUser;
    }

    /**
     * Setter for the field {@code creatorUser}.
     *
     * @param creatorUser
     *            creatorUser
     */
    public void setCreatorUser(T creatorUser) {
        this.creatorUser = creatorUser;
    }

    /**
     * Getter for the field {@code modifierUser}.
     *
     * @return modifierUser
     */
    public T getModifierUser() {
        return this.modifierUser;
    }

    /**
     * Setter for the field {@code modifierUser}.
     *
     * @param modifierUser
     *            modifierUser
     */
    public void setModifierUser(T modifierUser) {
        this.modifierUser = modifierUser;
    }
}
