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
package hu.icellmobilsoft.coffee.module.document.action;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.common.KeyValueBasicType;
import hu.icellmobilsoft.coffee.dto.document.document.TemplateFullType;
import hu.icellmobilsoft.coffee.dto.document.document.TemplateType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.document.model.TemplateData;
import hu.icellmobilsoft.coffee.module.document.model.TemplateParameter;
import hu.icellmobilsoft.coffee.module.document.model.enums.TemplateDataType;
import hu.icellmobilsoft.coffee.module.document.service.TemplateDataService;
import hu.icellmobilsoft.coffee.module.document.service.TemplateParameterService;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * Template business logic class
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Model
public class TemplateAction {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TemplateDataService templateDataService;

    @Inject
    private TemplateParameterService templateParameterService;

    /**
     * Returns {@link TemplateFullType} by given {@link TemplateType}.
     * 
     * @param templateType
     *            dto
     * @return {@code TemplateFullType} dto
     * @throws BaseException
     *             if invalid parameter or processing error
     */
    public TemplateFullType getTemplate(TemplateType templateType) throws BaseException {
        if (templateType == null) {
            throw new InvalidParameterException("templateType is null");
        }

        String templateKey = templateType.getTemplateKey();
        String language = templateType.getLanguage();
        TemplateDataType dataType = TemplateDataType.valueOf(templateType.getType());
        Date date = null;
        if (templateType.getDate() != null) {
            date = DateUtil.toDate(templateType.getDate());
        }
        TemplateData templateData = templateDataService.find(templateKey, dataType, language, date);
        List<TemplateParameter> templateParameters = templateParameterService.findAll(templateKey, language);

        byte[] data = null;
        String subject = processText(templateData.getSubject(), templateParameters, templateType.getParameter());
        switch (dataType) {
        case TEXT:
            data = processText(templateData, templateParameters, templateType.getParameter());
            break;
        case BINARY:
            data = processBinary(templateData, templateParameters, templateType.getParameter());
            break;
        case HTML:
            data = processText(templateData, templateParameters, templateType.getParameter());
            break;
        default:
            throw new BaseException("Not implemeted yet!");
        }

        TemplateFullType response = new TemplateFullType();
        response.setTemplateDataId(templateData.getId());
        response.setData(data);
        response.setDate(DateUtil.toOffsetDateTime(date));
        response.setFileName(templateData.getDefaultFilename());
        response.setLanguage(templateData.getLanguage());
        response.setSubject(subject);
        response.setTemplateKey(templateData.getTemplateKey());
        response.setType(dataType.name());
        return response;
    }

    /**
     * Processes TEXT template. Replaces template parameters.
     * 
     * @param templateData
     *            entity
     * @param templateParameters
     *            entities
     * @param requestParameters
     *            parameters from request
     * @return replaced templateData.data value with defined values
     * @throws BaseException
     *             if text cannot be processed
     */
    private byte[] processText(TemplateData templateData, List<TemplateParameter> templateParameters, List<KeyValueBasicType> requestParameters)
            throws BaseException {
        String text = processText(templateData.getData(), templateParameters, requestParameters);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private String processText(String templatText, List<TemplateParameter> templateParameters, List<KeyValueBasicType> requestParameters)
            throws BaseException {
        if (templatText == null) {
            return null;
        }
        String text = templatText;
        for (TemplateParameter tp : templateParameters) {
            String value = getParameterValue(tp.getParameterKey(), requestParameters, tp.getDefaultValue());
            text = StringUtils.replace(text, "{" + tp.getParameterKey() + "}", value);
        }
        return text;
    }

    private byte[] processBinary(TemplateData templateData, List<TemplateParameter> templateParameters, List<KeyValueBasicType> requestParameters)
            throws BaseException {
        if (templateData.getData() == null) {
            return null;
        }
        return templateData.getData().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns value for given key from given collection of {@link KeyValueBasicType}s.
     *
     * @param key
     *            key of the desired parameter
     * @param requestParameters
     *            parameters to search in
     * @param defaultValue
     *            default parameter value
     * @return desired parameter or default parameter if desired parameter is null or cannot be found
     */
    public static String getParameterValue(String key, Collection<KeyValueBasicType> requestParameters, String defaultValue) {
        if (requestParameters == null) {
            return defaultValue;
        }
        for (KeyValueBasicType kv : requestParameters) {
            if (StringUtils.equalsIgnoreCase(key, kv.getKey())) {
                return kv.getValue();
            }
        }
        return defaultValue;
    }
}
