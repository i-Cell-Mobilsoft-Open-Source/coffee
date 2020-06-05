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
package hu.icellmobilsoft.coffee.dto.common.impl.common;

import java.io.Serializable;

import hu.icellmobilsoft.coffee.dto.common.BaseEntityDto;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;

/**
 * Main dto for service to service communication
 *
 * @author imre.scheffer
 * @param <E>
 *            entity
 * @since 1.0.0
 */
public class EntityDto<E extends Serializable> extends BaseEntityDto<E, ContextType> {

    private static final long serialVersionUID = 1L;
}
