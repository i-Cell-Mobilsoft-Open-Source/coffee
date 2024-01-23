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
package hu.icellmobilsoft.coffee.module.mp.restclient.exception;

import org.apache.commons.lang3.EnumUtils;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * FaultType parser.
 *
 * @author adam.magyari
 * @since 1.2.0
 */
public class FaultTypeParser {

    /**
     * Default constructor, constructs a new object.
     */
    private FaultTypeParser() {
        super();
    }

    /**
     * Parse fault type String into Enum. If none match returns
     * {@link CoffeeFaultType#OPERATION_FAILED}.
     *
     * @param faultTypeString
     *            {@link String} to parse.
     * @return The Enum value parsed from input string.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Enum<?> parseFaultType(String faultTypeString) {

        for (Class<? extends Enum> faultTypeClass : FaultTypeParserExtension.getFaultTypeClasses()) {
            Enum<?> fault = EnumUtils.getEnum(faultTypeClass, faultTypeString);
            if (fault != null) {
                return fault;
            }
        }
        Logger.getLogger(FaultTypeParser.class).warn("FaultType not exists in enum for messages, faultType: [" + faultTypeString + "]");
        return CoffeeFaultType.OPERATION_FAILED;
    }
}
