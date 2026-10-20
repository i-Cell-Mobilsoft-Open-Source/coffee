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

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import hu.icellmobilsfoft.coffee.module.wildfly.util.VersionInfoUtil;
import hu.icellmobilsoft.coffee.rest.rest.BaseRestService;
import hu.icellmobilsoft.coffee.rest.system.ISystemRest;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType;

/**
 * SystemRest endpoint for wildfly application server - war deployment
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 */
public abstract class AbstractWildFlySystemRest extends BaseRestService implements ISystemRest {

    @Inject
    private ServletContext servletContext;

    /**
     * Default constructor
     */
    public AbstractWildFlySystemRest() {
        // Default constructor for java 21
    }

    /**
     * Reads the WAR manifest for the deprecated REST endpoint and Java callers.
     *
     * @return the manifest contents
     * @throws BaseException
     *             if the manifest cannot be read
     * @deprecated use the {@code /version} management endpoint
     */
    @Deprecated(since = "3.0.0")
    @Override
    public String versionInfo() throws BaseException {
        try {
            return VersionInfoUtil.getVersionInfo(servletContext);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }
}
