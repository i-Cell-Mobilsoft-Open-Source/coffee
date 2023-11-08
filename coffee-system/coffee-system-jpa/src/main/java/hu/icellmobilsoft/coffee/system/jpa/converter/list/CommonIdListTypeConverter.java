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
package hu.icellmobilsoft.coffee.system.jpa.converter.list;

import java.util.List;

import jakarta.enterprise.context.Dependent;

import hu.icellmobilsoft.coffee.dto.common.common.CommonIdListType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.model.base.AbstractIdentifiedAuditEntity;

/**
 * {@link CommonIdListType} dto &lt;-&gt; {@link AbstractIdentifiedAuditEntity} list converter
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
@Dependent
public class CommonIdListTypeConverter {

    /**
     * Default constructor, constructs a new object.
     */
    public CommonIdListTypeConverter() {
        super();
    }

    /**
     * Convert entity list to XML specific ID list type
     *
     * @param <T>
     *            entity class
     * @param sourceEntities
     *            list of entity
     * @return CommonIdListType filled with entity.id
     * @throws BaseException
     *             exception
     */
    public <T extends AbstractIdentifiedAuditEntity> CommonIdListType convert(List<T> sourceEntities) throws BaseException {
        CommonIdListType commonIdListType = new CommonIdListType();
        convert(commonIdListType, sourceEntities);
        return commonIdListType;
    }

    /**
     * Convert entity list to XML specific ID list type
     *
     * @param <T>
     *            entity class
     * @param commonIdListType
     *            exist XML id list type class
     * @param sourceEntities
     *            list of entity
     * @throws BaseException
     *             exception
     */
    public <T extends AbstractIdentifiedAuditEntity> void convert(CommonIdListType commonIdListType, List<T> sourceEntities) throws BaseException {
        for (T entity : sourceEntities) {
            commonIdListType.getId().add(entity.getId());
        }
    }
}
