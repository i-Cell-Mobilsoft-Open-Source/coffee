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
package hu.icellmobilsoft.coffee.jpa.sql.batch.provider;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.stream.Stream;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.type.BasicType;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import hu.icellmobilsoft.coffee.jpa.sql.batch.constants.TestBasicTypes;

/**
 * {@link ArgumentsProvider} to providing binary test cases for BatchService.
 *
 * @author csaba.balogh
 * @version 2.0.0
 */
public class BinaryArgumentsProvider implements ArgumentsProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        String test = "Test";

        // PRIMITIVE_BYTE_ARRAY_BLOB_BASIC_TYPE / PRIMITIVE_BYTE_ARRAY_VARBINARY_BASIC_TYPE
        byte[] primitiveByteArray = test.getBytes(StandardCharsets.UTF_8);

        // WRAPPER_BYTE_ARRAY_BASIC_TYPE / WRAPPER_BYTE_ARRAY_VARBINARY_BASIC_TYPE
        Byte[] wrapperByteArray = ArrayUtils.toObject(primitiveByteArray);

        // BLOB_BASIC_TYPE
        Blob blob = new SerialBlob(primitiveByteArray);
        InputStream blobBinaryStream = blob.getBinaryStream();

        return Stream.of( //
                toArguments(TestBasicTypes.BLOB_BASIC_TYPE, blob, blobBinaryStream), //
                toArguments(TestBasicTypes.PRIMITIVE_BYTE_ARRAY_BLOB_BASIC_TYPE, primitiveByteArray, primitiveByteArray), //
                toArguments(TestBasicTypes.PRIMITIVE_BYTE_ARRAY_VARBINARY_BASIC_TYPE, primitiveByteArray, primitiveByteArray), //
                toArguments(TestBasicTypes.WRAPPER_BYTE_ARRAY_BLOB_BASIC_TYPE, wrapperByteArray, primitiveByteArray), //
                toArguments(TestBasicTypes.WRAPPER_BYTE_ARRAY_VARBINARY_BASIC_TYPE, wrapperByteArray, primitiveByteArray) //
        );
    }

    private static Arguments toArguments(BasicType<?> basicType, Object value, Object expectedValue) {
        return Arguments.of(toNamed(basicType), value, expectedValue);
    }

    private static Named<BasicType<?>> toNamed(BasicType<?> basicType) {
        return Named.of(basicType.getJavaType().getSimpleName(), basicType);
    }
}
