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
package hu.icellmobilsoft.coffee.module.mongodb.codec.time.internal;

import java.text.MessageFormat;
import java.time.DateTimeException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.inject.Vetoed;

import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.Document;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;

/**
 * CodecsUtil for handling value exceptions, and documents
 *
 * https://github.com/cbartosiak/bson-codecs-jsr310
 *
 * @author balazs.joo
 * @since 1.0.0
 */
@Vetoed
public final class CodecsUtil {

    private CodecsUtil() {
    }

    /**
     * Unifies encode runtime exceptions.
     *
     * @param valueSupplier
     *            value supplier
     * @param valueConsumer
     *            value consumer
     * @param <Value>
     *            input value type
     */
    public static <Value> void translateEncodeExceptions(Supplier<Value> valueSupplier, Consumer<Value> valueConsumer) {

        Value value = valueSupplier.get();
        try {
            valueConsumer.accept(value);
        } catch (ArithmeticException | DateTimeException | NumberFormatException ex) {
            throw new BsonInvalidOperationException(MessageFormat.format("The value [{0}] is not supported", value), ex);
        }
    }

    /**
     * Unifies decode runtime exceptions.
     *
     * @param valueSupplier
     *            value supplier
     * @param valueConverter
     *            value converter function
     * @param <Value>
     *            input value type
     * @param <Result>
     *            decoded result type
     * @return decoding result
     */
    public static <Value, Result> Result translateDecodeExceptions(Supplier<Value> valueSupplier, Function<Value, Result> valueConverter) {

        Value value = valueSupplier.get();
        try {
            return valueConverter.apply(value);
        } catch (ArithmeticException | DateTimeException | IllegalArgumentException ex) {
            throw new BsonInvalidOperationException(MessageFormat.format("The value [{0}] is not supported", value), ex);
        }
    }

    /**
     * Reads BSON documents and decodes fields.
     *
     * @param reader
     *            BSON reader
     * @param decoderContext
     *            context
     * @param fieldDecoders
     *            {@link Map} of field decoders
     * @return BSON {@link Document}
     */
    public static Document readDocument(BsonReader reader, DecoderContext decoderContext, Map<String, Decoder<?>> fieldDecoders) {

        Document document = new Document();
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String fieldName = reader.readName();
            if (fieldDecoders.containsKey(fieldName)) {
                document.put(fieldName, fieldDecoders.get(fieldName).decode(reader, decoderContext));
            } else {
                throw new BsonInvalidOperationException(MessageFormat.format("The field [{0}] is not expected here", fieldName));
            }
        }
        reader.readEndDocument();
        return document;
    }

    /**
     * Returns field value from document.
     *
     * @param document
     *            document to return field value from
     * @param key
     *            key of field to return
     * @param clazz
     *            class of value to return
     * @param <Value>
     *            type of value to return
     * @return desired field value
     */
    public static <Value> Value getFieldValue(Document document, Object key, Class<Value> clazz) {

        try {
            Value value = document.get(key, clazz);
            if (value == null) {
                throw new BsonInvalidOperationException(MessageFormat.format("The value of the field [{0}] is null", key));
            }
            return value;
        } catch (ClassCastException ex) {
            throw new BsonInvalidOperationException(
                    MessageFormat.format("The value of the field [{0}] is not of the type [{1}]", key, clazz.getName()), ex);
        }
    }
}
