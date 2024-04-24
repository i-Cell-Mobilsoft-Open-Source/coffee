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
package hu.icellmobilsoft.coffee.grpc.client.metadata;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.grpc.api.metadata.IGrpcHeader;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.MetadataUtils;

/**
 * Grpc communication client header helper
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public class GrpcClientHeaderHelper {

    /**
     * Default constructor, constructs a new object.
     */
    private GrpcClientHeaderHelper() {
        super();
    }

    /**
     * Create Grpc Metadata header object with filled application process {@link IGrpcHeader#HEADER_SID} from Logging MDC settings
     * {@link LogConstants#LOG_SESSION_ID}
     * 
     * @return New Grpc Metadata header object
     */
    public static Metadata headerWithSid() {
        Metadata metaData = new Metadata();
        Object sid = MDC.get(LogConstants.LOG_SESSION_ID);
        if (sid != null) {
            metaData.put(IGrpcHeader.HEADER_SID, sid.toString());
        }
        return metaData;
    }

    /**
     * Create Grpc Metadata header object with filled application process {@link IGrpcHeader#HEADER_SID} from Logging MDC settings
     * {@link LogConstants#LOG_SESSION_ID} and given language value
     *
     * @param language
     *            the value of the language to be filled in the header. If null then language is not set
     * 
     * @return New Grpc Metadata header object
     */
    public static Metadata headerWithSid(String language) {
        Metadata metaData = headerWithSid();
        if (StringUtils.isNotBlank(language)) {
            metaData.put(IGrpcHeader.HEADER_LANGUAGE, language);
        }
        return metaData;
    }

    /**
     * Create Grpc Metadata header object filled with givev parameters. Predefined header is default application process
     * {@link IGrpcHeader#HEADER_SID} from Logging MDC settings
     * 
     * @param <KeyT>
     *            Grpc Metadata Key value type
     * @param metadataKey
     *            Grpc Metadata key
     * @param value
     *            Grpc metadata value
     * @return New Grpc Metadata header object
     */
    public static <KeyT> Metadata headerWithSid(Key<KeyT> metadataKey, KeyT value) {
        Metadata metaData = headerWithSid();
        if (metadataKey != null && value != null) {
            metaData.put(metadataKey, value);
        }
        return metaData;
    }

    /**
     * Add Grpc {@link IGrpcHeader#HEADER_SID} header to given Stub
     * 
     * @param <S>
     *            Grpc stub type
     * @param stub
     *            Grpc stub
     * @return New Stub extended with {@link IGrpcHeader#HEADER_SID}
     */
    public static <S extends AbstractStub<S>> S addHeaderWithSid(S stub) {
        if (stub == null) {
            return null;
        }
        Metadata header = headerWithSid();
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
    }

    /**
     * Add Grpc header to Stub
     * 
     * @param <S>
     *            Grpc stub type
     * @param stub
     *            Grpc stub
     * @param header
     *            Grpc Metadata header with keys and values
     * @return New Stub extended with header
     */
    public static <S extends AbstractStub<S>> S addHeader(S stub, Metadata header) {
        if (stub == null) {
            return null;
        }
        if (header == null) {
            return stub;
        }
        return stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(header));
    }
}
