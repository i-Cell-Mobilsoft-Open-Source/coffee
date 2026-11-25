/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import hu.icellmobilsoft.coffee.dto.common.config.evict.EvictResponse;
import hu.icellmobilsoft.coffee.dto.url.BaseServicePath;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * REST endpoint for system eviction service functions.
 *
 * @author attila.kiss
 * @since 4.0.0
 */
@Path("")
public interface ISystemEvictRest {

    /**
     * Clears caches. The REST implementation supplies the eviction behavior.
     *
     * @return the evict result
     * @throws BaseException
     *             if any error occurs
     */
    @Tag(name = "Maintenance", description = "Clearing internal state")
    @GET
    @Path(BaseServicePath.SYSTEM_EVICT)
    @Operation(summary = "Clearing internal state", description = """
            Clears application configuration and invokes eviction on CDI beans and configuration sources
            implementing the hu.icellmobilsoft.coffee.dto.evict.Evictable interface.""")
    @Produces(value = { MediaType.TEXT_XML, MediaType.APPLICATION_XML })
    EvictResponse getEvict() throws BaseException;

}
