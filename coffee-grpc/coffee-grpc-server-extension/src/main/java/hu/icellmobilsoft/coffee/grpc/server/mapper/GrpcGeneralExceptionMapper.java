/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.server.mapper;

import java.util.Arrays;
import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotAuthorizedException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.DebugInfo;
import com.google.rpc.ErrorInfo;
import com.google.rpc.LocalizedMessage;
import com.google.rpc.Status;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.grpc.base.exception.ExceptionMapper;
import hu.icellmobilsoft.coffee.grpc.base.metadata.GrpcHeaderHelper;
import hu.icellmobilsoft.coffee.rest.projectstage.ProjectStage;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.Metadata;

/**
 * Implementation of {@link ExceptionMapper} that maps general Exceptions to gRPC {@link Status}. If the exception is not recognized, it maps it to
 * {@link Code#INTERNAL}.
 * 
 * It uses {@link IGrpcExceptionTranslator} to translate exceptions to GRPC statuses.
 *
 * @author mark.petrenyi
 * @author Imre Scheffer
 * @since 2.7.0
 */
@ApplicationScoped
public class GrpcGeneralExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    private Logger log;

    @Inject
    private IGrpcExceptionTranslator grpcExceptionTranslator;

    @Inject
    private ProjectStage projectStage;

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcGeneralExceptionMapper() {
        super();
    }

    @Override
    public Status toStatus(Metadata requestHeaders, Exception e) {
        Status.Builder statusB = null;
        Locale locale = getRequestLocale(requestHeaders);
        if (e instanceof NotAuthorizedException) {
            statusB = createStatus(locale, Code.UNAUTHENTICATED, CoffeeFaultType.NOT_AUTHORIZED, e);
        } else if (e instanceof ForbiddenException) {
            statusB = createStatus(locale, Code.PERMISSION_DENIED, CoffeeFaultType.FORBIDDEN, e);
        } else if (e instanceof IllegalArgumentException || e.getCause() instanceof IllegalArgumentException) {
            statusB = createStatus(locale, Code.INVALID_ARGUMENT, CoffeeFaultType.ILLEGAL_ARGUMENT_EXCEPTION, e);
        }

        if (statusB != null) {
            log.error("Known error: ", e);
        } else {
            log.error("Unknown error: ", e);
            statusB = createStatus(locale, Code.INTERNAL, CoffeeFaultType.GENERIC_EXCEPTION, e);
        }
        return statusB.build();
    }

    /**
     * Get Locale parameter from Grpc request metadata
     * 
     * @param requestHeaders
     *            Grpc request metadata
     * @return Locale from request
     */
    protected Locale getRequestLocale(Metadata requestHeaders) {
        return GrpcHeaderHelper.getRequestLocale(requestHeaders);
    }

    /**
     * Create Proto Error info object from given parameter
     * 
     * @param faultEnum
     *            Application Error code packaged into result
     * @return Proto Error info object
     */
    public static ErrorInfo.Builder toErrorInfo(Enum<?> faultEnum) {
        ErrorInfo.Builder result = ErrorInfo.newBuilder();
        if (faultEnum != null) {
            result.setReason(faultEnum.name());
            result.setDomain(faultEnum.getClass().getName());
        }
        return result;
    }

    /**
     * Create Proto Debug info object from given parameters
     * 
     * @param throwable
     *            Throwable packaged into result
     * @return Proto Debug info object
     */
    public static DebugInfo.Builder toDebugInfo(Throwable throwable) {
        DebugInfo.Builder result = DebugInfo.newBuilder();
        if (throwable != null) {
            String[] frames = ExceptionUtils.getStackFrames(throwable);
            result.addAllStackEntries(Arrays.asList(frames));
            result.setDetail(ExceptionUtils.getMessage(throwable));
        }
        return result;
    }

    /**
     * Create Proto {@code com.google.rpc.Status} object from given parameters
     * 
     * @param locale
     *            Locale to translate response error message
     * @param grpcStatus
     *            Response Grpc status
     * @param faultType
     *            Error code
     * @param exception
     *            Exception to packaged into Grpc response metadata
     * @return Proto Status object
     */
    protected Status.Builder createStatus(Locale locale, Code grpcStatus, Enum<?> faultType, Exception exception) {
        Status.Builder result = Status.newBuilder();
        LocalizedMessage.Builder localizedMessageB = grpcExceptionTranslator.toLocalizedMessage(locale, faultType);
        ErrorInfo.Builder errorInfoBuilder = toErrorInfo(faultType);
        result.setCode(grpcStatus.getNumber());
        result.setMessage(exception.getLocalizedMessage());
        result.addDetails(Any.pack(localizedMessageB.build()));
        result.addDetails(Any.pack(errorInfoBuilder.build()));
        if (!projectStage.isProductionStage()) {
            DebugInfo.Builder debugInfoBuilder = toDebugInfo(exception);
            result.addDetails(Any.pack(debugInfoBuilder.build()));
        }
        return result;
    }
}
