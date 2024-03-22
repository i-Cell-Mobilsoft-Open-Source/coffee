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
package hu.icellmobilsoft.coffee.grpc.base.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.google.protobuf.Timestamp;

/**
 * GRPC proto - java time utils
 * 
 * @author Imre Scheffer
 * @since 2.7.0
 */
public class ProtoDateUtil {

    /**
     * Convert Grpc protobuf {@link Timestamp} to {@link Instant}
     * 
     * @param timestamp
     *            Grpc protobuf {@link Timestamp}
     * @return time {@link Instant}
     */
    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    /**
     * Convert Grpc protobuf {@link Timestamp} to {@link OffsetDateTime}
     * 
     * @param timestamp
     *            Grpc protobuf {@link Timestamp}
     * @return UTC {@link OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTimeUTC(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return toInstant(timestamp).atOffset(ZoneOffset.UTC);
    }

    /**
     * Convert {@link Instant} to Grpc protobuf {@link Timestamp}
     * 
     * @param instant
     *            time to convert
     * @return Grpc protobuf {@link Timestamp}
     */
    public static Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
    }

    /**
     * Convert {@link OffsetDateTime} to Grpc protobuf {@link Timestamp}
     * 
     * @param offsetDateTime
     *            time to convert
     * @return Grpc protobuf {@link Timestamp}
     */
    public static Timestamp toTimestamp(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return toTimestamp(offsetDateTime.toInstant());
    }
}
