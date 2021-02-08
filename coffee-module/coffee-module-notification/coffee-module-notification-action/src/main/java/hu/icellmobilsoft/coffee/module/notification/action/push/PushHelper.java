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
package hu.icellmobilsoft.coffee.module.notification.action.push;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import hu.icellmobilsoft.coffee.dto.common.common.KeyValueBasicType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.notification.notification.DeviceType;
import hu.icellmobilsoft.coffee.module.notification.model.Push;
import hu.icellmobilsoft.coffee.module.notification.model.PushDevice;
import hu.icellmobilsoft.coffee.module.notification.service.PushDeviceService;
import hu.icellmobilsoft.coffee.module.notification.service.PushService;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.enums.EnumUtil;

/**
 * CDI helper class for push handling
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
@Dependent
public class PushHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private PushService pushService;

    @Inject
    private PushDeviceService pushDeviceService;

    /**
     * Inserts {@link Push} with given parameters and corresponding {@link PushDevice} list into DB.
     *
     * @param subject
     *            {@code Push} subject
     * @param body
     *            {@code Push} body
     * @param externalId
     *            {@code Push} external id
     * @param deviceList
     *            corresponding list of {@code PushDevice}s to insert into DB
     * @param payloads
     *            {@code Push} payloads
     * @return saved {@code Push}
     * @throws BaseException
     *             if exception occurs during entity merge
     * @see PushService#save
     * @see PushDeviceService#save
     */
    @Transactional
    public Push insertToDb(String subject, String body, String externalId, List<DeviceType> deviceList, List<KeyValueBasicType> payloads)
            throws BaseException {
        if (StringUtils.isBlank(subject) || StringUtils.isBlank(body)) {
            return null;
        }
        Push savedPush = savePush(subject, body, externalId, JsonUtil.toJson(payloads));
        saveDevices(savedPush, deviceList);
        return savedPush;
    }

    private Push savePush(String subject, String body, String externalId, String payloads) throws BaseException {
        Push push = new Push();
        push.setBody(body);
        push.setSubject(subject);
        push.setPayload(payloads);
        if (StringUtils.isNotBlank(externalId)) {
            push.setExternalId(externalId);
        }
        return pushService.save(push);
    }

    private void saveDevices(Push pushEntity, List<DeviceType> deviceList) throws BaseException {
        if (deviceList == null || deviceList.isEmpty()) {
            return;
        }

        for (DeviceType device : deviceList) {
            PushDevice pushDevice = new PushDevice();
            pushDevice.setDeviceId(device.getChannelId());
            pushDevice
                    .setDeviceType(EnumUtil.convert(device.getDeviceOS(), hu.icellmobilsoft.coffee.module.notification.model.enums.DeviceType.class));
            pushDevice.setPushId(pushEntity.getId());
            PushDevice savedPushDevice = pushDeviceService.save(pushDevice);
            pushEntity.getPushDevices().put(savedPushDevice.getDeviceId(), savedPushDevice);
        }
    }

    /**
     * Updates {@link PushDevice} of given device id and {@link Push} in DB.
     *
     * @param pushEntity
     *            {@code Push} entity that connects to the device which has to be updated
     * @param deviceId
     *            id of {@code PushDevice} to update
     * @param success
     *            whether push was successful
     * @param sendResult
     *            result message of push
     * @throws BaseException
     *             if exception occurs during entity merge
     * @see PushDeviceService#save
     */
    @Transactional
    public void updateDb(Push pushEntity, String deviceId, boolean success, String sendResult) throws BaseException {
        if (pushEntity == null || pushEntity.getPushDevices() == null || pushEntity.getPushDevices().isEmpty() || StringUtils.isBlank(deviceId)) {
            return;
        }

        PushDevice pushDeviceEntity = pushEntity.getPushDevices().get(deviceId);

        pushDeviceEntity.setSentTime(new Date());
        pushDeviceEntity.setSuccess(success);
        pushDeviceEntity.setResult(StringUtils.abbreviate(sendResult, 2048));
        pushDeviceService.save(pushDeviceEntity);
    }

}
