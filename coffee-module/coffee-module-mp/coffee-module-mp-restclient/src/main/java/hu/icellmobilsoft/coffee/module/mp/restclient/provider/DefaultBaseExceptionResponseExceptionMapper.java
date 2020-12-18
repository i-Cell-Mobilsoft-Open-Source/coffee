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
package hu.icellmobilsoft.coffee.module.mp.restclient.provider;

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.exception.AccessDeniedException;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.mp.restclient.exception.FaultTypeParser;
import hu.icellmobilsoft.coffee.module.mp.restclient.exception.RestClientResponseException;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.marshalling.MarshallingUtil;

/**
 * Default BaseException ResponseExceptionMapper. Maps HTTP error responses to {@link BaseException}.
 *
 * @author adam.magyari
 * @since 1.2.0
 */
public class DefaultBaseExceptionResponseExceptionMapper implements ResponseExceptionMapper<BaseException> {
    public static final int HTTP_STATUS_I_AM_A_TEAPOT = 418;

    @Override
    public BaseException toThrowable(Response response) {
        int responseStatus = response.getStatus();

        BaseExceptionResultType dto = readEntity(response, BaseExceptionResultType.class);
        if (dto != null) {
            if (responseStatus == HTTP_STATUS_I_AM_A_TEAPOT) {
                return new BONotFoundException(FaultTypeParser.parseFaultType(dto.getFaultType()), dto.getMessage(),
                        RestClientResponseException.fromExceptionResult(dto));
            } else if (responseStatus == Response.Status.UNAUTHORIZED.getStatusCode()) {
                return new AccessDeniedException(FaultTypeParser.parseFaultType(dto.getFaultType()), dto.getMessage(),
                        RestClientResponseException.fromExceptionResult(dto));
            } else if (responseStatus == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
                return new BaseException(FaultTypeParser.parseFaultType(dto.getFaultType()), dto.getMessage(),
                        RestClientResponseException.fromExceptionResult(dto));
            }
        }
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                MessageFormat.format("HTTP error status [{0}], content [{1}]", responseStatus, response.readEntity(String.class)));
    }

    protected <T> T readEntity(Response response, Class<T> dtoClass) {
        String entity = response.readEntity(String.class);
        if (StringUtils.isBlank(entity)) {
            return null;
        }
        MediaType mediaType = response.getMediaType();
        if (StringUtils.equalsAnyIgnoreCase(mediaType.getSubtype(), "json")) {
            return JsonUtil.toObject(entity, dtoClass);
        } else if (StringUtils.equalsAnyIgnoreCase(mediaType.getSubtype(), "xml")) {
            return MarshallingUtil.unmarshallUncheckedXml(entity, dtoClass);
        }
        return null;
    }
}
