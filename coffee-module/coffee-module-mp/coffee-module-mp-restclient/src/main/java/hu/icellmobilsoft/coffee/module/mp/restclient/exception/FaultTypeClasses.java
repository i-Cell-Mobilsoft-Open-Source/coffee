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
package hu.icellmobilsoft.coffee.module.mp.restclient.exception;

import java.util.List;

/**
 * External faultype classes, where extension is not work
 *
 * @since 2.6.0
 * @author speter555
 */
public interface FaultTypeClasses {

    /**
     * Getter of enum classes which contains faultType enums
     * 
     * @return Get enum classes which contains faultType enums
     */
    List<Class<? extends Enum>> getFaultTypeClasses();
}
