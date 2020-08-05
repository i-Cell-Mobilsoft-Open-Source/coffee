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
package hu.icellmobilsoft.coffee.rest.exception;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.UnmarshalException;

import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.jboss.resteasy.spi.InternalServerErrorException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.common.commonservice.TechnicalFault;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.BaseExceptionWrapper;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.log.RequestResponseLogger;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * Exception mapper for non-handled exception throwing
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class DefaultGeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private ProjectStage projectStage;

    @Context
    private Providers providers;

    @Inject
    private RequestResponseLogger requestResponseLogger;

    @Inject
    private IExceptionMessageTranslator exceptionMessageTranslator;

    /** {@inheritDoc} */
    @Override
    public Response toResponse(Exception e) {
        Response result = null;
        if (e instanceof BaseExceptionWrapper<?>) {
            BaseExceptionWrapper<?> wrappedException = (BaseExceptionWrapper<?>) e;
            if (wrappedException.getException() != null) {
                log.info("Wrapped exception. Trying to match the correct mapper...");
                result = handleWrappedException(wrappedException.getException());
            } else if (e.getCause() instanceof BaseException) {
                log.info("Wrapped BaseException. Trying to match the correct mapper...");
                result = handleWrappedException((BaseException) e.getCause());
            } else {
                log.error("Unknown error in cause: ", e);
                log.writeLogToError();
            }
        } else if (e instanceof NotAuthorizedException || e instanceof ForbiddenException) {
            log.error("Known error: ", e);
            log.writeLogToError();
            result = handleException(e);
        } else {
            log.error("Unknown error: ", e);
            log.writeLogToError();
        }
        return (result != null) ? result : handleException(e);
    }

    /**
     * Handle exception.
     *
     * @param e
     *            the exception
     * @return the response
     */
    protected Response handleException(Exception e) {

        TechnicalFault dto = new TechnicalFault();
        exceptionMessageTranslator.addCommonInfo(dto, e, CoffeeFaultType.OPERATION_FAILED);

        Response.Status statusCode = Response.Status.INTERNAL_SERVER_ERROR;
        if (e instanceof InternalServerErrorException) {
            statusCode = Response.Status.BAD_REQUEST;
        } else if (e instanceof NotAuthorizedException) {
            statusCode = Response.Status.UNAUTHORIZED;
        } else if (e instanceof ForbiddenException) {
            statusCode = Response.Status.FORBIDDEN;
        }
        ResponseBuilder responseBuilder = Response.status(statusCode);
        boolean productionStage = ProjectStage.Production.equals(projectStage);
        if (productionStage) {
            handleProductionStageException(dto, responseBuilder, e);
        }
        return responseBuilder.entity(dto).build();
    }

    /**
     * Unhandled Exception -> Error message converter
     *
     * @param dto
     *            DTO to fill with message
     * @param responseBuilder
     *            response builder
     * @param e
     *            throwe exception to handling
     */
    protected void handleProductionStageException(TechnicalFault dto, ResponseBuilder responseBuilder, Exception e) {
        if (e instanceof NotAcceptableException) {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.NOT_ACCEPTABLE_EXCEPTION));
        } else if (e instanceof NotAllowedException) {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.NOT_ALLOWED_EXCEPTION));
        } else if (e instanceof NotAuthorizedException) {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.NOT_AUTHORIZED));
        } else if (e instanceof ForbiddenException) {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.FORBIDDEN));
        } else if (e instanceof UnmarshalException) {
            dto.setMessage(maskSensitiveData(exceptionMessageTranslator.getLinkedExceptionLocalizedMessage((UnmarshalException) e)));
        } else if (e instanceof InternalServerErrorException) {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.INVALID_REQUEST));
        } else if (e.getCause() instanceof IllegalArgumentException) {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.ILLEGAL_ARGUMENT_EXCEPTION));
        } else if (e instanceof ClientErrorException) {
            // NotFoundException, NotSupportedException, ...
            handleRequestProcess();
            // a kérésben megadott accept lesz a válasz application/octet-stream helyett
            String accept = servletRequest.getHeader(HttpHeaders.ACCEPT);
            responseBuilder.type(MediaType.valueOf(StringUtils.defaultString(accept, MediaType.APPLICATION_XML)));
            dto.setMessage(maskSensitiveData(e.getLocalizedMessage()));
        } else {
            dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(CoffeeFaultType.GENERIC_EXCEPTION));
        }
        dto.setException(null);
    }

    /**
     * Handle request process.
     */
    protected void handleRequestProcess() {
        // MDC ezekben az esetekben nem volt tisztitva, mert nem is volt request loggolas
        MDC.clear();
        // feltoltjuk a szokasos adatokkal
        try {
            String applicationName = InitialContext.doLookup("java:app/AppName");
            MDC.put(LogConstants.LOG_SERVICE_NAME, applicationName);
        } catch (NamingException e) {
            log.warn("Error in getting JNDI value of 'java:app/AppName'", e);
        }
        String sessionId = servletRequest.getHeader(LogConstants.LOG_SESSION_ID);
        MDC.put(LogConstants.LOG_SESSION_ID, StringUtils.defaultIfBlank(sessionId, RandomUtil.generateId()));
        // kiloggoljuk a request-et
        StringBuilder sb = new StringBuilder();
        sb.append("Exception occured on REST input, original request is:\n");
        sb.append(requestResponseLogger.printRequestLine(servletRequest));
        sb.append(requestResponseLogger.printRequestHeaders(servletRequest));
        sb.append(requestResponseLogger.printRequestEntity(servletRequest));
        log.info(sb.toString());
    }

    /**
     * Szetvalogato metodus, ami kezeli a beburkolt ceges kiveteleketet es tovabb iranyitja a beburkolt kivetelt a megfelelo kivetel feldogozonak,
     * amenyiben az lehetseges.
     *
     * @param <E>
     *            the type parameter
     * @param exception
     *            a beburkolt kivetel
     * @return a megfelelo Response objektum vagy null
     */
    protected <E extends BaseException> Response handleWrappedException(final E exception) {
        if (exception == null) {
            log.warn("Failed to map the wrapped exception. Wrapper exception don't have content.");
            return null;
        }
        @SuppressWarnings("unchecked")
        ExceptionMapper<E> mapper = (ExceptionMapper<E>) providers.getExceptionMapper(exception.getClass());
        if (mapper == null) {
            log.info("Failed to map the wrapped exception. Falling back to the generic mapper. Oringnal Exception: [{0}]",
                    exception.getClass().getSimpleName());
            return null;
        }
        return mapper.toResponse(exception);
    }

    /**
     * Mask sensitive data string.
     *
     * @param sensitive
     *            the string to mask
     * @return the masked string
     */
    protected String maskSensitiveData(String sensitive) {
        if (StringUtils.contains(sensitive, "http") || StringUtils.contains(sensitive, "ftp")) {
            // ez komplett url
            // String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            // ez csak a url protocol+host resze
            String regex = "(https?|ftp)://[-a-zA-Z0-9+&@#%?=~_|!:,.;]*[-a-zA-Z0-9+&@#%=~_|]";
            return sensitive.replaceAll(regex, "[protocol://host]");
        }
        return sensitive;
    }
}
