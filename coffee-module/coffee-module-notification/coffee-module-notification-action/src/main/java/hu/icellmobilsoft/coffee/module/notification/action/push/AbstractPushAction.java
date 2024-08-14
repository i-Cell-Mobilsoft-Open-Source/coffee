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

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.common.KeyValueBasicType;
import hu.icellmobilsoft.coffee.dto.document.document.TemplateFullType;
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

    /**
     * Logger
     */
    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Injected PushHelper
     */
    @Inject
    private PushHelper pushHelper;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractPushAction() {
        super();
    }

    /**
     * Sends given {@link PushType}.
     *
     * @param pushType
     *            push type to send
     * @throws BaseException
     *             if exception occurs during push type validation, push template retrieval, or push sending.
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
     * Sends given {@link PushType} using given {@link TemplateFullType}.
     *
     * @param pushType
     *            push type to send
     * @param templateFullType
     *            template to use
     * @throws BaseException
     *             if exception occurs during push saving or sending
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
     * Validates given {@link PushType}.
     *
     * @param pushType
     *            {@code PushType} to validate
     * @throws BaseException
     *             if {@code PushType} is invalid
     */
    public void validate(PushType pushType) throws BaseException {
    }

    /**
     * Finds android channels for given {@link PushType}.
     *
     * @param pushType
     *            {@code PushType} to find channels for
     * @return {@link List} of android channels
     */
    public List<String> androidChannels(PushType pushType) {
        return findChannels(pushType, DeviceOSType.ANDROID);
    }

    /**
     * Finds IOS channels for given {@link PushType}.
     *
     * @param pushType
     *            {@code PushType} to find channels for
     * @return {@link List} of IOS channels
     */
    public List<String> iosChannels(PushType pushType) {
        return findChannels(pushType, DeviceOSType.IOS);
    }

    /**
     * Finds channels for given {@link PushType} and {@link DeviceOSType}.
     *
     * @param pushType
     *            {@code PushType} to find channels for
     * @param deviceOSType
     *            {@code DeviceOSType} to find channels for
     * @return {@link List} of channels
     */
    protected List<String> findChannels(PushType pushType, DeviceOSType deviceOSType) {
        if (pushType == null) {
            return Collections.emptyList();
        }
        return pushType.getDevice().stream().filter(d -> d.getDeviceOS() == deviceOSType).map(DeviceType::getChannelId).collect(Collectors.toList());
    }

    /**
     * Sends push to android with given parameters.
     *
     * @param channel
     *            channel to send to
     * @param payloads
     *            push payloads
     * @param templateFullType
     *            push template
     * @param expireInSecond
     *            push expiry time in seconds
     * @return push response message
     * @throws PushClientException
     *             if exception occurs on the client
     * @throws PushServerException
     *             if exception occurs on the server
     */
    public abstract String sendAndroidPush(String channel, List<KeyValueBasicType> payloads, TemplateFullType templateFullType,
            Integer expireInSecond) throws PushClientException, PushServerException;

    /**
     * Sends push to IOS with given parameters.
     *
     * @param channel
     *            channel to send to
     * @param payloads
     *            push payloads
     * @param templateFullType
     *            push template
     * @param expireInSecond
     *            push expiry time in seconds
     * @return push response message
     * @throws PushClientException
     *             if exception occurs on the client
     * @throws PushServerException
     *             if exception occurs on the server
     */
    public abstract String sendIosPush(String channel, List<KeyValueBasicType> payloads, TemplateFullType templateFullType, Integer expireInSecond)
            throws PushClientException, PushServerException;

    /**
     * Returns {@link TemplateFullType} with given parameters.
     * 
     * @param templateKey
     *            key of template
     * @param parameters
     *            template params
     * @param language
     *            template language
     * @return {@code TemplateFullType}
     * @throws BaseException
     *             if template cannot be found
     */
    protected abstract TemplateFullType getTemplate(String templateKey, List<KeyValueBasicType> parameters, String language) throws BaseException;
}
