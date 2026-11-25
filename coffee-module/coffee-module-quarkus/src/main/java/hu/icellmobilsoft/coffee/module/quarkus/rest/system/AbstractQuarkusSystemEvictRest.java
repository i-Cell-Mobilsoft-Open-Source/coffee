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
package hu.icellmobilsoft.coffee.module.quarkus.rest.system;

import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.dto.common.config.evict.EvictResponse;
import hu.icellmobilsoft.coffee.rest.action.evict.EvictAction;
import hu.icellmobilsoft.coffee.rest.rest.BaseRestService;
import hu.icellmobilsoft.coffee.rest.system.ISystemEvictRest;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * System Evict Rest endpoint for quarkus
 *
 * @author tamas.cserhati
 * @author attila.kiss
 * @since 4.0.0
 */
public abstract class AbstractQuarkusSystemEvictRest extends BaseRestService implements ISystemEvictRest {

    @Inject
    EvictAction evictAction;

    /**
     * Default constructor
     */
    public AbstractQuarkusSystemEvictRest() {
        // Default constructor for java 21
    }

    /**
     * Clears caches using the CDI-selected eviction action.
     *
     * @return the eviction result
     * @throws BaseException
     *             if eviction fails
     * @since 4.0.0
     */
    @Override
    public EvictResponse getEvict() throws BaseException {
        return wrapNoParam(evictAction::evict, "evict");
    }

}
