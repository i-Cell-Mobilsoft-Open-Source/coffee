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

import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.AccessDeniedException;
import hu.icellmobilsoft.coffee.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.RestClientResponseException;
import hu.icellmobilsoft.coffee.exception.TechnicalException;
import hu.icellmobilsoft.coffee.module.mp.restclient.exception.FaultTypeParser;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.marshalling.MarshallingUtil;

/**
 * Default BaseException ResponseExceptionMapper. Maps HTTP error responses to {@link BaseException}.
 *
 * @author adam.magyari
 * @since 1.2.0
 */
@Dependent
public class DefaultBaseExceptionResponseExceptionMapper implements ResponseExceptionMapper<BaseException> {
    /**
     * <a href="https://datatracker.ietf.org/doc/html/rfc2324#section-2.3.2">RFC 2324 - section 2.3.2</a>
     */
    public static final int HTTP_STATUS_I_AM_A_TEAPOT = 418;

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultBaseExceptionResponseExceptionMapper() {
        super();
    }

    @Override
    public BaseException toThrowable(Response response) {
        int responseStatus = response.getStatus();

        BaseExceptionResultType dto = readEntity(response, BaseExceptionResultType.class);
        if (dto != null) {
            var restClientResponseException = new RestClientResponseException(
                    MessageFormat.format("REST client exception! ResponseStatus [{0}].", responseStatus), fromExceptionResult(dto, responseStatus));
            restClientResponseException.setClassName(dto.getClassName());
            restClientResponseException.setException(dto.getException());
            restClientResponseException.setService(dto.getService());
            restClientResponseException.setStatusCode(responseStatus);
            return restClientResponseException;
        }
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED,
                MessageFormat.format("HTTP error status [{0}], content [{1}]", responseStatus, response.readEntity(String.class)));
    }

    /**
     * Tries to unmarshall the response entity to a given class
     * 
     * @param response
     *            the HTTP response
     * @param dtoClass
     *            the expected response entity class
     * @param <T>
     *            dto class type
     * @return the unmarshalled response entity
     */
    protected <T> T readEntity(Response response, Class<T> dtoClass) {
        String entity = response.readEntity(String.class);
        if (StringUtils.isBlank(entity)) {
            return null;
        }
        MediaType mediaType = response.getMediaType();
        if (mediaType == null) {
            return null;
        }
        if (StringUtils.equalsAnyIgnoreCase(mediaType.getSubtype(), "json")) {
            return JsonUtil.toObject(entity, dtoClass);
        } else if (StringUtils.equalsAnyIgnoreCase(mediaType.getSubtype(), "xml")) {
            return MarshallingUtil.unmarshallUncheckedXml(entity, dtoClass);
        } else if (mediaType.equals(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
            if (StringUtils.startsWith(entity, "{")) {
                return JsonUtil.toObject(entity, dtoClass);
            } else if (StringUtils.startsWith(entity, "<")) {
                return MarshallingUtil.unmarshallUncheckedXml(entity, dtoClass);
            } else {
                return null;
            }
        }
        return null;
    }

    private BaseException fromExceptionResult(BaseExceptionResultType baseExceptionResultType, int responseStatus) {
        if (baseExceptionResultType == null) {
            return null;
        }

        if (responseStatus == HTTP_STATUS_I_AM_A_TEAPOT) {
            return new BONotFoundException(FaultTypeParser.parseFaultType(baseExceptionResultType.getFaultType()),
                    concatExceptionMessage(baseExceptionResultType), fromExceptionResultCausedBy(baseExceptionResultType.getCausedBy()));
        } else if (responseStatus == Response.Status.UNAUTHORIZED.getStatusCode()) {
            return new AccessDeniedException(FaultTypeParser.parseFaultType(baseExceptionResultType.getFaultType()),
                    concatExceptionMessage(baseExceptionResultType), fromExceptionResultCausedBy(baseExceptionResultType.getCausedBy()));
        } else {
            return new BaseException(FaultTypeParser.parseFaultType(baseExceptionResultType.getFaultType()),
                    concatExceptionMessage(baseExceptionResultType), fromExceptionResultCausedBy(baseExceptionResultType.getCausedBy()));
        }
    }

    private BaseException fromExceptionResultCausedBy(BaseExceptionResultType baseExceptionResultType) {
        if (baseExceptionResultType == null) {
            return null;
        }

        return new BaseException(FaultTypeParser.parseFaultType(baseExceptionResultType.getFaultType()),
                concatExceptionMessage(baseExceptionResultType), fromExceptionResultCausedBy(baseExceptionResultType.getCausedBy()));

    }

    private String concatExceptionMessage(BaseExceptionResultType baseExceptionResultType) {
        return String.join(" : ", baseExceptionResultType.getClassName(), baseExceptionResultType.getException());
    }
}
