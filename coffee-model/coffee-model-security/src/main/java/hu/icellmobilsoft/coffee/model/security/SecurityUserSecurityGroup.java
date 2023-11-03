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
 * Join table for {@link SecurityUser} and {@link SecurityGroup}.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Entity
@Table(name = "security_user_security_group")
public class SecurityUserSecurityGroup extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public SecurityUserSecurityGroup() {
        super();
    }

    /**
     * security group FK
     */
    @Column(name = "security_group_id", nullable = false, length = 30)
    @Size(max = 30)
    @NotNull
    private String groupId;

    /**
     * security user FK
     */
    @Column(name = "security_user_id", nullable = false, length = 30)
    @Size(max = 30)
    @NotNull
    private String userId;

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

    /**
     * Getter for the field {@code userId}.
     * 
     * @return userId
     */
    public String getSecurityUserId() {
        return userId;
    }

    /**
     * Setter for the field {@code userId}.
     * 
     * @param userId
     *            userId
     */
    public void setSecurityUserId(String userId) {
        this.userId = userId;
    }
}
