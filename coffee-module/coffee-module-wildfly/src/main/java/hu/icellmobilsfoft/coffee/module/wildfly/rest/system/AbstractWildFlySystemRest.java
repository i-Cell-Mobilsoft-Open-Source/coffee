/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsfoft.coffee.module.wildfly.rest.system;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import hu.icellmobilsoft.coffee.rest.system.AbstractSystemRest;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType;

/**
 * SystemRest endpoint for wildfly application server - war deployment
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 * @see AbstractSystemRest
 */
public abstract class AbstractWildFlySystemRest extends AbstractSystemRest {

    @Inject
    private ServletContext servletContext;

    /**
     * Default constructor
     */
    public AbstractWildFlySystemRest() {
        // Default constructor for java 21
    }

    @Override
    public String versionInfo() throws BaseException {
        try {
            InputStream inputStream = servletContext.getResourceAsStream(META_INF_MANIFEST_MF);
            StringBuilder version = new StringBuilder();
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    version.append(line);
                    version.append(NEW_LINE);
                }
            }
            return version.toString();
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }
}
