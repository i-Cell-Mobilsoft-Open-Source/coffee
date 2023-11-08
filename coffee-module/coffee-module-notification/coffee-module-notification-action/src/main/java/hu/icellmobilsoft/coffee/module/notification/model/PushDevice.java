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
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;

import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;
import hu.icellmobilsoft.coffee.module.notification.model.enums.DeviceType;

/**
 * push device table entity
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
@Entity
@Table(name = "push_device")
public class PushDevice extends AbstractIdentifiedAuditEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public PushDevice() {
        super();
    }

    /**
     * Push message id
     */
    @Column(name = "push_id", nullable = false, length = 30)
    @Size(max = 30)
    private String pushId;

    /**
     * Push device type (e.g.: Android, IOS)
     */
    @Column(name = "device_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    /**
     * Push device id
     */
    @Column(name = "device_id", nullable = false, length = 255)
    @Size(max = 255)
    private String deviceId;

    /**
     * Push message sent time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_time")
    private Date sentTime;

    /**
     * Push send was successful
     */
    @Column(name = "success")
    private boolean success;

    /**
     * Push result
     */
    @Column(name = "result", length = 2048)
    @Size(max = 2048)
    private String result;

    /**
     * Getter for the field {@code pushId}.
     *
     * @return {@code pushId}
     */
    public String getPushId() {
        return pushId;
    }

    /**
     * Setter for the field {@code pushId}.
     *
     * @param pushId
     *            pushId to set
     */
    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    /**
     * Getter for the field {@code deviceId}.
     *
     * @return {@code deviceId}
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Setter for the field {@code deviceId}.
     *
     * @param deviceId
     *            deviceId to set
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Getter for the field {@code deviceType}.
     *
     * @return {@code deviceType}
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * Setter for the field {@code deviceType}.
     *
     * @param deviceType
     *            deviceType to set
     */
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
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
