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
package hu.icellmobilsoft.coffee.module.notification.action.email;

import java.io.Serializable;
import java.util.List;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.mail.Session;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.common.KeyValueBasicType;
import hu.icellmobilsoft.coffee.dto.document.document.TemplateFullType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.notification.notification.EmailPlainTextType;

/**
 * AbstractEmailPlainTextAction class.
 *
 * @since 1.0.0
 */
@Dependent
public abstract class AbstractEmailPlainTextAction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Logger
     */
    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Injected email session
     */
    // @Resource(mappedName = "java:jboss/mail/default")
    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    private Session sessionDefault;

    /**
     * Injected email database
     */
    @Inject
    private EmailDataBase emailDataBase;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractEmailPlainTextAction() {
        super();
    }

    /**
     * Sends given {@link EmailPlainTextType} and saves it to the database.
     *
     * @param emailType
     *            email to send
     * @throws BaseException
     *             if email fields are empty, template cannot be found, or email exception occurs
     */
    public void sendAndSave(EmailPlainTextType emailType) throws BaseException {
        if (emailType == null) {
            return;
        }
        validate(emailType);

        emailDataBase.setMailSession(sessionDefault);
        for (String to : emailType.getTo()) {
            emailDataBase.addTo(to);
        }
        for (String cc : emailType.getCc()) {
            emailDataBase.addCc(cc);
        }
        for (String bcc : emailType.getBcc()) {
            emailDataBase.addBcc(bcc);
        }
        emailDataBase.setFrom(emailType.getFrom());

        emailDataBase.setSubject(emailType.getSubject() == null ? "subject" : emailType.getSubject());
        emailDataBase.setBody(emailType.getBody());

        emailDataBase.send();
        emailDataBase.clear();
    }

    /**
     * Checks if given {@link EmailPlainTextType} is valid.
     *
     * @param emailType
     *            email to validate
     * @throws BaseException
     *             if {@code emailType} is invalid
     */
    public void validate(EmailPlainTextType emailType) throws BaseException {
    }

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
