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

import java.text.MessageFormat;
import java.util.Date;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.jpa.service.BaseService;
import hu.icellmobilsoft.coffee.module.document.model.TemplateData;
import hu.icellmobilsoft.coffee.module.document.model.enums.TemplateDataType;
import hu.icellmobilsoft.coffee.module.document.repository.TemplateDataRepository;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Service for TemplateData functionality. Represents only DB operations
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Model
public class TemplateDataService extends BaseService<TemplateData> {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TemplateDataRepository templateDataRepository;

    /**
     * <p>find.</p>
     *
     * @param templateKey
     *            not null value of template key
     * @param dataType
     *            not null value of template data type
     * @param language
     *            not null value of language
     * @param date
     *            searching date, can be null
     * @return entity
     * @throws BaseException
     */
    public TemplateData find(String templateKey, TemplateDataType dataType, String language, Date date) throws BaseException {
        log.trace(">> find(templateKey: [{0}], dataType: [{1}], language: [{2}], date: [{3}]", templateKey, dataType, language, date);
        if (StringUtils.isAnyBlank(templateKey, language) || dataType == null) {
            throw new BaseException("templateKey, templateType or language cant be a null");
        }
        Date findDate = date == null ? new Date() : date;
        try {
            return templateDataRepository.find(templateKey, dataType, language, findDate);
        } catch (NoResultException e) {
            throw new BONotFoundException("TemplateData by templateKey: [" + templateKey + "] ,dataType: [" + dataType + "] ,language: [" + language
                    + "] ,date: [" + findDate + "] not found");
        } catch (Exception e) {
            String msg = MessageFormat.format("Error occurred in finding TemplateData by templateKey: [{0}] ,dataType: [{1}] ,language: [{2}] ,date: [{3,date," + DateUtil.DEFAULT_FULL_PATTERN + "}]: {4}",
                    templateKey, dataType.name(), language, findDate, e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        } finally {
            log.trace("<< find(templateKey: [{0}] ,dataType: [{1}] ,language: [{2}] ,date: [{3}]", templateKey, dataType, language, date);
        }
    }

    /**
     * <p>find.</p>
     *
     * @param templateKey
     *            not null value of template key
     * @param dataType
     *            not null value of template data type
     * @param language
     *            not null value of language
     * @return entity
     * @throws BaseException
     */
    public TemplateData find(String templateKey, TemplateDataType dataType, String language) throws BaseException {
        return find(templateKey, dataType, language, null);
    }

    /**
     * Find TemplateData by id.
     *
     * @param id
     * @throws BaseException
     */
    public TemplateData findById(String id) throws BaseException {
        log.trace(">> TemplateDataService.findById(id: [{0}])", id);
        if (id == null) {
            return null;
        }
        TemplateData templateData = null;
        try {
            templateData = templateDataRepository.findBy(id);
        } catch (Exception e) {
            String msg = String.format("Error occured in finding TemplateData by id[%s]: %s", id, e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        }
        if (templateData == null) {
            throw new BONotFoundException("TemplateData by id [" + id + "] not found");
        }
        log.trace("<< TemplateDataService.findById(id: [{0}])", id);
        return templateData;
    }
}
