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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * push table entity
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "push")
public class Push extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Push message subject
     */
    @Column(name = "subject", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String subject;

    /**
     * Push message body
     */
    @Column(name = "body")
    private String body;

    /**
     * Push message external id
     */
    @Column(name = "external_id", nullable = true, length = 30)
    @Size(max = 30)
    private String externalId;

    /**
     * Transient map storing push devices by device id
     */
    @Transient
    private Map<String, PushDevice> pushDevices = new HashMap<>();

    /**
     * Push message payload
     */
    @Column(name = "payload", nullable = true, length = 1024)
    @Size(max = 1024)
    private String payload;

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
     * Getter for the field {@code externalId}.
     *
     * @return {@code externalId}
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Setter for the field {@code externalId}.
     *
     * @param externalId
     *            externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Getter for the field {@code pushDevices}.
     *
     * @return {@code pushDevices}
     */
    public Map<String, PushDevice> getPushDevices() {
        return pushDevices;
    }

    /**
     * Setter for the field {@code pushDevices}.
     *
     * @param pushDevices
     *            pushDevices to set
     */
    public void setPushDevices(Map<String, PushDevice> pushDevices) {
        this.pushDevices = pushDevices;
    }

    /**
     * Getter for the field {@code payload}.
     *
     * @return {@code payload}
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Setter for the field {@code payload}.
     *
     * @param payload
     *            payload to set
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
