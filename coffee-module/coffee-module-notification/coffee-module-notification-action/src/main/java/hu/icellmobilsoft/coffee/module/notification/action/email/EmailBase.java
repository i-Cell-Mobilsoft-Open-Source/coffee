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

import jakarta.activation.DataHandler;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

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
 * Handles basic e-mail sending, based on Apache commons-email
 * <a href="https://commons.apache.org/proper/commons-email">https://commons.apache.org/proper/commons-email</a> package.<br>
 * All exceptions are converted to {@link BaseException}.<br>
 * Usage example for simple text content: <br>
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
 *         // basic text content or html
 *         emailBase.setBody("test body");
 *         // emailBase.setBody("&lt;HTML&gt;&lt;HEAD&gt;&lt;TITLE&gt;test html title&lt;/TITLE&gt;&lt;/HEAD&gt;&lt;BODY&gt;test html body&lt;/BODY&gt;&lt;/HTML&gt;");
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

    // For internal purposes only, because it is included in the email but not accessible otherwise
    private Object content;
    private String to;

    /**
     * Setter for {@code mailSession} field of {@link Email}.
     * 
     * @param mailSession
     *            mailSession to set
     */
    public void setMailSession(Session mailSession) {
        getEmail().setMailSession(mailSession);
    }

    /**
     * Adds {@code to} field to {@link Email}.
     *
     * @param to
     *            can be multiple to-s by separating them with {@value #EMAIL_LIST_DELIMITER}
     * @throws BaseException
     *             if email exception occurs
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
     * Adds {@code from} field to {@link Email}.
     *
     * @param from
     *            if empty, uses system property "swarm.mail.mail-sessions.project-default.from"
     * @throws BaseException
     *             if email exception
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
     * Adds CC field to {@link Email}.
     *
     * @param cc
     *            can be multiple cc-s by separating them with {@value #EMAIL_LIST_DELIMITER}
     * @throws BaseException
     *             if email exception occurs
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
     * Adds BCC field to {@link Email}.
     *
     * @param bcc
     *            can be multiple bcc-s by separating them with {@value #EMAIL_LIST_DELIMITER}
     * @throws BaseException
     *             if email exception
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
     * Setter for subject field of {@link Email}.
     *
     * @param subject
     *            subject to set
     */
    public void setSubject(String subject) {
        getEmail().setSubject(subject);
    }

    /**
     * Sets {@link Email} body content. If given content is between "&lt;" and "&gt;" characters, sets the content type to HTML.
     *
     * @param bodyContent
     *            basic text or HTML content
     * @throws BaseException
     *             if email exception occurs
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
     * Sets {@link Email} body and content type.
     *
     * @param body
     *            text content
     * @param contentType
     *            eg.: "text/html; charset=utf-8"
     */
    public void setContent(Object body, String contentType) {
        content = body;
        getEmail().setContent(body, contentType);
    }

    /**
     * Sets {@link Email} body and HTML content type (text/html; charset=utf-8).
     *
     * @param htmlBody
     *            HTML content
     */
    public void setHtmlContent(String htmlBody) {
        setContent(htmlBody, "text/html; charset=utf-8");
    }

    /**
     * Sets {@link Email} body and plain text content type (plain/text; charset=utf-8).
     *
     * @param textBody
     *            text content
     * @throws BaseException
     *             if email exception occurs
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
     * Adds attachments to the email. This method is implemented for compatibility reasons.
     *
     * @param attachments
     *            attachments to add
     * @throws BaseException
     *             if email is not a {@link MultiPartEmail}, or messaging/email exception occurs
     * @see #addAttachment(String, byte[])
     */
    public void addAttachments(List<AttachmentBean> attachments) throws BaseException {
        if (attachments != null) {
            for (AttachmentBean attachment : attachments) {
                addAttachment(attachment.getName(), attachment.getData());
            }
        }
    }

    /**
     * Adds attachment to the email.
     *
     * @param name
     *            attachment name (filename)
     * @param data
     *            attachment
     * @throws BaseException
     *             if email is not a {@link MultiPartEmail}, or messaging/email exception occurs
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
     * Sends email.
     *
     * @return message id
     * @throws BaseException
     *             if email fields are empty or email exception occurs
     * @see Email#send()
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
     * Collects and returns the fields and data of the {@link Email} object to a {@link String}. Osszegyujti az email objektum adatait egy String-be
     *
     * @param email
     *            email object
     * @return email {@code String}
     * @throws BaseException
     *             exception
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
     * Getter for the field {@code email}. If it's not initialized yet, creates an {@link org.apache.commons.mail.MultiPartEmail} instance. For a
     * different {@link Email} class, use {@link #setEmail(Email)}.
     *
     * @return {@code email}
     */
    public Email getEmail() {
        if (email == null) {
            email = new MultiPartEmail();
            email.setCharset(StandardCharsets.UTF_8.name());
        }
        return email;
    }

    /**
     * Setter for the field {@code email}.
     *
     * @param email
     *            email to set
     */
    public void setEmail(Email email) {
        this.email = email;
    }

    /**
     * Getter for the field {@code content}.
     *
     * @return {@code content}
     */
    public Object getContent() {
        return content;
    }

    /**
     * Getter for the field {@code to}.
     *
     * @return {@code to}
     */
    public String getTo() {
        return to;
    }

    /**
     * Clears {@code to}, {@code content} and {@code email} fields of this.
     */
    public void clear() {
        to = null;
        content = null;
        email = null;
    }
}
