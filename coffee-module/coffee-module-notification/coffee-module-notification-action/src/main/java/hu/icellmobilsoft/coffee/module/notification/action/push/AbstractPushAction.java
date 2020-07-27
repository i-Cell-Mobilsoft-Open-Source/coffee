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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.common.KeyValueBasicType;
import hu.icellmobilsoft.coffee.dto.document.document.TemplateFullType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.dto.notification.notification.DeviceOSType;
import hu.icellmobilsoft.coffee.dto.notification.notification.DeviceType;
import hu.icellmobilsoft.coffee.dto.notification.notification.PushType;
import hu.icellmobilsoft.coffee.module.notification.exception.PushClientException;
import hu.icellmobilsoft.coffee.module.notification.exception.PushServerException;
import hu.icellmobilsoft.coffee.module.notification.model.Push;

/**
 * Push notification business logic class
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public abstract class AbstractPushAction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private PushHelper pushHelper;

    /**
     * <p>send.</p>
     */
    public void send(PushType pushType) throws BaseException {
        if (pushType == null) {
            return;
        }
        validate(pushType);
        TemplateFullType template = getTemplate(pushType.getTemplateKey(), pushType.getParameter(), pushType.getLanguage());
        push(pushType, template);
    }

    /**
     * <p>push.</p>
     */
    protected void push(PushType pushType, TemplateFullType templateFullType) throws BaseException {
        Push pushEntity = pushHelper.insertToDb(templateFullType.getSubject(), new String(templateFullType.getData(), StandardCharsets.UTF_8),
                pushType.getExternalId(), pushType.getDevice(), pushType.getPayload());
        for (String channel : androidChannels(pushType)) {
            String responseMessage;
            try {
                responseMessage = sendAndroidPush(channel, pushType.getPayload(), templateFullType, pushType.getExpire());
                log.debug("Message to channel[{0}] sended: [{1}]", channel, responseMessage);
                pushHelper.updateDb(pushEntity, channel, true, responseMessage);
            } catch (PushClientException e) {
                String message = String.format("Error in client push channel[%s]: [%s]", channel, e.getLocalizedMessage());
                pushHelper.updateDb(pushEntity, channel, false, message);
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, message, e);
            } catch (PushServerException e) {
                String message = String.format("Error in server push channel[%s]: [%s]", channel, e.getLocalizedMessage());
                pushHelper.updateDb(pushEntity, channel, false, message);
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, message, e);
            }
        }
        for (String channel : iosChannels(pushType)) {
            String responseMessage;
            try {
                responseMessage = sendIosPush(channel, pushType.getPayload(), templateFullType, pushType.getExpire());
                log.debug("Message to channel[{0}] sended: [{1}]", channel, responseMessage);
                pushHelper.updateDb(pushEntity, channel, true, responseMessage);
            } catch (PushClientException e) {
                String message = "Error in client pushing: " + e.getLocalizedMessage();
                pushHelper.updateDb(pushEntity, channel, false, message);
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, message, e);
            } catch (PushServerException e) {
                String message = String.format("Error in server push channel[%s]: [%s]", channel, e.getLocalizedMessage());
                pushHelper.updateDb(pushEntity, channel, false, message);
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, message, e);
            }
        }
    }

    /**
     * <p>validate.</p>
     */
    public void validate(PushType pushType) throws BaseException {
    }

    /**
     * <p>androidChannels.</p>
     */
    public List<String> androidChannels(PushType pushType) {
        return findChannels(pushType, DeviceOSType.ANDROID);
    }

    /**
     * <p>iosChannels.</p>
     */
    public List<String> iosChannels(PushType pushType) {
        return findChannels(pushType, DeviceOSType.IOS);
    }

    /**
     * <p>findChannels.</p>
     */
    protected List<String> findChannels(PushType pushType, DeviceOSType deviceOSType) {
        if (pushType == null) {
            return Collections.emptyList();
        }
        return pushType.getDevice().stream().filter(d -> d.getDeviceOS() == deviceOSType).map(DeviceType::getChannelId).collect(Collectors.toList());
    }

    /**
     * <p>sendAndroidPush.</p>
     */
    public abstract String sendAndroidPush(String channel, List<KeyValueBasicType> payloads, TemplateFullType templateFullType, Integer expireInSecond)
            throws PushClientException, PushServerException;

    /**
     * <p>sendIosPush.</p>
     */
    public abstract String sendIosPush(String channel, List<KeyValueBasicType> payloads, TemplateFullType templateFullType, Integer expireInSecond)
            throws PushClientException, PushServerException;

    /**
     * <p>getTemplate.</p>
     */
    protected abstract TemplateFullType getTemplate(String templateKey, List<KeyValueBasicType> parameters, String language) throws BaseException;
}
