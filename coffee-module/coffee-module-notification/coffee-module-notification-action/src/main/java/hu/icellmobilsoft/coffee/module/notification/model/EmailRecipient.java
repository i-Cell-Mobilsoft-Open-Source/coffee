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

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.module.notification.model.enums.RecipientType;

/**
 * email recipient table entity
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "email_recipient")
public class EmailRecipient extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    @Column(name = "recipient_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipientType recipientType;

    /**
     * <p>Getter for the field <code>email</code>.</p>
     */
    public Email getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     */
    public void setEmail(Email email) {
        this.email = email;
    }

    /**
     * <p>Getter for the field <code>recipient</code>.</p>
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * <p>Setter for the field <code>recipient</code>.</p>
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    /**
     * <p>Getter for the field <code>recipientType</code>.</p>
     */
    public RecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * <p>Setter for the field <code>recipientType</code>.</p>
     */
    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }
}
