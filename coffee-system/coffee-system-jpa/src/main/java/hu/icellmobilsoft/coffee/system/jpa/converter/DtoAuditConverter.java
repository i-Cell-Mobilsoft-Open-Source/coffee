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
package hu.icellmobilsoft.coffee.system.jpa.converter;

import hu.icellmobilsoft.coffee.dto.common.common.AbstractAuditDtoType;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.model.base.javatime.AbstractAuditEntity;

/**
 * Basic converter {@link AbstractAuditEntity} &lt;-&gt; {@link AbstractAuditDtoType}
 *
 * @author karoly.tamas
 * @param <E>
 *            {@link AbstractAuditEntity}
 * @param <D>
 *            {@link AbstractAuditDtoType}
 * @since 1.0.0
 */
public abstract class DtoAuditConverter<E extends AbstractAuditEntity<String>, D extends AbstractAuditDtoType> extends DtoConverter<E, D> {

    /**
     * Default constructor, constructs a new object.
     */
    public DtoAuditConverter() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void convert(D destinationDto, E sourceEntity) throws BaseException {
        validate(destinationDto, sourceEntity);
        super.convert(destinationDto, sourceEntity);
        destinationDto.setCreationDate(sourceEntity.getCreationDate());
        destinationDto.setCreatorUser(sourceEntity.getCreatorUser());
        destinationDto.setModificationDate(sourceEntity.getModificationDate());
        destinationDto.setModifierUser(sourceEntity.getModifierUser());
    }

    /** {@inheritDoc} */
    @Override
    public void convert(E destinationEntity, D sourceDto) throws BaseException {
        // NOTE Do not modify audit data manually.
    }
}
