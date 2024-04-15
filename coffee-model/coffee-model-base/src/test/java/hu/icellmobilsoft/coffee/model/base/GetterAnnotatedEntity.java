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

import java.time.OffsetDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

import hu.icellmobilsoft.coffee.model.base.annotation.CreatedBy;
import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.CreatedOn;
import hu.icellmobilsoft.coffee.model.base.javatime.annotation.ModifiedOn;

/**
 * Getter annotated test entity
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
@Entity
@Table(name = "dummy")
public class GetterAnnotatedEntity extends AbstractIdentifiedEntity {

    /**
     * Creation date of the entity
     */
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "X__INSDATE", updatable = false)
    private OffsetDateTime creationDate;

    /**
     * Last modification date of the entity
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "X__MODDATE")
    private OffsetDateTime modificationDate;

    /**
     * Creator user of the entity
     */
    @NotNull
    @Column(name = "X__INSUSER", length = 30, updatable = false)
    private String creatorUser;

    /**
     * The last modifier user of the entity
     */
    @Column(name = "X__MODUSER", length = 30)
    private String modifierUser;

    /**
     * Getter for the field {@code creationDate}.
     *
     * @return creationDate
     */
    @CreatedOn
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
    @ModifiedOn
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
    @CreatedBy
    public String getCreatorUser() {
        return this.creatorUser;
    }

    /**
     * Setter for the field {@code creatorUser}.
     *
     * @param creatorUser
     *            creatorUser
     */
    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    /**
     * Getter for the field {@code modifierUser}.
     *
     * @return modifierUser
     */
    @ModifiedBy
    public String getModifierUser() {
        return this.modifierUser;
    }

    /**
     * Setter for the field {@code modifierUser}.
     *
     * @param modifierUser
     *            modifierUser
     */
    public void setModifierUser(String modifierUser) {
        this.modifierUser = modifierUser;
    }

}
