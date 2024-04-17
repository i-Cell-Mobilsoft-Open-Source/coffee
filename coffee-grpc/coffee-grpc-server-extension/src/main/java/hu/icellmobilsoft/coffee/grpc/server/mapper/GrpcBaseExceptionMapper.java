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

import java.util.Locale;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.DebugInfo;
import com.google.rpc.ErrorInfo;
import com.google.rpc.LocalizedMessage;
import com.google.rpc.Status;

import hu.icellmobilsoft.coffee.dto.exception.AccessDeniedException;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.ServiceUnavailableException;
import hu.icellmobilsoft.coffee.grpc.base.exception.ExceptionMapper;
import hu.icellmobilsoft.coffee.grpc.server.metadata.GrpcHeaderHelper;
import hu.icellmobilsoft.coffee.rest.projectstage.ProjectStage;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.BusinessException;
import hu.icellmobilsoft.coffee.se.api.exception.DtoConversionException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.Metadata;

/**
 * Implementation of {@link ExceptionMapper} that maps {@link BaseException} to gRPC {@link Status}.
 *
 * @author mark.petrenyi
 * @author Imre Scheffer
 * @since 2.7.0
 */
@ApplicationScoped
@Priority(1)
public class GrpcBaseExceptionMapper implements ExceptionMapper<BaseException> {

    @Inject
    private Logger log;

    @Inject
    private IGrpcExceptionTranslator grpcExceptionTranslator;

    @Inject
    private ProjectStage projectStage;

    /**
     * Default constructor, constructs a new object.
     */
    public GrpcBaseExceptionMapper() {
        super();
    }

    @Override
    public Status toStatus(Metadata requestHeaders, BaseException e) {
        Status.Builder statusB = null;
        Locale locale = getRequestLocale(requestHeaders);
        if (e instanceof AccessDeniedException) {
            statusB = createStatus(locale, Code.UNAUTHENTICATED, e);
        } else if (e instanceof BONotFoundException) {
            statusB = createStatus(locale, Code.NOT_FOUND, e);
        } else if (e instanceof DtoConversionException || e instanceof hu.icellmobilsoft.coffee.dto.exception.DtoConversionException) {
            statusB = createStatus(locale, Code.INVALID_ARGUMENT, e);
        } else if (e instanceof ServiceUnavailableException) {
            statusB = createStatus(locale, Code.UNAVAILABLE, e);
        } else if (e instanceof BusinessException || e instanceof hu.icellmobilsoft.coffee.dto.exception.BusinessException) {
            statusB = createStatus(locale, Code.FAILED_PRECONDITION, e);
        }

        if (statusB == null) {
            statusB = createStatus(locale, Code.INTERNAL, e);
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
     * Create Proto {@code com.google.rpc.Status} object from given parameters
     * 
     * @param locale
     *            Locale to translate response error message
     * @param grpcStatus
     *            Response Grpc status
     * @param baseException
     *            Exception to packaged into Grpc response metadata
     * @return Proto Status object
     */
    protected Status.Builder createStatus(Locale locale, Code grpcStatus, BaseException baseException) {
        Status.Builder result = Status.newBuilder();
        LocalizedMessage.Builder localizedMessageB = grpcExceptionTranslator.toLocalizedMessage(locale, baseException);
        ErrorInfo.Builder errorInfoBuilder = GrpcGeneralExceptionMapper.toErrorInfo(baseException.getFaultTypeEnum());
        result.setCode(grpcStatus.getNumber());
        result.setMessage(baseException.getLocalizedMessage());
        result.addDetails(Any.pack(localizedMessageB.build()));
        result.addDetails(Any.pack(errorInfoBuilder.build()));
        if (!projectStage.isProductionStage()) {
            DebugInfo.Builder debugInfoBuilder = GrpcGeneralExceptionMapper.toDebugInfo(baseException);
            result.addDetails(Any.pack(debugInfoBuilder.build()));
        }
        return result;
    }
}
