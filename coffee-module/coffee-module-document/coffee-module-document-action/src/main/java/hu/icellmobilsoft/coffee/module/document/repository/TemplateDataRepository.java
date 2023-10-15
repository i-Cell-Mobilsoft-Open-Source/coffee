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
package hu.icellmobilsoft.coffee.module.document.repository;

import java.util.Date;

import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryParam;
import org.apache.deltaspike.data.api.Repository;

import hu.icellmobilsoft.coffee.module.document.model.TemplateData;
import hu.icellmobilsoft.coffee.module.document.model.enums.TemplateDataType;

/**
 * Common repository interface for handling {@link TemplateData}s.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Repository
public interface TemplateDataRepository extends EntityRepository<TemplateData, String> {

    /**
     * Finds {@link TemplateData} by template key, data type and language and validity date.
     *
     * @param templateKey
     *            value of template key
     * @param dataType
     *            value of template data type
     * @param language
     *            value of language
     * @param date
     *            searching date
     * @return entity
     */
    @Query("SELECT td FROM TemplateData td WHERE td.templateKey = :templateKey AND td.dataType = :dataType AND td.language = :language "
            + "AND ((:date BETWEEN td.validFrom AND td.validTo) OR (:date >= td.validFrom AND td.validTo IS NULL))")
    TemplateData find(@QueryParam("templateKey") String templateKey, @QueryParam("dataType") TemplateDataType dataType,
            @QueryParam("language") String language, @QueryParam("date") Date date);

    /**
     * Find Entity by PK
     * 
     * @param id
     *            Table PK
     * @return Table entity
     */
    TemplateData findBy(String id);
}
