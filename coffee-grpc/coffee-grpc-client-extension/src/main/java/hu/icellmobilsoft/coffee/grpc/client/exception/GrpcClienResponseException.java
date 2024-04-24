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
package hu.icellmobilsoft.coffee.grpc.client.exception;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.DebugInfo;
import com.google.rpc.ErrorInfo;
import com.google.rpc.LocalizedMessage;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;

/**
 * Grpc client call response exception
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public class GrpcClienResponseException extends BaseException {

    private static final long serialVersionUID = 1L;

    /**
     * Error information of exception
     */
    private ErrorInfo errorInfo;

    /**
     * Debug information of exception
     */
    private DebugInfo debugInfo;

    /**
     * Localized messages of exception
     */
    private List<LocalizedMessage> responseLocalizedMessages = new ArrayList<LocalizedMessage>();

    private GrpcClienResponseException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }

    /**
     * Create, extract and parse all known relevant information from Grpc response call exception. This may include localized error messages,
     * application error code, debugging informations and more.
     * 
     * @param statusRuntimeException
     *            Grpc call exception
     * @return Extracted data from Grpc call response exception
     */
    public static GrpcClienResponseException fromGrpcResponseException(StatusRuntimeException statusRuntimeException) {
        if (statusRuntimeException == null) {
            return null;
        }
        GrpcClienResponseException result = new GrpcClienResponseException(CoffeeFaultType.REST_CLIENT_EXCEPTION,
                MessageFormat.format("GRPC client response exception occured: [{0}]", statusRuntimeException.getLocalizedMessage()),
                statusRuntimeException);

        com.google.rpc.Status status = StatusProto.fromThrowable(statusRuntimeException);
        for (Any any : status.getDetailsList()) {
            if (any.is(ErrorInfo.class)) {
                result.setErrorInfo(anyUnpack(any, ErrorInfo.class));
                continue;
            }
            if (any.is(DebugInfo.class)) {
                result.setDebugInfo(anyUnpack(any, DebugInfo.class));
                continue;
            }
            if (any.is(LocalizedMessage.class)) {
                result.getResponseLocalizedMessages().add(anyUnpack(any, LocalizedMessage.class));
                continue;
            }
        }
        return result;
    }

    /**
     * Helper method to unpack custom any header without exception throwing
     * 
     * @param <T>
     *            Message object type
     * @param any
     *            Google protobuf Any object
     * @param clazz
     *            Unpack class type from Any
     * @return Unpacked Object from Any
     */
    public static <T extends com.google.protobuf.Message> T anyUnpack(Any any, java.lang.Class<T> clazz) {
        try {
            return any.unpack(clazz);
        } catch (InvalidProtocolBufferException e) {
            Logger.getLogger(GrpcClienResponseException.class)
                    .warn(MessageFormat.format("Exception occured on unpack Grpc Any: [{0}]", e.getLocalizedMessage()), e);
        }
        return null;
    }

    /**
     * Get Error specific data of exception
     * 
     * @return Error information
     */
    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    /**
     * Set Error specific data of exception
     * 
     * @param errorInfo
     *            Error information
     */
    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }

    /**
     * Get Debug specific data of exception
     * 
     * @return Debug information
     */
    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    /**
     * Set Debug specific data of exception
     * 
     * @param debugInfo
     *            Debug information
     */
    public void setDebugInfo(DebugInfo debugInfo) {
        this.debugInfo = debugInfo;
    }

    /**
     * Get Localization specific data of exception
     * 
     * @return Localized messages
     */
    public List<LocalizedMessage> getResponseLocalizedMessages() {
        return responseLocalizedMessages;
    }

    /**
     * Set Localization specific data of exception
     * 
     * @param responseLocalizedMessages
     *            Localized messages
     */
    public void setResponseLocalizedMessages(List<LocalizedMessage> responseLocalizedMessages) {
        this.responseLocalizedMessages = responseLocalizedMessages;
    }
}
