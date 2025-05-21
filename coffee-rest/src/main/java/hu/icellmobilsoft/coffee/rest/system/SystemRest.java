/*-
 * #%L
 * DookuG
 * %%
 * Copyright (C) 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.system;

import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import hu.icellmobilsoft.coffee.rest.action.versioninfo.VersionInfoAction;
import hu.icellmobilsoft.coffee.rest.rest.BaseRestService;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * System rest endpoint implementations
 *
 * @author tamas.cserhati
 * @since 2.11.0
 */
@Model
@Named("coffeeSystemRest")
public class SystemRest extends BaseRestService implements ISystemRest {

    @Inject
    private VersionInfoAction versionInfoAction;

    /**
     * Default constructor
     */
    public SystemRest() {
        // Default constructor for java 21
    }

    @Override
    public String versionInfo() throws BaseException {
        return versionInfoAction.versionInfo();
    }
}
