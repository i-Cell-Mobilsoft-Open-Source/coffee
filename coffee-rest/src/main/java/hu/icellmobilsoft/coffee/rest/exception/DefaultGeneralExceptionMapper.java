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

import java.util.function.BiConsumer;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Providers;
import jakarta.xml.bind.UnmarshalException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.spi.InternalServerErrorException;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.common.commonservice.TechnicalFault;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.rest.cdi.BaseApplicationContainer;
import hu.icellmobilsoft.coffee.rest.log.RequestResponseLogger;
import hu.icellmobilsoft.coffee.rest.projectstage.ProjectStage;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.wrapper.IBaseExceptionWrapper;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;

/**
 * Exception mapper for non-handled exception throwing
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class DefaultGeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private BaseApplicationContainer baseApplicationContainer;

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

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultGeneralExceptionMapper() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Response toResponse(Exception e) {
        Response result = null;
        if (e instanceof IBaseExceptionWrapper<?>) {
            Exception unwrappedException = unwrapException((Exception & IBaseExceptionWrapper<?>) e);
            if (unwrappedException instanceof BaseException) {
                result = handleWrappedException((BaseException) unwrappedException);
            } else {
                log.trace("Unwrapped exception is not a BaseException, proceeding with default exception handling.");
            }
        }
        return (result != null) ? result : handleException(e);
    }

    /**
     * Tries to unwrap the given exception. I.e. the exception in the parameter has a BaseException cause, then it returns the BaseException
     *
     * @param exceptionWrapper
     *            the exception to unwrap
     * @param <WRAPPER>
     *            the type of the wrapper exception
     * @return the unwrapped exception
     */
    protected <WRAPPER extends Exception & IBaseExceptionWrapper<?>> Exception unwrapException(WRAPPER exceptionWrapper) {
        Exception unwrapped;
        if (exceptionWrapper.getWrappedBaseException() != null) {
            log.trace("Wrapped BaseException.");
            unwrapped = exceptionWrapper.getWrappedBaseException();
        } else if (exceptionWrapper.getCause() instanceof BaseException) {
            log.trace("Wrapped BaseException.");
            unwrapped = (BaseException) exceptionWrapper.getCause();
        } else {
            log.error("Unknown error in cause: ", exceptionWrapper);
            log.writeLogToError();
            unwrapped = exceptionWrapper;
        }
        return unwrapped;
    }

    /**
     * Handle exception.
     *
     * @param e
     *            the exception
     * @return the response
     */
    protected Response handleException(Exception e) {
        ResponseBuilder responseBuilder = null;
        if (e instanceof NotAcceptableException) {
            responseBuilder = createResponseBuilder(
                    e,
                    Response.Status.INTERNAL_SERVER_ERROR,
                    CoffeeFaultType.NOT_ACCEPTABLE_EXCEPTION,
                    this::handleProductionStage);
        } else if (e instanceof NotAllowedException) {
            responseBuilder = createResponseBuilder(
                    e,
                    Response.Status.INTERNAL_SERVER_ERROR,
                    CoffeeFaultType.NOT_ALLOWED_EXCEPTION,
                    this::handleProductionStage);
        } else if (e instanceof NotAuthorizedException) {
            responseBuilder = createResponseBuilder(e, Response.Status.UNAUTHORIZED, CoffeeFaultType.NOT_AUTHORIZED, this::handleProductionStage);
        } else if (e instanceof ForbiddenException) {
            responseBuilder = createResponseBuilder(e, Response.Status.FORBIDDEN, CoffeeFaultType.FORBIDDEN, this::handleProductionStage);
        } else if (e instanceof UnmarshalException) {
            responseBuilder = createResponseBuilder(e, Response.Status.INTERNAL_SERVER_ERROR, CoffeeFaultType.OPERATION_FAILED, (dto, faultType) -> {
                dto.setMessage(maskSensitiveData(exceptionMessageTranslator.getLinkedExceptionLocalizedMessage((UnmarshalException) e)));
                dto.setException(null);
            });
        } else if (e instanceof InternalServerErrorException) {
            responseBuilder = createResponseBuilder(e, Response.Status.BAD_REQUEST, CoffeeFaultType.INVALID_REQUEST, this::handleProductionStage);
        } else if (e.getCause() instanceof IllegalArgumentException) {
            responseBuilder = createResponseBuilder(
                    e,
                    Response.Status.INTERNAL_SERVER_ERROR,
                    CoffeeFaultType.ILLEGAL_ARGUMENT_EXCEPTION,
                    this::handleProductionStage);
        } else if (e instanceof ClientErrorException) {
            responseBuilder = createResponseBuilder(e, Response.Status.INTERNAL_SERVER_ERROR, CoffeeFaultType.OPERATION_FAILED, (dto, faultType) -> {
                dto.setMessage(maskSensitiveData(e.getLocalizedMessage()));
                dto.setException(null);
            });

            boolean productionStage = projectStage.isProductionStage();
            if (productionStage) {
                // NotFoundException, NotSupportedException, ...
                handleRequestProcess();
                // a kérésben megadott accept lesz a válasz application/octet-stream helyett
                String accept = servletRequest.getHeader(HttpHeaders.ACCEPT);
                responseBuilder.type(MediaType.valueOf(StringUtils.defaultString(accept, MediaType.APPLICATION_XML)));
            }
        }

        if (responseBuilder != null) {
            log.error("Known error: ", e);
            log.writeLogToError();
        } else {
            log.error("Unknown error: ", e);
            log.writeLogToError();
            responseBuilder = createResponseBuilder(
                    e,
                    Response.Status.INTERNAL_SERVER_ERROR,
                    CoffeeFaultType.GENERIC_EXCEPTION,
                    this::handleProductionStage);

        }
        return responseBuilder.build();
    }

    private void handleProductionStage(TechnicalFault dto, CoffeeFaultType faultType) {
        dto.setMessage(exceptionMessageTranslator.getLocalizedMessage(faultType));
        dto.setException(null);
    }

    /**
     * Creates a response builder for the given exception/error
     *
     * @param e
     *            exception
     * @param responseStatus
     *            HTTP response code
     * @param faultType
     *            coffee fault type
     * @param productionStageConsumer
     *            function to be called if the current stage is production
     * @return the created response builder
     */
    protected ResponseBuilder createResponseBuilder(Exception e, Response.Status responseStatus, CoffeeFaultType faultType,
            BiConsumer<TechnicalFault, CoffeeFaultType> productionStageConsumer) {
        TechnicalFault dto = new TechnicalFault();
        exceptionMessageTranslator.addCommonInfo(dto, e, faultType);
        Response.Status statusCode = responseStatus;
        ResponseBuilder responseBuilder = Response.status(statusCode);
        boolean productionStage = projectStage.isProductionStage();
        if (productionStage) {
            productionStageConsumer.accept(dto, faultType);
        }
        return responseBuilder.entity(dto);
    }

    /**
     * Handle request process.
     */
    protected void handleRequestProcess() {
        // MDC ezekben az esetekben nem volt tisztitva, mert nem is volt request loggolas
        MDC.clear();
        // feltoltjuk a szokasos adatokkal
        MDC.put(LogConstants.LOG_SERVICE_NAME, baseApplicationContainer.getCoffeeAppName());
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
            log.info(
                    "Failed to map the wrapped exception. Falling back to the generic mapper. Oringnal Exception: [{0}]",
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
