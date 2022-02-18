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

import java.util.Date;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.deltaspike.data.api.audit.CreatedOn;
import org.apache.deltaspike.data.api.audit.ModifiedOn;

import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;

/**
 * Base class for audited entities.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@MappedSuperclass
public abstract class AbstractAuditEntity<T> extends AbstractEntity implements IAuditEntity<Date> {

    private static final long serialVersionUID = 1L;

    /**
     * Creation date of the entity
     */
    @CreatedOn
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "X__INSDATE", updatable = false)
    private Date creationDate;

    /**
     * Last modification date of the entity
     */
    @ModifiedOn
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "X__MODDATE")
    private Date modificationDate;

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
    public Date getCreationDate() {
        return this.creationDate;
    }

    /**
     * Setter for the field {@code creationDate}.
     *
     * @param creationDate
     *            creationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Getter for the field {@code modificationDate}.
     *
     * @return modificationDate
     */
    public Date getModificationDate() {
        return this.modificationDate;
    }

    /**
     * Setter for the field {@code modificationDate}.
     *
     * @param modificationDate
     *            modificationDate
     */
    public void setModificationDate(Date modificationDate) {
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
