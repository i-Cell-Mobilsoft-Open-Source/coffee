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
package hu.icellmobilsoft.coffee.module.notification.model;

import java.util.Date;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * email table entity
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "email")
public class Email extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "email_from", length = 255)
    @Size(max = 255)
    private String from;

    @Column(name = "subject", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String subject;

    @Column(name = "body")
    private String body;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_time")
    private Date sentTime;

    @Column(name = "success")
    private boolean success;

    @Column(name = "result", length = 2048)
    @Size(max = 2048)
    private String result;

    /**
     * <p>Getter for the field <code>from</code>.</p>
     */
    public String getFrom() {
        return from;
    }

    /**
     * <p>Setter for the field <code>from</code>.</p>
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * <p>Getter for the field <code>subject</code>.</p>
     */
    public String getSubject() {
        return subject;
    }

    /**
     * <p>Setter for the field <code>subject</code>.</p>
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * <p>Getter for the field <code>body</code>.</p>
     */
    public String getBody() {
        return body;
    }

    /**
     * <p>Setter for the field <code>body</code>.</p>
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * <p>Getter for the field <code>sentTime</code>.</p>
     */
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * <p>Setter for the field <code>sentTime</code>.</p>
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * <p>isSuccess.</p>
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * <p>Setter for the field <code>success</code>.</p>
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * <p>Getter for the field <code>result</code>.</p>
     */
    public String getResult() {
        return result;
    }

    /**
     * <p>Setter for the field <code>result</code>.</p>
     */
    public void setResult(String result) {
        this.result = result;
    }
}
