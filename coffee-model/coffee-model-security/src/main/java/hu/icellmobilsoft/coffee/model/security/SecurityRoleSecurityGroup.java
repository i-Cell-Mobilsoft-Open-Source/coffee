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
 * SecurityRoleSecurityGroup class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Entity
@Table(name = "security_role_security_group")
public class SecurityRoleSecurityGroup extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public SecurityRoleSecurityGroup() {
        super();
    }

    /**
     * security role FK
     */
    @Column(name = "security_role_id", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String roleId;

    /**
     * security group FK
     */
    @Column(name = "security_group_id", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String groupId;

    /**
     * Getter for the field {@code roleId}.
     * 
     * @return roleId
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Setter for the field {@code roleId}.
     * 
     * @param roleId
     *            roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * Getter for the field {@code groupId}.
     * 
     * @return groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Setter for the field {@code groupId}.
     * 
     * @param groupId
     *            groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
