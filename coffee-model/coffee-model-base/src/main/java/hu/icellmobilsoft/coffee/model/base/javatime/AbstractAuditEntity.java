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

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import hu.icellmobilsoft.coffee.model.base.AbstractEntity;
import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.CreatedOn;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.ModifiedOn;

/**
 * Base class for audited (X__INSDATE, X__MODDATE, X__INSUSER, X__MODUSER) entities.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@MappedSuperclass
public abstract class AbstractAuditEntity<T extends Serializable> extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @CreatedOn
    @NotNull
    @Column(name = "X__INSDATE")
    private OffsetDateTime creationDate;

    @ModifiedOn
    @Column(name = "X__MODDATE")
    private OffsetDateTime modificationDate;

    @CreatedBy
    @NotNull
    @Column(name = "X__INSUSER", length = 30)
    private T creatorUser;

    @ModifiedBy
    @Column(name = "X__MODUSER", length = 30)
    private T modifierUser;

    /**
     * <p>Getter for the field <code>creationDate</code>.</p>
     */
    public OffsetDateTime getCreationDate() {
        return this.creationDate;
    }

    /**
     * <p>Setter for the field <code>creationDate</code>.</p>
     */
    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * <p>Getter for the field <code>modificationDate</code>.</p>
     */
    public OffsetDateTime getModificationDate() {
        return this.modificationDate;
    }

    /**
     * <p>Setter for the field <code>modificationDate</code>.</p>
     */
    public void setModificationDate(OffsetDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    /**
     * <p>Getter for the field <code>creatorUser</code>.</p>
     */
    public T getCreatorUser() {
        return this.creatorUser;
    }

    /**
     * <p>Setter for the field <code>creatorUser</code>.</p>
     */
    public void setCreatorUser(T creatorUser) {
        this.creatorUser = creatorUser;
    }

    /**
     * <p>Getter for the field <code>modifierUser</code>.</p>
     */
    public T getModifierUser() {
        return this.modifierUser;
    }

    /**
     * <p>Setter for the field <code>modifierUser</code>.</p>
     */
    public void setModifierUser(T modifierUser) {
        this.modifierUser = modifierUser;
    }
}
