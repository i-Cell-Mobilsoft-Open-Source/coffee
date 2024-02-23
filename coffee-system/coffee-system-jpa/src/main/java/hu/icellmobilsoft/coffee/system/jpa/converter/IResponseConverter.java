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

import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.DtoConversionException;
import hu.icellmobilsoft.coffee.model.base.AbstractEntity;

/**
 * In case of response we cannot be able to convert the response back to {@link jakarta.persistence.Entity} it will throw {@link DtoConversionException}
 * by default
 *
 * @param <E>
 *            ENTITY
 * @param <D>
 *            DTO
 * @author imre.scheffer
 * @see IEntityConverter
 * @see IRequestConverter
 * @since 1.0.0
 */
public interface IResponseConverter<E extends AbstractEntity, D> extends IEntityConverter<E, D> {

    /** Constant <code>ERROR_NOT_IMPLEMENTED="Not implemented yet"</code> */
    String ERROR_NOT_IMPLEMENTED = "Not implemented yet";
    /** Constant <code>ERROR_DEST_ENTITY_NULL="Destination entity is null"</code> */
    String ERROR_DEST_ENTITY_NULL = "Destination entity is null";

    /** {@inheritDoc} */
    @Override
    default E convert(D d) throws BaseException {
        throw new DtoConversionException(ERROR_NOT_IMPLEMENTED);
    }

    /** {@inheritDoc} */
    @Override
    default void convert(E destinationEntity, D sourceDto) throws BaseException {
        throw new DtoConversionException(ERROR_NOT_IMPLEMENTED);
    }
}
