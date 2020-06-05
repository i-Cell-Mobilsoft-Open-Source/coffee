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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.activation.DataHandler;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.notification.action.email.annotation.EmailOnly;

/**
 * Alap e-mail kikuldeset kezeli. Apache commons-email {@link https://commons.apache.org/proper/commons-email} csomagbol indul ki.<br>
 * Minden exception a sajat BaseException-ra van forditva<br>
 * Hogyha a tartalom sima text, akkor a <br>
 * Minta hasznalat
 *
 * <pre>
 * private static final String MAIL_SERVICE_JNDI = "java:/TraconMail";
 *
 * &#64;Inject
 * &#64;EmailOnly
 * private EmailBase emailBase;
 *
 * &#64;Resource(mappedName = MAIL_SERVICE_JNDI)
 * private Session mailSession;
 *
 * public void sendMail() {
 *     try {
 *         emailBase.setMailSession(mailSession);
 *
 *         emailBase.addTo("imre.scheffer@icellmobilsoft.hu");
 *         emailBase.setFrom("imrich.scheffer@gmail.com");
 *         emailBase.setSubject("test");
 *
 *         // sima text tartalom vagy html
 *         emailBase.setBody("test body");
 *         // emailBase.setBody("&ltHTML>&ltHEAD>&ltTITLE>test html title&lt/TITLE>&lt/HEAD>&ltBODY>test html body&lt/BODY>&lt/HTML>");
 *
 *         File file = new File("c:/TEMP/photo_1417611404439.jpg");
 *         byte[] fileByte = FileUtils.readFileToByteArray(file);
 *         emailBase.addAttachment(file.getName(), fileByte);
 *
 *         emailBase.send();
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * }
 * </pre>
 *
 * @author ischeffer
 * @since 1.0.0
 */
@Dependent
@EmailOnly
public class EmailBase {

    @Inject
    @ThisLogger
    private AppLogger log;

    /** Constant <code>EMAIL_LIST_DELIMITER=";"</code> */
    public static final String EMAIL_LIST_DELIMITER = ";";

    private Email email;

    // csak belso celokra, mert egyebkent az email-ben benne van, csak nem elerheto
    private Object content;
    private String to;

    /**
     * <p>setMailSession.</p>
     */
    public void setMailSession(Session mailSession) {
        getEmail().setMailSession(mailSession);
    }

    /**
     * <p>addTo.</p>
     *
     * @param to
     *            lehet {@value #EMAIL_LIST_DELIMITER} karaketerrel elvalasztott lista string is
     * @throws BaseException
     */
    public void addTo(String to) throws BaseException {
        if (StringUtils.isBlank(to)) {
            return;
        }
        try {
            if (this.to != null) {
                this.to = this.to + EMAIL_LIST_DELIMITER + to;
            } else {
                this.to = to;
            }
            getEmail().addTo(addressToArray(to));
        } catch (EmailException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in add 'to': " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>setFrom.</p>
     */
    public void setFrom(String from) throws BaseException {
        try {
            if (StringUtils.isBlank(from)) {
                getEmail().setFrom(System.getProperty("swarm.mail.mail-sessions.project-default.from"));
            } else {
                getEmail().setFrom(from);
            }
        } catch (EmailException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in add 'from': " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>addCc.</p>
     *
     * @param cc
     *            lehet {@value #EMAIL_LIST_DELIMITER} karaketerrel elvalasztott lista string is
     * @throws BaseException
     */
    public void addCc(String cc) throws BaseException {
        if (StringUtils.isBlank(cc)) {
            return;
        }
        try {
            getEmail().addCc(addressToArray(cc));
        } catch (EmailException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in add 'cc': " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>addBcc.</p>
     *
     * @param bcc
     *            lehet {@value #EMAIL_LIST_DELIMITER} karaketerrel elvalasztott lista string is
     * @throws BaseException
     */
    public void addBcc(String bcc) throws BaseException {
        if (StringUtils.isBlank(bcc)) {
            return;
        }
        try {
            getEmail().addBcc(addressToArray(bcc));
        } catch (EmailException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in add 'bcc': " + e.getLocalizedMessage(), e);
        }
    }

    private String[] addressToArray(String address) {
        return StringUtils.split(address, EMAIL_LIST_DELIMITER);
    }

    /**
     * Email targy
     *
     * @param subject
     */
    public void setSubject(String subject) {
        getEmail().setSubject(subject);
    }

    /**
     * Email tartalom. Ellenorzi hogy a tartalom "&lt" karakterrel kezdodik es vegzodik ">" karakterrel, ha igen akkor html body-kent rakja be
     *
     * @param bodyContent
     *            vagy sima szoveg, vahu html formatumuban levo szoveg
     * @throws BaseException
     */
    public void setBody(String bodyContent) throws BaseException {
        boolean isHtml = StringUtils.startsWith(bodyContent, "<") && StringUtils.endsWith(bodyContent, ">");
        if (isHtml) {
            setHtmlContent(bodyContent);
        } else {
            setTextContent(bodyContent);
        }
    }

    /**
     * Email tartalma es kodolasa
     *
     * @param body
     *            pl.: email szoveges tartalma
     * @param contentType
     *            pl.: "text/html; charset=utf-8"
     */
    public void setContent(Object body, String contentType) {
        content = body;
        getEmail().setContent(body, contentType);
    }

    /**
     * Html email tartalom ("text/html; charset=utf-8")
     *
     * @param htmlBody
     */
    public void setHtmlContent(String htmlBody) {
        setContent(htmlBody, "text/html; charset=utf-8");
    }

    /**
     * text email tartalom "plain/text; charset=utf-8"
     *
     * @param textBody
     */
    public void setTextContent(String textBody) throws BaseException {
        try {
            content = textBody;
            getEmail().setMsg(textBody);
        } catch (EmailException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in set msg: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Regi kompatibilitas vegett lett ide is atemelve
     *
     * @param attachments
     * @throws BaseException
     */
    public void addAttachments(List<AttachmentBean> attachments) throws BaseException {
        if (attachments != null) {
            for (AttachmentBean attachment : attachments) {
                addAttachment(attachment.getName(), attachment.getData());
            }
        }
    }

    /**
     * Tartalom hozza adasa az emailhez
     *
     * @param name
     *            mondjuk fajlnev, vagy egyeb megnevezes ami alatt szerepeljen a tartalom
     * @param data
     *            tartalom adat
     * @throws BaseException
     */
    public void addAttachment(String name, byte[] data) throws BaseException {
        MimeBodyPart part = new MimeBodyPart();
        ByteArrayDataSource source = new ByteArrayDataSource(data, "application/octet-stream");
        try {
            part.setDataHandler(new DataHandler(source));
            part.setFileName(name);
            MimeMultipart mime = new MimeMultipart();
            mime.addBodyPart(part);
            if (getEmail() instanceof MultiPartEmail) {
                ((MultiPartEmail) getEmail()).addPart(mime);
            } else {
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                        "Email object is not instance of MultiPartEmail! email class: [" + getEmail().getClass() + "]");
            }
        } catch (MessagingException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    "MessagingException in add 'attachment', name: [" + name + "], error: " + e.getLocalizedMessage(), e);
        } catch (EmailException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                    "EmailException in add 'attachment', name: [" + name + "], error: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Email elkuldese
     *
     * @return {@link Email#send()}
     * @throws BaseException
     */
    public String send() throws BaseException {
        try {
            log.info(emailString(getEmail()));
            if (getEmail().getToAddresses().isEmpty() || getEmail().getSubject() == null || content == null) {
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "To, Subject and Content cant be null");
            }

            buildMimeMessage();
            return sendMimeMessage();

        } catch (EmailException e) {
            clear();
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Exception in send email: " + e.getLocalizedMessage(), e);
        }
    }

    private void buildMimeMessage() throws EmailException {
        try {
            getEmail().buildMimeMessage();
        } catch (IllegalStateException e) {
            log.error("MimeMessage already built, redelivery happened");
        }
    }

    private String sendMimeMessage() throws EmailException {
        return getEmail().sendMimeMessage();
    }

    /**
     * Osszegyujti az email objektum adatait egy String-be
     *
     * @param email
     * @throws BaseException
     */
    public String emailString(Email email) throws BaseException {
        StringBuffer msg = new StringBuffer();
        // java 1.7
        // String lineSeparator = System.lineSeparator();
        String lineSeparator = System.getProperty("line.separator");
        msg.append(lineSeparator).append("Sending email");
        if (email.getFromAddress() != null) {
            msg.append(lineSeparator).append("from: ").append(email.getFromAddress());
        }
        for (InternetAddress ia : email.getToAddresses()) {
            msg.append(lineSeparator).append("to: ").append(ia.toString());
        }
        for (InternetAddress ia : email.getCcAddresses()) {
            msg.append(lineSeparator).append("cc: ").append(ia.toString());
        }
        for (InternetAddress ia : email.getBccAddresses()) {
            msg.append(lineSeparator).append("bcc: ").append(ia.toString());
        }
        for (InternetAddress ia : email.getReplyToAddresses()) {
            msg.append(lineSeparator).append("reply: ").append(ia.toString());
        }
        if (email.getBounceAddress() != null) {
            msg.append(lineSeparator).append("bounce address: ").append(email.getBounceAddress());
        }
        if (email.getHostName() != null) {
            msg.append(lineSeparator).append("hostname: ").append(email.getHostName());
        }
        if (email.getSentDate() != null) {
            msg.append(lineSeparator).append("sent date: ").append(email.getSentDate());
        }
        if (email.getSmtpPort() != null) {
            msg.append(lineSeparator).append("smtp port: ").append(email.getSmtpPort());
        }
        msg.append(lineSeparator).append("socket connection timeout: ").append(email.getSocketConnectionTimeout());
        msg.append(lineSeparator).append("socket timeout: ").append(email.getSocketTimeout());
        if (email.getSslSmtpPort() != null) {
            msg.append(lineSeparator).append("ssl smtp port: ").append(email.getSslSmtpPort());
        }
        if (email.getSubject() != null) {
            msg.append(lineSeparator).append("subject: ").append(email.getSubject());
        }
        try {
            if (email.getMimeMessage() != null && email.getMimeMessage().getContent() != null) {
                msg.append(lineSeparator).append("mime message - content: ").append(email.getMimeMessage().getContent());
            }
        } catch (IOException e) {
            log.error("IOException on getContent", e);
        } catch (MessagingException e) {
            log.error("MessagingException on getContent", e);
        }
        return msg.toString();
    }

    /**
     * Ha nem volt megadva, akkor alapbol org.apache.commons.mail.MultiPartEmail tipust hoz letre. Kezzel allithato mas fajtara a set-en keresztul
     */
    public Email getEmail() {
        if (email == null) {
            email = new MultiPartEmail();
            email.setCharset(StandardCharsets.UTF_8.name());
        }
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     */
    public void setEmail(Email email) {
        this.email = email;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     */
    public Object getContent() {
        return content;
    }

    /**
     * <p>Getter for the field <code>to</code>.</p>
     */
    public String getTo() {
        return to;
    }

    /**
     * <p>clear.</p>
     */
    public void clear() {
        to = null;
        content = null;
        email = null;
    }
}
