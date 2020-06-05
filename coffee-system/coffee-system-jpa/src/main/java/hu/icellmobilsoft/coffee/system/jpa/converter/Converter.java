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
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.model.base.AbstractEntity;

/**
 * Custom converter.
 *
 * @author karoly.tamas
 * @param <E>
 *            AbstractEntity
 * @param <D>
 *            AbstractDtoType
 * @since 1.0.0
 */
public interface Converter<E extends AbstractEntity, D extends AbstractDtoType> {

    /**
     * <p>convert.</p>
     */
    public D convert(E entity) throws BaseException;

    /**
     * <p>convert.</p>
     */
    public E convert(D dto) throws BaseException;

    /**
     * <p>convert.</p>
     */
    public void convert(D destinationDto, E sourceEntity) throws BaseException;

    /**
     * <p>convert.</p>
     */
    public void convert(E destinationEntity, D sourceDto) throws BaseException;
}
