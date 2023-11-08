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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * email table entity
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Entity
@Table(name = "email")
public class Email extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public Email() {
        super();
    }

    /**
     * email sender address
     */
    @Column(name = "email_from", length = 255)
    @Size(max = 255)
    private String from;

    /**
     * email subject
     */
    @Column(name = "subject", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String subject;

    /**
     * email body
     */
    @Column(name = "body")
    private String body;

    /**
     * email sent time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_time")
    private Date sentTime;

    /**
     * email send was successful
     */
    @Column(name = "success")
    private boolean success;

    /**
     * message id or exception message
     */
    @Column(name = "result", length = 2048)
    @Size(max = 2048)
    private String result;

    /**
     * Getter for the field {@code from}.
     *
     * @return {@code from}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Setter for the field {@code from}.
     *
     * @param from
     *            from to set
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Getter for the field {@code subject}.
     *
     * @return {@code subject}
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Setter for the field {@code subject}.
     *
     * @param subject
     *            subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Getter for the field {@code body}.
     *
     * @return {@code body}
     */
    public String getBody() {
        return body;
    }

    /**
     * Setter for the field {@code body}.
     *
     * @param body
     *            body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Getter for the field {@code sentTime}.
     *
     * @return {@code sentTime}
     */
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * Setter for the field {@code sentTime}.
     *
     * @param sentTime
     *            sentTime to set
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * Getter for the field {@code success}.
     *
     * @return {@code success}
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter for the field {@code success}.
     *
     * @param success
     *            success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter for the field {@code result}.
     *
     * @return {@code result}
     */
    public String getResult() {
        return result;
    }

    /**
     * Setter for the field {@code result}.
     *
     * @param result
     *            result to set
     */
    public void setResult(String result) {
        this.result = result;
    }
}
