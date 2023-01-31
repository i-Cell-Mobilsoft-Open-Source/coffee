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
import java.util.Date;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.jpa.annotation.Transactional;
import hu.icellmobilsoft.coffee.module.notification.model.Email;
import hu.icellmobilsoft.coffee.module.notification.model.EmailRecipient;
import hu.icellmobilsoft.coffee.module.notification.model.Recipient;
import hu.icellmobilsoft.coffee.module.notification.model.enums.RecipientType;
import hu.icellmobilsoft.coffee.module.notification.service.EmailRecipientService;
import hu.icellmobilsoft.coffee.module.notification.service.EmailService;
import hu.icellmobilsoft.coffee.module.notification.service.RecipientService;

/**
 * CDI helper class for email handling
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class EmailHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Injected RecipientService
     */
    @Inject
    private RecipientService recipientService;

    /**
     * Injected EmailRecipientService
     */
    @Inject
    private EmailRecipientService emailRecipientService;

    /**
     * Injected EmailService
     */
    @Inject
    private EmailService emailService;

    /**
     * Inserts {@link EmailDataBase} into DB.
     *
     * @param emailDataBase
     *            {@code emailDataBase} to insert
     * @return saved {@link Email}
     * @throws BaseException
     *             if exception occurs during entity merge
     * @see EmailService#save
     * @see EmailRecipientService#save
     */
    @Transactional
    public Email insertToDb(EmailDataBase emailDataBase) throws BaseException {
        if (emailDataBase == null || emailDataBase.getEmail() == null) {
            return null;
        }
        Email savedEmail = saveEmail(emailDataBase);
        saveRecipients(savedEmail, emailDataBase);
        return savedEmail;
    }

    private Email saveEmail(EmailDataBase emailDataBase) throws BaseException {
        Email email = new Email();
        if (emailDataBase.getContent() != null) {
            email.setBody(emailDataBase.getContent().toString());
        }
        if (emailDataBase.getEmail().getFromAddress() != null) {
            email.setFrom(emailDataBase.getEmail().getFromAddress().getAddress());
        }
        email.setSubject(emailDataBase.getEmail().getSubject());
        return emailService.save(email);
    }

    private void saveRecipients(Email emailEntity, EmailDataBase emailDataBase) throws BaseException {
        for (InternetAddress ia : emailDataBase.getEmail().getToAddresses()) {
            saveRecipient(ia.getAddress(), emailEntity, RecipientType.TO);
        }
        for (InternetAddress ia : emailDataBase.getEmail().getCcAddresses()) {
            saveRecipient(ia.getAddress(), emailEntity, RecipientType.CC);
        }
        for (InternetAddress ia : emailDataBase.getEmail().getBccAddresses()) {
            saveRecipient(ia.getAddress(), emailEntity, RecipientType.BCC);
        }
    }

    private void saveRecipient(String address, Email email, RecipientType recipientType) throws BaseException {
        Recipient recipient = null;
        try {
            recipient = recipientService.findByRecipient(address);
        } catch (BONotFoundException e) {
            Recipient r = new Recipient();
            r.setRecipient(address);
            recipient = recipientService.save(r);
        }
        EmailRecipient er = new EmailRecipient();
        er.setEmail(email);
        er.setRecipient(recipient);
        er.setRecipientType(recipientType);
        emailRecipientService.save(er);
    }

    /**
     * Updates given {@link Email} in DB.
     *
     * @param emailEntity
     *            {@code Email} entity which has to be updated
     * @param success
     *            whether email was sent successfully
     * @param sendResult
     *            result message of email
     * @return saved {@code Email}
     * @throws BaseException
     *             if exception occurs during entity merge
     * @see EmailService#save
     */
    @Transactional
    public Email updateDb(Email emailEntity, boolean success, String sendResult) throws BaseException {
        if (emailEntity == null) {
            return null;
        }
        emailEntity.setSentTime(new Date());
        emailEntity.setSuccess(success);
        emailEntity.setResult(StringUtils.abbreviate(sendResult, 2048));
        return emailService.save(emailEntity);
    }

}
