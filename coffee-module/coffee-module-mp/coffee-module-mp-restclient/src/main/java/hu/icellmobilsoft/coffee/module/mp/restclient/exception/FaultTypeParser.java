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

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.apache.commons.lang3.EnumUtils;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.dto.fault.provider.spi.IFaultTypeProvider;
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
     * Parse fault type String into an implementation of {@link hu.icellmobilsoft.coffee.dto.error.IFaultType}. If none match returns
     * {@link CoffeeFaultType#OPERATION_FAILED}.
     *
     * @param faultTypeString
     *            {@link String} to parse.
     * @return {@link hu.icellmobilsoft.coffee.dto.error.IFaultType} parsed from input string.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Enum<?> parseFaultType(String faultTypeString) {
        Instance<IFaultTypeProvider> faultProviderInstance = CDI.current().select(IFaultTypeProvider.class);
        IFaultTypeProvider provider = null;
        if (faultProviderInstance.isResolvable()) {
            try {
                provider = faultProviderInstance.get();
                for (Class<? extends Enum> faultTypeClass : provider.faultTypeEnums()) {
                    Enum<?> fault = EnumUtils.getEnum(faultTypeClass, faultTypeString);
                    if (fault != null) {
                        return fault;
                    }
                }
            } finally {
                if (provider != null) {
                    faultProviderInstance.destroy(provider);
                }
            }
        }
        // default coffee
        Enum<?> fault = EnumUtils.getEnum(CoffeeFaultType.class, faultTypeString);
        if (fault != null) {
            return fault;
        }
        Logger.getLogger(FaultTypeParser.class)
                .warn("FaultType not exists in enum for messages, faultType: [{0}] -> [{1}]", faultTypeString, CoffeeFaultType.OPERATION_FAILED);
        return CoffeeFaultType.OPERATION_FAILED;
    }
}
