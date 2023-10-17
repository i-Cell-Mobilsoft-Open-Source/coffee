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
package hu.icellmobilsoft.coffee.module.mongodb.codec.time.duration;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.Decoder;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import hu.icellmobilsoft.coffee.module.mongodb.codec.time.internal.CodecsUtil;

/**
 * <p>
 * Encodes and decodes {@code Duration} values to and from {@code BSON Document}, such as {@code { seconds: 10, nanos: 100 }}.
 * <p>
 * The values are stored using the following structure:
 * <ul>
 * <li>{@code seconds} (a non-null {@code Int64});
 * <li>{@code nanos} (a non-null {@code Int32}).
 * </ul>
 * <p>
 * This type is <b>immutable</b>.
 *
 * https://github.com/cbartosiak/bson-codecs-jsr310
 *
 * @author balazs.joo
 * @since 1.0.0
 */
public final class DurationAsDocumentCodec implements Codec<Duration> {

    private static final String SECONDS = "seconds";
    private static final String NANOS = "nanos";

    private static final Map<String, Decoder<?>> FIELD_DECODERS;

    static {
        Map<String, Decoder<?>> fd = new HashMap<>();
        fd.put(SECONDS, (r, dc) -> r.readInt64());
        fd.put(NANOS, (r, dc) -> r.readInt32());
        FIELD_DECODERS = Collections.unmodifiableMap(fd);
    }

    /**
     * Default constructor, constructs a new object.
     */
    public DurationAsDocumentCodec() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void encode(BsonWriter writer, Duration value, EncoderContext encoderContext) {
        if (Objects.nonNull(writer) && Objects.nonNull(value)) {
            writer.writeStartDocument();
            writer.writeInt64(SECONDS, value.getSeconds());
            writer.writeInt32(NANOS, value.getNano());
            writer.writeEndDocument();
        }
    }

    /** {@inheritDoc} */
    @Override
    public Duration decode(BsonReader reader, DecoderContext decoderContext) {
        if (Objects.nonNull(reader)) {
            return CodecsUtil.translateDecodeExceptions(() -> CodecsUtil.readDocument(reader, decoderContext, FIELD_DECODERS), val -> Duration
                    .ofSeconds(CodecsUtil.getFieldValue(val, SECONDS, Long.class), CodecsUtil.getFieldValue(val, NANOS, Integer.class)));
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Class<Duration> getEncoderClass() {
        return Duration.class;
    }
}
