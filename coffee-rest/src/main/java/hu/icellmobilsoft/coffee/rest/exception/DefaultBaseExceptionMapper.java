/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.exception;

import java.util.Collection;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BONotFound;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.BusinessFault;
import hu.icellmobilsoft.coffee.dto.common.commonservice.InvalidRequestFault;
import hu.icellmobilsoft.coffee.dto.common.commonservice.TechnicalFault;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ValidationType;
import hu.icellmobilsoft.coffee.dto.exception.AccessDeniedException;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.OptimisticLockException;
import hu.icellmobilsoft.coffee.dto.exception.ServiceUnavailableException;
import hu.icellmobilsoft.coffee.dto.exception.XMLValidationError;
import hu.icellmobilsoft.coffee.rest.exception.enums.HttpStatus;
import hu.icellmobilsoft.coffee.rest.validation.xml.exception.XsdProcessingException;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.BusinessException;
import hu.icellmobilsoft.coffee.se.api.exception.DtoConversionException;

/**
 * Exception mapper for handled exception throwing
 *
 * @author balazs.joo
 * @since 1.8.0
 */
@Dependent
public class DefaultBaseExceptionMapper implements ExceptionMapper<BaseException> {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private IExceptionMessageTranslator exceptionMessageTranslator;

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultBaseExceptionMapper() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public final Response toResponse(BaseException e) {
        log.error("Known error: ", e);
        log.writeLogToError();

        return handleException(e);
    }

    /**
     * Kivétel kezelése
     *
     * @param e
     *            a kivétel
     * @return összeállított válasz
     */
    protected Response handleException(BaseException e) {
        if (e instanceof AccessDeniedException) {
            return createResponse(e, Response.Status.UNAUTHORIZED, new BusinessFault());
        } else if (e instanceof BONotFoundException) {
            return createResponse(e, IExceptionMessageTranslator.HTTP_STATUS_I_AM_A_TEAPOT, new BONotFound());
        } else if (e instanceof DtoConversionException) {
            return createResponse(e, Response.Status.BAD_REQUEST, new BusinessFault());
        } else if (e instanceof ServiceUnavailableException) {
            return createResponse(e, Response.Status.SERVICE_UNAVAILABLE, new BusinessFault());
        } else if (e instanceof XsdProcessingException) {
            XsdProcessingException xsdProcessingException = (XsdProcessingException) e;
            return createValidationErrorResponse(e, xsdProcessingException.getErrors());
        } else if (e instanceof BusinessException || e instanceof hu.icellmobilsoft.coffee.dto.exception.BusinessException) {
            return createResponse(e, HttpStatus.UNPROCESSABLE_ENTITY.getStatusCode(), new BusinessFault());
        } else if (e instanceof OptimisticLockException) {
            return createResponse(e, HttpStatus.OPTIMISTIC_LOCK.getStatusCode(), new TechnicalFault());
        } else {
            // BaseException/TechnicalException
            return createResponse(e, Response.Status.INTERNAL_SERVER_ERROR, new TechnicalFault());
        }
    }

    /**
     * Válasz létrehozása, mely tartalmazza a validációs hibákat
     *
     * @param e
     *            a kivétel
     * @param errors
     *            validációs hibák gyűjteménye
     * @return összeállított válasz
     */
    protected Response createValidationErrorResponse(BaseException e, Collection<XMLValidationError> errors) {
        InvalidRequestFault dto = new InvalidRequestFault();
        addValidationErrors(dto, errors);
        return createResponse(e, Response.Status.BAD_REQUEST, dto);
    }

    /**
     * Válasz létrehozása
     *
     * @param e
     *            a kivétel
     * @param status
     *            a válasznak átadni kívánt {@link Response.Status}
     * @param dto
     *            {@link BaseExceptionResultType} leszármazott, mely átadásra kerül a válaszban
     * @return összeállított válasz
     */
    protected Response createResponse(BaseException e, Response.Status status, BaseExceptionResultType dto) {
        return createResponse(e, status.getStatusCode(), dto);
    }

    /**
     * Válasz létrehozása
     *
     * @param e
     *            a kivétel
     * @param statusCode
     *            a válasznak átadni kívánt státusz kódja (pl.: 418)
     * @param dto
     *            {@link BaseExceptionResultType} leszármazott, mely átadásra kerül a válaszban
     * @return összeállított válasz
     */
    protected Response createResponse(BaseException e, int statusCode, BaseExceptionResultType dto) {
        exceptionMessageTranslator.addCommonInfo(dto, e, e.getFaultTypeEnum());
        return Response.status(statusCode).entity(dto).build();
    }

    private void addValidationErrors(InvalidRequestFault dto, Collection<XMLValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return;
        }
        for (XMLValidationError error : errors) {
            ValidationType valType = new ValidationType();
            valType.setError(error.getError());
            valType.setColumnNumber(error.getColumnNumber());
            valType.setLineNumber(error.getLineNumber());
            dto.withError(valType);
        }
    }
}
