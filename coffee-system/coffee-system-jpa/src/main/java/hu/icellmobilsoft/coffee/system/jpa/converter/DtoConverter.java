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

import hu.icellmobilsoft.coffee.dto.common.common.AbstractDtoType;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.DtoConversionException;
import hu.icellmobilsoft.coffee.model.base.AbstractEntity;

/**
 * Basic converter {@link AbstractEntity} &lt;-&gt; {@link AbstractDtoType}
 *
 * @author karoly.tamas
 * @param <E>
 *            {@link AbstractEntity}
 * @param <D>
 *            {@link AbstractDtoType}
 * @since 1.0.0
 */
public abstract class DtoConverter<E extends AbstractEntity, D extends AbstractDtoType> implements Converter<E, D> {

    /**
     * Default constructor, constructs a new object.
     */
    public DtoConverter() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void convert(D destinationDto, E sourceEntity) throws BaseException {
        validate(destinationDto, sourceEntity);
        destinationDto.setVersion(sourceEntity.getVersion());
    }

    /** {@inheritDoc} */
    @Override
    public void convert(E destinationEntity, D sourceDto) throws BaseException {
        // NOTE Do not modify version data manually.
    }

    /**
     * validate
     * 
     * @param dto
     *            dto
     * @param entity
     *            entity
     * @throws DtoConversionException
     *             exception
     */
    public void validate(D dto, E entity) throws DtoConversionException {
        validate(dto);
        validate(entity);
    }

    /**
     * validate
     * 
     * @param dto
     *            dto
     * @throws DtoConversionException
     *             exception
     */
    public void validate(D dto) throws DtoConversionException {
        if (dto == null) {
            throw new DtoConversionException("Unable to convert, AbtractDto is null!");
        }
    }

    /**
     * validate
     * 
     * @param entity
     *            entity
     * @throws DtoConversionException
     *             exception
     */
    public void validate(E entity) throws DtoConversionException {
        if (entity == null) {
            throw new DtoConversionException("Unable to convert, AbstractEntity is null!");
        }
    }
}
