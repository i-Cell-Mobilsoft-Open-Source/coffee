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
 * SecurityUserSecurityRole class.
 *
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "security_user_security_role")
public class SecurityUserSecurityRole extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * security role FK
     */
    @Column(name = "security_role_id", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String roleId;

    /**
     * security user FK
     */
    @Column(name = "security_user_id", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String securityUserId;

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
     * Getter for the field {@code securityUserId}.
     * 
     * @return securityUserId
     */
    public String getSecurityUserId() {
        return securityUserId;
    }

    /**
     * Setter for the field {@code securityUserId}.
     * 
     * @param securityUserId
     *            securityUserId
     */
    public void setSecurityUserId(String securityUserId) {
        this.securityUserId = securityUserId;
    }
}
