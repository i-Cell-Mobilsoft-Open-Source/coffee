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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import hu.icellmobilsoft.coffee.dto.common.config.evict.EvictResponse;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.dto.url.BaseServicePath;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;

/**
 * REST endpoint for system service functions.
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 */
@Path("")
public interface ISystemRest {

    /**
     * META-INF/MANIFEST.MF content in Rest response
     * 
     * @return META-INF/MANIFEST.MF content in text format
     * @throws BaseException
     *             if any error occurs
     */
    @Operation(hidden = true)
    @GET
    @Path(BaseServicePath.VERSION_INFO)
    @Produces(MediaType.TEXT_PLAIN)
    String versionInfo() throws BaseException;

    /**
     * Clear caches
     * 
     * @return the evict result
     * @throws BaseException
     *             if any error occurs
     */
    @Tag(name = "Maintenance", description = "Clearing internal state")
    @GET
    @Operation(summary = "Clearing internal state", description = """
            Iterates over implementations of the hu.icellmobilsoft.dookug.common.core.evictable.Evictable interface,
            and explicitly calls the clear function for known framework-level services.
            If there’s a change at runtime in the content of a template stored in the TEMPLATE_PART_CONTENT
            database table, this endpoint must be called to apply the changes, since the module caches this data.""")
    @Path(BaseServicePath.SYSTEM_EVICT)
    @Produces(value = { MediaType.TEXT_XML, MediaType.APPLICATION_XML })
    default EvictResponse getEvict() throws BaseException {
        throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Evict not implemented");
    }

}
