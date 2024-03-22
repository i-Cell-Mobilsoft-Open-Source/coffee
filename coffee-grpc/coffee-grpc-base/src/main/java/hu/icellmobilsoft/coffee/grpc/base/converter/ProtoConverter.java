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
package hu.icellmobilsoft.coffee.grpc.base.converter;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.DtoConversionException;

/**
 * GRPC proto - REST xsd converter
 *
 * @author Imre Scheffer
 * @param <PROTO>
 *            GRPC proto type
 * @param <REST>
 *            XSD rest type
 * @since 2.1.0
 */
public interface ProtoConverter<PROTO extends com.google.protobuf.MessageOrBuilder, REST> {

    /**
     * Constant {@value #ERROR_NOT_IMPLEMENTED}
     */
    String ERROR_NOT_IMPLEMENTED = "Not implemented yet";

    /**
     * convert GRPC proto to REST xsd
     * 
     * @param proto
     *            GRPC proto object
     * @return type REST xsd object
     * @throws BaseException
     *             exception
     */
    default REST toType(PROTO proto) throws BaseException {
        throw new DtoConversionException(ERROR_NOT_IMPLEMENTED);
    };

    /**
     * convert REST xsd to GRPC proto
     * 
     * @param type
     *            REST xsd object
     * @return GRPC proto object
     * @throws BaseException
     *             exception
     */
    default PROTO toProto(REST type) throws BaseException {
        throw new DtoConversionException(ERROR_NOT_IMPLEMENTED);
    };

    /**
     * convert REST xsd to GRPC proto
     * 
     * @param destinationType
     *            REST xsd destination object
     * @param sourceProto
     *            GRPC proto source object
     * @throws BaseException
     *             exception
     */
    default void convert(REST destinationType, PROTO sourceProto) throws BaseException {
        throw new DtoConversionException(ERROR_NOT_IMPLEMENTED);
    };

    /**
     * convert GRPC proto to REST xsd
     * 
     * @param destinationProto
     *            GRPC proto destination object
     * @param sourceType
     *            REST xsd source object
     * @throws BaseException
     *             exception
     */
    default void convert(PROTO destinationProto, REST sourceType) throws BaseException {
        throw new DtoConversionException(ERROR_NOT_IMPLEMENTED);
    };
}
