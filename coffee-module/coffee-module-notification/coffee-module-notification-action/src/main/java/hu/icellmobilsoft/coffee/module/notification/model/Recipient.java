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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * recipient table entity
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "recipient")
public class Recipient extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Recipient
     */
    @Column(name = "recipient", nullable = false)
    @Size(max = 255)
    @NotNull
    private String recipient;

    /**
     * Getter for the field {@code recipient}.
     *
     * @return {@code recipient}
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Setter for the field {@code recipient}.
     *
     * @param recipient
     *            recipient to set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
