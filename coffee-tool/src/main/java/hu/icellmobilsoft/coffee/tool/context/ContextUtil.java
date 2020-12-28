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
package hu.icellmobilsoft.coffee.tool.context;

import javax.enterprise.inject.Vetoed;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * ContextUtil class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class ContextUtil {

    /**
     * Retrieves named object.
     *
     * @param jndi
     *            the name of the object to look up
     * @param clazz
     *            class of returned object
     * @param <T>
     *            type of returned object
     * @return the object bound to {@code jndi}
     * @throws BaseException
     *             exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T doLookup(String jndi, Class<T> clazz) throws BaseException {
        if (StringUtils.isBlank(jndi)) {
            return null;
        }
        if (clazz == null) {
            throw new TechnicalException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, "clazz cant be null!");
        }
        try {
            return (T) InitialContext.doLookup(jndi);
        } catch (NamingException e) {
            throw new TechnicalException(CoffeeFaultType.INVALID_REQUEST,
                    "Exception in context lookup for [" + jndi + "]: [" + e.getLocalizedMessage() + "]", e);
        }
    }
}
