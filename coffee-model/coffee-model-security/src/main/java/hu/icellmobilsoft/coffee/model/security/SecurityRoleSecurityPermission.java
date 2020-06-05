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

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * <p>SecurityRoleSecurityPermission class.</p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "security_role_security_permission")
public class SecurityRoleSecurityPermission extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "security_role_id", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String roleId;

    @Column(name = "security_permission_id", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String permissionId;

    /**
     * <p>Getter for the field <code>roleId</code>.</p>
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * <p>Setter for the field <code>roleId</code>.</p>
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * <p>Getter for the field <code>permissionId</code>.</p>
     */
    public String getPermissionId() {
        return permissionId;
    }

    /**
     * <p>Setter for the field <code>permissionId</code>.</p>
     */
    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }
}
