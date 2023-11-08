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
 * Openapi filter, mely hozzáadja az általános response-okat leírásukkal, ezen kívül általános header elemeket lehet minden végponthoz definiálni.
 *
 * Azon Response leírások melyeket már a filterig jutásig tartalmaz az OpenApi leíró, nem lesznek felülírva!!!
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
     * Az mp-openapi a pattern-nel rendelkező, de nem String típusu objektumokat (pl.dátum típusok) object type-nak veszi, így nem generálja le a
     * pattern-t a definícióba.
     *
     * @param schema
     *            az aktuális séma elem
     * @return a módosított séma
     */
    @Override
    public Schema filterSchema(Schema schema) {
        if (schema != null && StringUtils.isNotBlank(schema.getPattern())) {
            // Ha van pattern, akkor request/response szinten string kell legyen (a dto-ban lehet object pl. XmlGregorianCalendar)
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
     * Az általános hibakód halmaz és hozzátartozó {@link APIResponse} leírók
     * 
     * @return Az általános hibakód halmaz és hozzátartozó {@link APIResponse} leírók
     */
    protected Map<Integer, APIResponse> getCommonApiResponseByStatusCodeMap() {
        return ApiResponseUtil.getCommonApiResponseByStatusCodeMap();
    }

    /**
     * Azon paraméterek, melyek minden rest végponton rajta kell hogy legyenek, pl nyelv meghatározás, api verzió stb.
     *
     * @return OpenApi Parameter lista mely az általános requestHeader leírását tartalmazza
     */
    protected List<Parameter> getCommonRequestHeaderParameters() {
        return Collections.emptyList();
    }

    private void addCommonResponseCodes(OpenAPI openAPI) {

        // NOTE Új modulhoz még nem feltétlen van végpont
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
        // NOTE Új modulhoz még nem feltétlen van végpont
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
