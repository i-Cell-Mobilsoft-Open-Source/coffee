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
package hu.icellmobilsoft.coffee.jpa.sql.batch;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.hibernate.type.CalendarDateType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.DateType;
import org.hibernate.type.InstantType;
import org.hibernate.type.LocalDateTimeType;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.LocalTimeType;
import org.hibernate.type.OffsetDateTimeType;
import org.hibernate.type.OffsetTimeType;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.ZonedDateTimeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;

/**
 * Class for testing {@link BatchService}.
 * 
 * @author csaba.balogh
 * @since 1.12.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    private static final ZoneOffset DEFAULT_ZONE_OFFSET = OffsetTime.now().getOffset();

    @Mock
    private AppLogger appLogger;

    @Mock
    private PreparedStatement preparedStatement;

    @Spy
    @InjectMocks
    private BatchService batchService = Mockito.spy(new BatchService());

    private TimeZone dbTimeZone;

    @Order(1)
    @ParameterizedTest(name = "[{index}] - type: [{0}] value: [{1}]")
    @MethodSource({ "provideDateTypes" })
    void setPsObjectDateWithoutTimezoneTest(SingleColumnType<?> type, Object value) throws SQLException {
        batchService.setPsObject(preparedStatement, 0, type, value);
        Mockito.verify(preparedStatement).setObject(0, value);
    }

    @Order(2)
    @ParameterizedTest(name = "[{index}] - type: [{0}] value: [{1}]")
    @MethodSource({ "provideTimeTypes", "provideTimestampTypes" })
    void setPsObjectTimeAndTimestampWithoutTimezoneTest(SingleColumnType<?> type, Object value) throws SQLException {
        batchService.setPsObject(preparedStatement, 0, type, value);
        Mockito.verify(preparedStatement).setObject(0, value, type.sqlType());
    }

    @Order(3)
    @ParameterizedTest(name = "[{index}] - type: [{0}] value: [{1}]")
    @MethodSource("provideDateTypes")
    void setPsObjectDateWithTimezoneTest(SingleColumnType<?> type, Object value) throws SQLException {
        // BatchService-ben lévő dbTimeZone mock készítése miatt szükséges
        mockDbTimeZone();

        batchService.setPsObject(preparedStatement, 0, type, value);

        Mockito.verify(preparedStatement).setObject(0, value);
    }

    @Order(4)
    @ParameterizedTest(name = "[{index}] - type: [{0}] value: [{1}] expectedValue: [{2}]")
    @MethodSource("provideTimeTypes")
    void setPsObjectTimeWithTimezoneTest(SingleColumnType<?> type, Object value, Object expectedValue) throws SQLException {
        // BatchService-ben lévő dbTimeZone mock készítése miatt szükségesek
        mockDbTimeZone();
        Mockito.doReturn(UTC_ZONE_ID).when(dbTimeZone).toZoneId();

        batchService.setPsObject(preparedStatement, 0, type, value);

        ArgumentCaptor<Calendar> calendarArgumentCaptor = ArgumentCaptor.forClass(Calendar.class);
        if (expectedValue instanceof Time) {
            Mockito.verify(preparedStatement).setTime(Mockito.eq(0), Mockito.eq((Time) expectedValue), calendarArgumentCaptor.capture());
            Assertions.assertEquals(UTC_ZONE_ID, calendarArgumentCaptor.getValue().getTimeZone().toZoneId());
        } else {
            Mockito.verify(preparedStatement).setObject(0, expectedValue, Types.TIME);
        }
    }

    @Order(5)
    @ParameterizedTest(name = "[{index}] - type: [{0}] value: [{1}] expectedValue: [{2}]")
    @MethodSource("provideTimestampTypes")
    void setPsObjectTimestampWithTimezoneTest(SingleColumnType<?> type, Object value, Object expectedValue) throws SQLException {
        // BatchService-ben lévő dbTimeZone mock készítése miatt szükségesek
        mockDbTimeZone();
        Mockito.doReturn(UTC_ZONE_ID).when(dbTimeZone).toZoneId();

        batchService.setPsObject(preparedStatement, 0, type, value);

        ArgumentCaptor<Calendar> calendarArgumentCaptor = ArgumentCaptor.forClass(Calendar.class);
        if (expectedValue instanceof Timestamp) {
            Mockito.verify(preparedStatement).setTimestamp(Mockito.eq(0), Mockito.eq((Timestamp) expectedValue), calendarArgumentCaptor.capture());
            Assertions.assertEquals(UTC_ZONE_ID, calendarArgumentCaptor.getValue().getTimeZone().toZoneId());
        } else {
            Mockito.verify(preparedStatement).setObject(0, expectedValue, Types.TIMESTAMP);
        }
    }

    private void mockDbTimeZone() {
        dbTimeZone = Mockito.mock(TimeZone.class);
        dbTimeZone.setID("UTC");
        MockitoAnnotations.initMocks(this);
    }

    private static Stream<Arguments> provideDateTypes() {
        return Stream.of( //
                Arguments.of(toNamed(LocalDateType.INSTANCE), LocalDate.now(), null), //
                Arguments.of(toNamed(DateType.INSTANCE), java.sql.Date.valueOf(LocalDate.now()), null), //
                Arguments.of(toNamed(DateType.INSTANCE), new Date(), null), //
                Arguments.of(toNamed(CalendarDateType.INSTANCE), Calendar.getInstance(), null) //
        );
    }

    private static Stream<Arguments> provideTimeTypes() {
        LocalTime localTime = LocalTime.now();
        LocalTime localTimeInUTC = OffsetTime.of(localTime, DEFAULT_ZONE_OFFSET).withOffsetSameInstant(ZoneOffset.UTC).toLocalTime();
        OffsetTime offsetTime = OffsetTime.now();
        OffsetTime offsetTimeInUTC = offsetTime.withOffsetSameInstant(ZoneOffset.UTC);
        Date date = new Date();
        return Stream.of( //
                Arguments.of(toNamed(LocalTimeType.INSTANCE), localTime, localTimeInUTC), //
                Arguments.of(toNamed(OffsetTimeType.INSTANCE), offsetTime, offsetTimeInUTC), //
                Arguments.of(toNamed(TimeType.INSTANCE), Time.valueOf(localTime), Time.valueOf(localTime)), //
                Arguments.of(toNamed(TimeType.INSTANCE), date, new Time(date.getTime())) //
        );
    }

    private static Stream<Arguments> provideTimestampTypes() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTimeInUTC = OffsetDateTime.of(localDateTime, DEFAULT_ZONE_OFFSET).withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
        Instant instant = Instant.now();
        Instant instantInUTC = OffsetDateTime.ofInstant(instant, ZoneId.systemDefault()).withOffsetSameInstant(ZoneOffset.UTC).toInstant();
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        OffsetDateTime offsetDateTimeInUTC = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC);
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        ZonedDateTime zonedDateTimeInUTC = zonedDateTime.withZoneSameInstant(UTC_ZONE_ID);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        return Stream.of( //
                Arguments.of(toNamed(LocalDateTimeType.INSTANCE), localDateTime, localDateTimeInUTC), //
                Arguments.of(toNamed(InstantType.INSTANCE), instant, instantInUTC), //
                Arguments.of(toNamed(OffsetDateTimeType.INSTANCE), offsetDateTime, offsetDateTimeInUTC), //
                Arguments.of(toNamed(ZonedDateTimeType.INSTANCE), zonedDateTime, zonedDateTimeInUTC), //
                Arguments.of(toNamed(TimestampType.INSTANCE), Timestamp.from(instant), Timestamp.from(instant)), //
                Arguments.of(toNamed(TimestampType.INSTANCE), date, new Timestamp(date.getTime())), //
                Arguments.of(toNamed(CalendarType.INSTANCE), calendar, new Timestamp(calendar.getTimeInMillis())), //
                Arguments.of(toNamed(CalendarType.INSTANCE), gregorianCalendar, new Timestamp(gregorianCalendar.getTimeInMillis())) //
        );
    }

    private static <T> Named<T> toNamed(T object) {
        return Named.of(object.getClass().getSimpleName(), object);
    }
}
