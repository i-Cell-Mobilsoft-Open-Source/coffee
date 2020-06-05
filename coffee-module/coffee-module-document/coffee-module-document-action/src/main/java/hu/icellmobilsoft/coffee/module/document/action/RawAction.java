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

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.document.model.TemplateData;
import hu.icellmobilsoft.coffee.module.document.service.TemplateDataService;
import hu.icellmobilsoft.coffee.rest.utils.ResponseUtil;

/**
 * Raw data manipulating business logic class
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Model
public class RawAction {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TemplateDataService templateDataService;

    /**
     * get template raw data from DB
     *
     * @param templateDataId
     *            templateData.id
     * @return file response with templateData.data content
     * @throws BaseException
     */
    public Response findRaw(String templateDataId) throws BaseException {
        if (StringUtils.isBlank(templateDataId)) {
            throw new BaseException("templateDataId is null");
        }
        TemplateData entityData = templateDataService.findById(templateDataId);
        return ResponseUtil.getFileResponse(entityData.getData(), templateDataId + "_" + entityData.getLanguage() + ".raw",
                MediaType.APPLICATION_OCTET_STREAM);
    }

    /**
     * Warning: Transaction required!
     *
     * @param templateDataId
     *            templateData.id
     * @param inputStream
     *            stream with new templateData.data content
     * @return String message
     * @throws BaseException
     */
    public String updateRaw(String templateDataId, InputStream inputStream) throws BaseException {
        if (StringUtils.isBlank(templateDataId) || inputStream == null) {
            throw new BaseException("templateDataId or inputStream is null");
        }
        try {
            String fileContent = IOUtils.toString(inputStream);
            TemplateData entityData = templateDataService.findById(templateDataId);
            entityData.setData(fileContent);
            templateDataService.save(entityData);
            return "RAW template [" + templateDataId + "] saved successfully";
        } catch (IOException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in processing RAW template: " + e.getLocalizedMessage(), e);
        }
    }
}
