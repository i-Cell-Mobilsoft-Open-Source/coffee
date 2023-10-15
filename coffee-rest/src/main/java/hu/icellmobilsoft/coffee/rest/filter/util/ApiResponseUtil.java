/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.filter.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.enterprise.inject.Vetoed;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

/**
 * ApiResponse util, mely visszaadja a http response kódokat és a hozzájuk tartozó {@link APIResponse} objektumokat, melyek a projektben álltalánosak.
 *
 * @since 1.11.0
 * @author speter555
 */
@Vetoed
public class ApiResponseUtil {

    private static Map<Integer, APIResponse> apiResponseByStatusCodeMap;
    private static final String INVALIDREQUESTFAULTTYPE_REF = "#/components/schemas/InvalidRequestFault";
    private static final String BUSINESSFAULTTYPE_REF = "#/components/schemas/BusinessFault";
    private static final String TECHNICALFAULTTYPE_REF = "#/components/schemas/TechnicalFault";
    private static final String BONOTFOUNDTYPE_REF = "#/components/schemas/BONotFound";

    private ApiResponseUtil() {
    }

    /**
     * Get general ApiResponses by StatusCodes in Map structure
     *
     * @return status code and ApiResponse key-value Map
     */
    public static Map<Integer, APIResponse> getCommonApiResponseByStatusCodeMap() {
        if (Objects.isNull(apiResponseByStatusCodeMap)) {
            init();
        }
        return apiResponseByStatusCodeMap;
    }

    /**
     * Init status code and ApiResponse key-value Map
     */
    private static void init() {
        apiResponseByStatusCodeMap = new HashMap<>();
        apiResponseByStatusCodeMap.put( //
                Response.Status.OK.getStatusCode(), //
                OASFactory.createAPIResponse() //
                        .description(Response.Status.OK.getReasonPhrase() //
                                + "\n* the request was successfully " //
                                + "\n\t * **resultCode** = *OK*"));

        apiResponseByStatusCodeMap.put( //
                Response.Status.BAD_REQUEST.getStatusCode(), //
                OASFactory.createAPIResponse() //
                        .content(OASFactory.createContent()
                                .addMediaType(MediaType.APPLICATION_JSON,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(INVALIDREQUESTFAULTTYPE_REF)))
                                .addMediaType(MediaType.APPLICATION_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(INVALIDREQUESTFAULTTYPE_REF)))
                                .addMediaType(MediaType.TEXT_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(INVALIDREQUESTFAULTTYPE_REF))))
                        .description(Response.Status.BAD_REQUEST.getReasonPhrase() //
                                + "\n" + "* Invalid request" //
                                + "\n\t * **resultCode** = *OPERATION_FAILED*" //
                                + "\n\t * **resultCode** = *WRONG_OR_MISSING_PARAMETERS*" //
                                + "\n\t * **resultCode** = *INVALID_INPUT*" //
                                + "\n\t * **resultCode** = *INVALID_INPUT*" //
                                + "\n\t * **resultCode** = *OPERATION_FAILED*" //
                        ));

        apiResponseByStatusCodeMap.put( //
                Response.Status.UNAUTHORIZED.getStatusCode(), //
                OASFactory.createAPIResponse() //
                        .content(OASFactory.createContent()
                                .addMediaType(MediaType.APPLICATION_JSON,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BUSINESSFAULTTYPE_REF)))
                                .addMediaType(MediaType.APPLICATION_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BUSINESSFAULTTYPE_REF)))
                                .addMediaType(MediaType.TEXT_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BUSINESSFAULTTYPE_REF))))
                        .description(Response.Status.UNAUTHORIZED.getReasonPhrase() //
                                + "\n" + "* You are not authorized to perform the operation" //
                                + "\n\t * **resultCode** = *NOT_AUTHORIZED*"));

        apiResponseByStatusCodeMap.put( //
                Response.Status.FORBIDDEN.getStatusCode(), //
                OASFactory.createAPIResponse() //
                        .content(OASFactory.createContent()
                                .addMediaType(MediaType.APPLICATION_JSON,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(TECHNICALFAULTTYPE_REF)))
                                .addMediaType(MediaType.APPLICATION_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(TECHNICALFAULTTYPE_REF)))
                                .addMediaType(MediaType.TEXT_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(TECHNICALFAULTTYPE_REF))))
                        .description(Response.Status.FORBIDDEN.getReasonPhrase() //
                                + "\n" + "* An access to a resource requested by a client has been forbidden by the server." //
                                + "\n\t * **resultCode** = *NOT_AUTHORIZED*"));

        apiResponseByStatusCodeMap.put( //
                418, //
                OASFactory.createAPIResponse() //
                        .content(OASFactory.createContent()
                                .addMediaType(MediaType.APPLICATION_JSON,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BONOTFOUNDTYPE_REF)))
                                .addMediaType(MediaType.APPLICATION_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BONOTFOUNDTYPE_REF)))
                                .addMediaType(MediaType.TEXT_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BONOTFOUNDTYPE_REF))))
                        .description("Unique http response code" //
                                + "\n" + "* Looked for entity is not found." //
                                + "\n\t * **resultCode** = *ENTITY_NOT_FOUND*"));

        apiResponseByStatusCodeMap.put( //
                Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), //
                OASFactory.createAPIResponse() //
                        .content(OASFactory.createContent()
                                .addMediaType(MediaType.APPLICATION_JSON,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BUSINESSFAULTTYPE_REF)))
                                .addMediaType(MediaType.APPLICATION_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BUSINESSFAULTTYPE_REF)))
                                .addMediaType(MediaType.TEXT_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(BUSINESSFAULTTYPE_REF))))
                        .description(Response.Status.SERVICE_UNAVAILABLE.getReasonPhrase() //
                                + "\n" + "* The REST endpoint is down by configuration" //
                                + "\n\t * **resultCode** = *SERVICE_UNAVAILABLE*"));

        apiResponseByStatusCodeMap.put( //
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), //
                OASFactory.createAPIResponse() //
                        .content(OASFactory.createContent()
                                .addMediaType(MediaType.APPLICATION_JSON,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(TECHNICALFAULTTYPE_REF)))
                                .addMediaType(MediaType.APPLICATION_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(TECHNICALFAULTTYPE_REF)))
                                .addMediaType(MediaType.TEXT_XML,
                                        OASFactory.createMediaType().schema(OASFactory.createSchema().ref(TECHNICALFAULTTYPE_REF))))
                        .description(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase() //
                                + "\n" + "* Technical error" //
                                + "\n\t+ **resultCode** = *OPERATION_FAILED* (default)" //
                                + "\n\t * **resultCode** = *NOT_ACCEPTABLE_EXCEPTION*" //
                                + "\n\t * **resultCode** = *REPOSITORY_FAILED*" //
                                + "\n\t * **resultCode** = *NOT_ALLOWED_EXCEPTION*" //
                                + "\n\t * **resultCode** = *ILLEGAL_ARGUMENT_EXCEPTION*" //
                                + "\n\t * **resultCode** = *GENERIC_EXCEPTION*" //
                                + "\n\t * **resultCode** = *OPTIMISTIC_LOCK_EXCEPTION*" //
                                + "\n\t * **resultCode** = *unique see in rest endpoint description*"));
    }

}
