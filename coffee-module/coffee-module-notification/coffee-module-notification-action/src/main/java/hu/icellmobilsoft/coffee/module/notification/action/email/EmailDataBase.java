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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.notification.model.Email;

/**
 * {@link EmailBase} mukodeset terjeszti ki DB mentessel. Ebben az esetben az OUTPUT_JOB tablat kezeli
 *
 * <pre>
 * private static final String MAIL_SERVICE_JNDI = "java:/TraconMail";
 *
 * &#64;Inject
 * private EmailDataBase emailDb;
 *
 * &#64;Resource(mappedName = MAIL_SERVICE_JNDI)
 * private Session mailSession;
 *
 * public void sendMail() {
 *     try {
 *         emailDb.setMailSession(mailSession);
 *
 *         emailDb.addTo("imre.scheffer@icellmobilsoft.hu");
 *         emailDb.setFrom("imrich.scheffer@gmail.com");
 *         emailDb.setSubject("test");
 *
 *         // sima text tartalom vagy html
 *         emailDb.setBody("test body");
 *         // emailDb.setBody("&ltHTML>&ltHEAD>&ltTITLE>test html title&lt/TITLE>&lt/HEAD>&ltBODY>test html body&lt/BODY>&lt/HTML>");
 *
 *         File file = new File("c:/TEMP/photo_1417611404439.jpg");
 *         byte[] fileByte = FileUtils.readFileToByteArray(file);
 *         emailDb.addAttachment(file.getName(), fileByte);
 *
 *         emailDb.send();
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
public class EmailDataBase extends EmailBase {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private EmailHelper emailHelper;

    /** {@inheritDoc} */
    @Override
    public String send() throws BaseException {
        // nincs requestScope, nem ugyan az az osztaly!
        Email emailEntity = emailHelper.insertToDb(this);
        String sendResult = null;
        try {
            sendResult = super.send();
            emailHelper.updateDb(emailEntity, true, sendResult);
        } catch (BaseException e) {
            emailHelper.updateDb(emailEntity, false, e.getLocalizedMessage());
            throw e;
        }
        return sendResult;
    }
}
