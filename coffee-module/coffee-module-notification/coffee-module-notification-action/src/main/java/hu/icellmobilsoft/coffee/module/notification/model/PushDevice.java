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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.module.notification.model.enums.DeviceType;

/**
 * push device table entity
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
@Vetoed
@Entity
@Table(name = "push_device")
public class PushDevice extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "push_id", nullable = false, length = 30)
    @Size(max = 30)
    private String pushId;

    @Column(name = "device_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(name = "device_id", nullable = false, length = 255)
    @Size(max = 255)
    private String deviceId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_time")
    private Date sentTime;

    @Column(name = "success")
    private boolean success;

    @Column(name = "result", length = 2048)
    @Size(max = 2048)
    private String result;

    /**
     * <p>Getter for the field <code>pushId</code>.</p>
     */
    public String getPushId() {
        return pushId;
    }

    /**
     * <p>Setter for the field <code>pushId</code>.</p>
     */
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    /**
     * <p>Getter for the field <code>deviceId</code>.</p>
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * <p>Setter for the field <code>deviceId</code>.</p>
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * <p>Getter for the field <code>deviceType</code>.</p>
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * <p>Setter for the field <code>deviceType</code>.</p>
     */
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
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
