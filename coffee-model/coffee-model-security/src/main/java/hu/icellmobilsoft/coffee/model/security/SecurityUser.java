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

import java.util.Date;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.model.security.enums.UserStatus;

/**
 * <p>SecurityUser class.</p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "security_user")
public class SecurityUser extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "login", nullable = false, unique = true, length = 100)
    @Size(max = 100)
    @NotNull
    private String login;

    @Column(name = "password", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String password;

    @Column(name = "user_status_enum_id", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus status;

    @Column(name = "salt", length = 512)
    @Size(max = 512)
    private String salt;

    @Column(name = "last_login_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    /**
     * <p>Getter for the field <code>login</code>.</p>
     */
    public String getLogin() {
        return login;
    }

    /**
     * <p>Setter for the field <code>login</code>.</p>
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * <p>Getter for the field <code>password</code>.</p>
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Setter for the field <code>password</code>.</p>
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * <p>Getter for the field <code>salt</code>.</p>
     */
    public String getSalt() {
        return salt;
    }

    /**
     * <p>Setter for the field <code>salt</code>.</p>
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * <p>Getter for the field <code>lastLoginDate</code>.</p>
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * <p>Setter for the field <code>lastLoginDate</code>.</p>
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
