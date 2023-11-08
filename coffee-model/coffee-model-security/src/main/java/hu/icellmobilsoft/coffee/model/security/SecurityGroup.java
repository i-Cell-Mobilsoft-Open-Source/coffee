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
package hu.icellmobilsoft.coffee.model.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * SecurityGroup class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Entity
@Table(name = "security_group")
public class SecurityGroup extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public SecurityGroup() {
        super();
    }

    /**
     * security group description
     */
    @Column(name = "description", length = 255)
    @Size(max = 255)
    private String description;

    /**
     * security group can be deleted
     */
    @Column(name = "deletable", nullable = false)
    private boolean deletable;

    /**
     * security group name
     */
    @Column(name = "name", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String name;

    /**
     * Getter for the field {@code description}.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the field {@code description}.
     * 
     * @param description
     *            description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the field {@code deletable}.
     * 
     * @return deletable
     */
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * Setter for the field {@code deletable}.
     * 
     * @param deletable
     *            deletable
     */
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * Getter for the field {@code name}.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the field {@code name}.
     * 
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }
}
