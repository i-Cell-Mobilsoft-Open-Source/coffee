/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.base.exception;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.lite.ProtoLiteUtils;

/**
 * 
 * <p>
 * The {@code StatusResponse} class represents a gRPC status response containing a {@link Status} and {@link Metadata}. It is used to return gRPC
 * error responses to clients.
 * </p>
 * <p>
 * The {@code StatusResponse} class can be created by calling the static factory methods {@link #of(com.google.rpc.Status)} or
 * {@link #of(com.google.rpc.Status, Throwable)}. The first method creates a {@code StatusResponse} instance with only a gRPC status code, while the
 * second method creates an instance with a gRPC status code and a cause, which is the root cause of the error. Both methods take a
 * {@link com.google.rpc.Status} object as an argument, which represents the gRPC status response to be returned to the client.
 * </p>
 * <p>
 * The {@code Metadata} object contains the binary representation of the {@link com.google.rpc.Status} object, which can be used to pass additional
 * error information to the client.
 * </p>
 * 
 * @author mark.petrenyi
 * @since 1.14.0
 * @see Status
 * @see Metadata
 */
public class StatusResponse {

    private static final Metadata.Key<com.google.rpc.Status> STATUS_DETAILS_KEY = Metadata.Key.of("grpc-status-details-bin",
            ProtoLiteUtils.metadataMarshaller(com.google.rpc.Status.getDefaultInstance()));

    private final Status status;
    private final Metadata metadata;

    private StatusResponse(com.google.rpc.Status statusProto) {
        status = Status.fromCodeValue(statusProto.getCode());
        metadata = toMetadata(statusProto);
    }

    private StatusResponse(com.google.rpc.Status statusProto, Throwable cause) {
        status = Status.fromCodeValue(statusProto.getCode()).withCause(cause);
        metadata = toMetadata(statusProto);
    }

    /**
     * Creates a {@link StatusResponse} from a {@link com.google.rpc.Status} object. The {@link StatusResponse} object can be used to pass the gRPC
     * status code and additional error information to the client.
     * 
     * @param statusProto
     *            the {@link com.google.rpc.Status} object representing the gRPC status code and additional error information.
     * @return the {@link StatusResponse} object created from the given {@link com.google.rpc.Status} object.
     */
    public static StatusResponse of(com.google.rpc.Status statusProto) {
        return new StatusResponse(statusProto);
    }

    /**
     * Creates a {@link StatusResponse} from a {@link com.google.rpc.Status} object and a {@link Throwable} cause. The {@link StatusResponse} object
     * can be used to pass the gRPC status code, cause, and additional error information to the client.
     * 
     * @param statusProto
     *            the {@link com.google.rpc.Status} object representing the gRPC status code and additional error information.
     * @param cause
     *            the {@link Throwable} representing the cause of the error.
     * @return the {@link StatusResponse} object created from the given {@link com.google.rpc.Status} object and {@link Throwable} cause.
     */
    public static StatusResponse of(com.google.rpc.Status statusProto, Throwable cause) {
        return new StatusResponse(statusProto, cause);
    }

    private static Metadata toMetadata(com.google.rpc.Status statusProto) {
        Metadata metadata = new Metadata();
        metadata.put(STATUS_DETAILS_KEY, statusProto);
        return metadata;
    }

    /**
     * Returns the {@link Status} object representing the gRPC status code and cause of the error.
     * 
     * @return the {@link Status} object representing the gRPC status code and cause of the error.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the {@link Metadata} object representing the gRPC metadata associated with the error response. The metadata contains the binary
     * representation of the {@link com.google.rpc.Status} object, which can be used to pass additional error information to the client.
     * 
     * @return the {@link Metadata} object representing the gRPC metadata associated with the error response.
     */
    public Metadata getMetadata() {
        return metadata;
    }
}
