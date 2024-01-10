/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.dto.fault.provider.spi;

import java.util.Collection;

/**
 * FaultType provider
 * 
 * @author tamas.cserhati
 * @since 2.5.0
 */
public interface IFaultTypeProvider {

    /**
     * return the project FaultType class(es)
     * 
     * @return List of FaultType enums
     */
    Collection<Class<? extends Enum>> faultTypeEnums();
}
