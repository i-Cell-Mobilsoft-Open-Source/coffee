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

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.jpa.service.BaseService;
import hu.icellmobilsoft.coffee.module.notification.model.EmailRecipient;
import hu.icellmobilsoft.coffee.module.notification.repository.EmailRecipientRepository;

/**
 * Service for {@link EmailRecipient} functionality. Represents only DB operations
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class EmailRecipientService extends BaseService<EmailRecipient> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Wrapped repository
     */
    @Inject
    private EmailRecipientRepository emailRecipientRepository;

    /**
     * Default constructor, constructs a new object.
     */
    public EmailRecipientService() {
        super();
    }

}
