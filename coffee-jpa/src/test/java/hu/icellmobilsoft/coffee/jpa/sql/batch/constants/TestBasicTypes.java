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
package hu.icellmobilsoft.coffee.jpa.sql.batch.constants;

import java.sql.Blob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import jakarta.enterprise.inject.Vetoed;

import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.BlobJavaType;
import org.hibernate.type.descriptor.java.BooleanJavaType;
import org.hibernate.type.descriptor.java.ByteArrayJavaType;
import org.hibernate.type.descriptor.java.CalendarJavaType;
import org.hibernate.type.descriptor.java.CharacterJavaType;
import org.hibernate.type.descriptor.java.DateJavaType;
import org.hibernate.type.descriptor.java.InstantJavaType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.java.JdbcDateJavaType;
import org.hibernate.type.descriptor.java.JdbcTimeJavaType;
import org.hibernate.type.descriptor.java.JdbcTimestampJavaType;
import org.hibernate.type.descriptor.java.LocalDateJavaType;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.hibernate.type.descriptor.java.LocalTimeJavaType;
import org.hibernate.type.descriptor.java.OffsetDateTimeJavaType;
import org.hibernate.type.descriptor.java.OffsetTimeJavaType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayJavaType;
import org.hibernate.type.descriptor.java.StringJavaType;
import org.hibernate.type.descriptor.java.ZonedDateTimeJavaType;
import org.hibernate.type.descriptor.jdbc.BlobJdbcType;
import org.hibernate.type.descriptor.jdbc.BooleanJdbcType;
import org.hibernate.type.descriptor.jdbc.CharJdbcType;
import org.hibernate.type.descriptor.jdbc.DateJdbcType;
import org.hibernate.type.descriptor.jdbc.TimeJdbcType;
import org.hibernate.type.descriptor.jdbc.TimestampJdbcType;
import org.hibernate.type.descriptor.jdbc.VarbinaryJdbcType;
import org.hibernate.type.internal.BasicTypeImpl;

/**
 * {@link BasicType} constants and helper methods for testing.
 * 
 * @author csaba.balogh
 * @since 2.0.0
 */
@Vetoed
public class TestBasicTypes {

    // date

    /**
     * Basic type constant for wrapping {@link JdbcDateJavaType} and {@link DateJdbcType}.
     */
    public static final BasicType<Date> JDBC_DATE_BASIC_TYPE = createDateBasicType(JdbcDateJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link LocalDateJavaType} and {@link DateJdbcType}.
     */
    public static final BasicType<LocalDate> LOCAL_DATE_BASIC_TYPE = createDateBasicType(LocalDateJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link DateJavaType} and {@link DateJdbcType}.
     */
    public static final BasicType<Date> UTIL_DATE_DATE_BASIC_TYPE = createDateBasicType(DateJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link CalendarJavaType} and {@link DateJdbcType}.
     */
    public static final BasicType<Calendar> CALENDAR_DATE_BASIC_TYPE = createDateBasicType(CalendarJavaType.INSTANCE);

    // time

    /**
     * Basic type constant for wrapping {@link JdbcTimeJavaType} and {@link TimeJdbcType}.
     */
    public static final BasicType<Date> JDBC_TIME_BASIC_TYPE = createTimeBasicType(JdbcTimeJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link LocalTimeJavaType} and {@link TimeJdbcType}.
     */
    public static final BasicType<LocalTime> LOCAL_TIME_BASIC_TYPE = createTimeBasicType(LocalTimeJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link OffsetTimeJavaType} and {@link TimeJdbcType}.
     */
    public static final BasicType<OffsetTime> OFFSET_TIME_BASIC_TYPE = createTimeBasicType(OffsetTimeJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link DateJavaType} and {@link TimeJdbcType}.
     */
    public static final BasicType<Date> UTIL_DATE_TIME_BASIC_TYPE = createTimeBasicType(DateJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link CalendarJavaType} and {@link TimeJdbcType}.
     */
    public static final BasicType<Calendar> CALENDAR_TIME_BASIC_TYPE = createTimeBasicType(CalendarJavaType.INSTANCE);

    // timestamp

    /**
     * Basic type constant for wrapping {@link JdbcTimestampJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<Date> JDBC_TIMESTAMP_BASIC_TYPE = createTimestampBasicType(JdbcTimestampJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link LocalDateTimeJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<LocalDateTime> LOCAL_DATE_TIME_BASIC_TYPE = createTimestampBasicType(LocalDateTimeJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link OffsetDateTimeJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<OffsetDateTime> OFFSET_DATE_TIME_BASIC_TYPE = createTimestampBasicType(OffsetDateTimeJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link ZonedDateTimeJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<ZonedDateTime> ZONED_DATE_TIME_BASIC_TYPE = createTimestampBasicType(ZonedDateTimeJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link InstantJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<Instant> INSTANT_BASIC_TYPE = createTimestampBasicType(InstantJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link DateJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<Date> DATE_TIMESTAMP_BASIC_TYPE = createTimestampBasicType(DateJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link CalendarJavaType} and {@link TimestampJdbcType}.
     */
    public static final BasicType<Calendar> CALENDAR_TIMESTAMP_BASIC_TYPE = createTimestampBasicType(CalendarJavaType.INSTANCE);

    // blob

    /**
     * Basic type constant for wrapping {@link BlobJavaType} and {@link BlobJdbcType#BLOB_BINDING)}.
     */
    public static final BasicType<Blob> BLOB_BASIC_TYPE = createBlobBasicType(BlobJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link PrimitiveByteArrayJavaType} and {@link BlobJdbcType#BLOB_BINDING}.
     */
    public static final BasicType<byte[]> PRIMITIVE_BYTE_ARRAY_BLOB_BASIC_TYPE = createBlobBasicType(PrimitiveByteArrayJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link ByteArrayJavaType} and {@link BlobJdbcType#BLOB_BINDING}.
     */
    public static final BasicType<Byte[]> WRAPPER_BYTE_ARRAY_BLOB_BASIC_TYPE = createBlobBasicType(ByteArrayJavaType.INSTANCE);

    // varbinary

    /**
     * Basic type constant for wrapping {@link PrimitiveByteArrayJavaType} and {@link VarbinaryJdbcType}.
     */
    public static final BasicType<byte[]> PRIMITIVE_BYTE_ARRAY_VARBINARY_BASIC_TYPE = createVarBinaryBasicType(PrimitiveByteArrayJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link ByteArrayJavaType} and {@link VarbinaryJdbcType}.
     */
    public static final BasicType<Byte[]> WRAPPER_BYTE_ARRAY_VARBINARY_BASIC_TYPE = createVarBinaryBasicType(ByteArrayJavaType.INSTANCE);

    // char

    /**
     * Basic type constant for wrapping {@link CharacterJavaType} and {@link CharJdbcType}.
     */
    public static final BasicType<Character> CHARACTER_BASIC_TYPE = new BasicTypeImpl<>(CharacterJavaType.INSTANCE, CharJdbcType.INSTANCE);

    // boolean

    /**
     * Basic type constant for wrapping {@link BooleanJavaType} and {@link BooleanJdbcType}.
     */
    public static final BasicType<Boolean> BOOLEAN_BASIC_TYPE = createBooleanBasicType(BooleanJavaType.INSTANCE);

    /**
     * Basic type constant for wrapping {@link StringJavaType} and {@link BooleanJdbcType}.
     */
    public static final BasicType<String> BOOLEAN_STRING_BASIC_TYPE = createBooleanBasicType(StringJavaType.INSTANCE);

    /**
     * Creates a {@link BasicType} for wrapping the incoming {@link JavaType} and {@link DateJdbcType}.
     * 
     * @param javaType
     *            the {@link JavaType}.
     * @return the created {@link BasicType}.
     * @param <E>
     *            the java type.
     */
    public static <E> BasicType<E> createDateBasicType(JavaType<E> javaType) {
        return new BasicTypeImpl<>(javaType, DateJdbcType.INSTANCE);
    }

    /**
     * Creates a {@link BasicType} for wrapping the incoming {@link JavaType} and {@link TimeJdbcType}.
     *
     * @param javaType
     *            the {@link JavaType}.
     * @return the created {@link BasicType}.
     * @param <E>
     *            the java type.
     */
    public static <E> BasicType<E> createTimeBasicType(JavaType<E> javaType) {
        return new BasicTypeImpl<>(javaType, TimeJdbcType.INSTANCE);
    }

    /**
     * Creates a {@link BasicType} for wrapping the incoming {@link JavaType} and {@link TimestampJdbcType}.
     *
     * @param javaType
     *            the {@link JavaType}.
     * @return the created {@link BasicType}.
     * @param <E>
     *            the java type.
     */
    public static <E> BasicType<E> createTimestampBasicType(JavaType<E> javaType) {
        return new BasicTypeImpl<>(javaType, TimestampJdbcType.INSTANCE);
    }

    /**
     * Creates a {@link BasicType} for wrapping the incoming {@link JavaType} and {@link BlobJdbcType}.
     *
     * @param javaType
     *            the {@link JavaType}.
     * @return the created {@link BasicType}.
     * @param <E>
     *            the java type.
     */
    public static <E> BasicType<E> createBlobBasicType(JavaType<E> javaType) {
        return new BasicTypeImpl<>(javaType, BlobJdbcType.BLOB_BINDING);
    }

    /**
     * Creates a {@link BasicType} for wrapping the incoming {@link JavaType} and {@link VarbinaryJdbcType}.
     *
     * @param javaType
     *            the {@link JavaType}.
     * @return the created {@link BasicType}.
     * @param <E>
     *            the java type.
     */
    public static <E> BasicType<E> createVarBinaryBasicType(JavaType<E> javaType) {
        return new BasicTypeImpl<>(javaType, VarbinaryJdbcType.INSTANCE);
    }

    /**
     * Creates a {@link BasicType} for wrapping the incoming {@link JavaType} and {@link BooleanJdbcType}.
     *
     * @param javaType
     *            the {@link JavaType}.
     * @return the created {@link BasicType}.
     * @param <E>
     *            the java type.
     */
    public static <E> BasicType<E> createBooleanBasicType(JavaType<E> javaType) {
        return new BasicTypeImpl<>(javaType, BooleanJdbcType.INSTANCE);
    }
}
