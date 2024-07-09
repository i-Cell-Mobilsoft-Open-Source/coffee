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
package hu.icellmobilsoft.coffee.rest.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;

import hu.icellmobilsoft.coffee.rest.filter.util.ApiResponseUtil;

/**
 * An OpenAPI filter that adds general responses with their descriptions. Additionally, it allows defining common header elements for every endpoint.
 *
 * The response descriptions already included in the OpenAPI descriptor up to the point of filtering will not be overwritten.
 *
 * @author speter555
 * @since 1.11.0
 */
public class OpenAPIFilter implements OASFilter {

    /**
     * Default constructor, constructs a new object.
     */
    public OpenAPIFilter() {
        super();
    }

    /**
     * The MP-OpenAPI treats objects with patterns (e.g., date types) as object types rather than string types,
     * so it does not generate the pattern in the definition.
     *
     * @param schema
     *            the current schema element
     * @return the modified schema
     */
    @Override
    public Schema filterSchema(Schema schema) {
        if (schema != null && StringUtils.isNotBlank(schema.getPattern())) {
            // If there is a pattern, then at the request/response level it should be a string (even if the DTO has an object like XmlGregorianCalendar)
            schema.setType(Schema.SchemaType.STRING);
        }
        return schema;
    }

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        if (openAPI != null) {
            addCommonRequestHeaderParameters(openAPI);
            addCommonResponseCodes(openAPI);
        }
    }

    /**
     * The general set of error codes and corresponding {@link APIResponse} descriptions
     * 
     * @return The general error code set and corresponding {@link APIResponse} descriptions
     */
    protected Map<Integer, APIResponse> getCommonApiResponseByStatusCodeMap() {
        return ApiResponseUtil.getCommonApiResponseByStatusCodeMap();
    }

    /**
     * Parameters that should be present on every REST endpoint, such as language definition, API version, etc
     *
     * @return OpenAPI parameter list containing the description of general request headers
     */
    protected List<Parameter> getCommonRequestHeaderParameters() {
        return Collections.emptyList();
    }

    private void addCommonResponseCodes(OpenAPI openAPI) {

        // NOTE The new module may not necessarily have endpoints yet.
        if (isOpenapiPathEmpty(openAPI)) {
            return;
        }

        openAPI.getPaths().getPathItems().forEach((pathName, pathItem) -> pathItem.getOperations().values().forEach(operation -> {
            APIResponses apiResponses = operation.getResponses();
            getCommonApiResponseByStatusCodeMap()
                    .forEach((statusCode, apiResponseValue) -> addApiResponse(apiResponses, Integer.toString(statusCode), apiResponseValue));
        }));
    }

    private void addApiResponse(APIResponses apiResponses, String statusCode, APIResponse apiResponse) {
        if (!apiResponses.getAPIResponses().containsKey(statusCode)) {
            apiResponses.addAPIResponse(statusCode, apiResponse);
        }
    }

    private void addCommonRequestHeaderParameters(OpenAPI openAPI) {
        // NOTE The new module may not necessarily have endpoints yet.
        if (isOpenapiPathEmpty(openAPI) || getCommonRequestHeaderParameters() == null || getCommonRequestHeaderParameters().isEmpty()) {
            return;
        }

        openAPI.getPaths().getPathItems().forEach( //
                (pathName, pathItem) -> pathItem.getOperations().values().forEach( //
                        operation -> getCommonRequestHeaderParameters().forEach(operation::addParameter) //
                ) //
        );
    }

    private boolean isOpenapiPathEmpty(OpenAPI openAPI) {
        return Objects.isNull(openAPI.getPaths()) || Objects.isNull(openAPI.getPaths().getPathItems()) || openAPI.getPaths().getPathItems().isEmpty();
    }
}
