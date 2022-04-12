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
package hu.icellmobilsoft.coffee.module.notification.service;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.jpa.service.BaseService;
import hu.icellmobilsoft.coffee.module.notification.model.Recipient;
import hu.icellmobilsoft.coffee.module.notification.repository.RecipientRepository;

/**
 * Service for {@link Recipient} functionality. Represents only DB operations
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class RecipientService extends BaseService<Recipient> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Logger
     */
    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Wrapped repository
     */
    @Inject
    private RecipientRepository recipientRepository;

    /**
     * Find {@link Recipient} by recipient.
     *
     * @param recipient
     *            recipient
     * @return {@code Recipient} or null if input is null
     * @throws BaseException
     *             if {@code Recipient} not found or exception occurs in {@link RecipientRepository} call.
     * @see RecipientRepository#findByRecipient(String)
     */
    public Recipient findByRecipient(String recipient) throws BaseException {
        log.trace(">> RecipientService.findByRecipient(recipient: [{0}])", recipient);
        if (recipient == null) {
            return null;
        }
        try {
            return recipientRepository.findByRecipient(recipient);
        } catch (NoResultException e) {
            throw new BONotFoundException("Recipient by recipient [" + recipient + "] not found");
        } catch (Exception e) {
            String msg = String.format("Error occured in finding Recipient by recipient[%s]: %s", recipient, e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        } finally {
            log.trace("<< RecipientService.findByRecipient(recipient: [{0}])", recipient);
        }
    }
}
