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
 * SecurityUser class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "security_user")
public class SecurityUser extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * user login name
     */
    @Column(name = "login", nullable = false, unique = true, length = 100)
    @Size(max = 100)
    @NotNull
    private String login;

    /**
     * user password
     */
    @Column(name = "password", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String password;

    /**
     * user status
     */
    @Column(name = "user_status_enum_id", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus status;

    /**
     * the user's password salt
     */
    @Column(name = "salt", length = 512)
    @Size(max = 512)
    private String salt;

    /**
     * user's last login date
     */
    @Column(name = "last_login_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    /**
     * Getter for the field {@code login}.
     * 
     * @return login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter for the field {@code login}.
     * 
     * @param login
     *            login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Getter for the field {@code password}.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for the field {@code password}.
     * 
     * @param password
     *            password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the field {@code status}.
     * 
     * @return status
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Setter for the field {@code status}.
     * 
     * @param status
     *            status
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Getter for the field {@code salt}.
     * 
     * @return salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Setter for the field {@code salt}.
     * 
     * @param salt
     *            salt
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Getter for the field {@code lastLoginDate}.
     * 
     * @return lastLoginDate
     */
    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Setter for the field {@code lastLoginDate}.
     * 
     * @param lastLoginDate
     *            lastLoginDate
     */
    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
