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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.module.notification.model.enums.RecipientType;

/**
 * email recipient table entity
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Entity
@Table(name = "email_recipient")
public class EmailRecipient extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public EmailRecipient() {
        super();
    }

    /**
     * Email FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;

    /**
     * Recipient FK
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private Recipient recipient;

    /**
     * Recipient type (TO, CC, BCC)
     */
    @Column(name = "recipient_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipientType recipientType;

    /**
     * Getter for the field {@code email}.
     *
     * @return {@code email}
     */
    public Email getEmail() {
        return email;
    }

    /**
     * Setter for the field {@code email}.
     *
     * @param email
     *            email to set
     */
    public void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Getter for the field {@code recipient}.
     *
     * @return {@code recipient}
     */
    public Recipient getRecipient() {
        return recipient;
    }

    /**
     * Setter for the field {@code recipient}.
     *
     * @param recipient
     *            recipient to set
     */
    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    /**
     * Getter for the field {@code recipientType}.
     *
     * @return {@code recipientType}
     */
    public RecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * Setter for the field {@code recipientType}.
     *
     * @param recipientType
     *            recipientType to set
     */
    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }
}
