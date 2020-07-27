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

    @Column(name = "subject", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String subject;

    @Column(name = "body")
    private String body;
    
    @Column(name = "external_id", nullable = true, length = 30)
    @Size(max = 30)
    private String externalId;

    @Transient
    private Map<String, PushDevice> pushDevices = new HashMap<String, PushDevice>();

    @Column(name = "payload")
    private String payload;

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
     * <p>Getter for the field <code>externalId</code>.</p>
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * <p>Setter for the field <code>externalId</code>.</p>
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * <p>Getter for the field <code>pushDevices</code>.</p>
     */
    public Map<String, PushDevice> getPushDevices() {
        return pushDevices;
    }

    /**
     * <p>Setter for the field <code>pushDevices</code>.</p>
     */
    public void setPushDevices(Map<String, PushDevice> pushDevices) {
        this.pushDevices = pushDevices;
    }

    /**
     * <p>Getter for the field <code>payload</code>.</p>
     */
    public String getPayload() {
        return payload;
    }

    /**
     * <p>Setter for the field <code>payload</code>.</p>
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
