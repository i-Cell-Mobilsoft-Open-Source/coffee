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
package hu.icellmobilsoft.coffee.module.document.service;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.jpa.service.BaseService;
import hu.icellmobilsoft.coffee.module.document.model.TemplateParameter;
import hu.icellmobilsoft.coffee.module.document.repository.TemplateParameterRepository;

/**
 * Service for TemplateParameter functionality. Represents only DB operations
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Model
public class TemplateParameterService extends BaseService<TemplateParameter> {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TemplateParameterRepository templateParameterRepository;

    /**
     * <p>findAll.</p>
     *
     * @param templateKey
     *            not null value of template key
     * @param language
     *            not null value of language
     * @return entity
     * @throws BaseException
     */
    public List<TemplateParameter> findAll(String templateKey, String language) throws BaseException {
        log.trace(">> findAll(templateKey: [{0}] ,language: [{1}]", templateKey, language);
        if (StringUtils.isAnyBlank(templateKey, language)) {
            throw new BaseException("templateKey or language is null");
        }
        try {
            return templateParameterRepository.findAllTemplateKeyAndLanguage(templateKey, language);
        } catch (Exception e) {
            String msg = String.format("Error occured in finding TemplateParameter by templateKey: [%s] ,language: [%s]: %s", templateKey,
                    templateKey, e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        } finally {
            log.trace("<< findAll(templateKey: [{0}] ,language: [{1}]", templateKey, language);
        }
    }
}
