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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import org.hibernate.type.BasicType;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import hu.icellmobilsoft.coffee.jpa.sql.batch.constants.TestBasicTypes;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * {@link ArgumentsProvider} to providing {@link java.sql.Timestamp} test cases for BatchService.
 *
 * @author csaba.balogh
 * @version 2.0.0
 */
public class TimestampArgumentsProvider implements ArgumentsProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        ZoneOffset systemZoneOffset = OffsetTime.now().getOffset();
        LocalDateTime localDateTime = LocalDateTime.now();

        // JDBC_TIMESTAMP_BASIC_TYPE
        Timestamp jdbcTimestamp = Timestamp.valueOf(localDateTime);
        Timestamp jdbcTSWithoutTimeZone = Timestamp.valueOf(localDateTime);
        Timestamp jdbcTSWithTimeZone = Timestamp.valueOf(localDateTime);

        // LOCAL_DATE_TIME_BASIC_TYPE
        LocalDateTime localDateTimeInSystemDefault = OffsetDateTime.of(localDateTime, systemZoneOffset).toLocalDateTime();
        Timestamp localDateTimeTSWithoutTimeZone = Timestamp.valueOf(localDateTime);
        Timestamp localDateTimeTSWithTimeZone = Timestamp.valueOf(localDateTimeInSystemDefault);

        // OFFSET_DATE_TIME_BASIC_TYPE
        OffsetDateTime offsetDateTime = OffsetDateTime.of(localDateTime, systemZoneOffset);
        Timestamp offsetDateTimeTSWithoutTZ = Timestamp.valueOf(offsetDateTime.toLocalDateTime());
        Timestamp offsetDateTimeTSWithTZ = Timestamp.valueOf(offsetDateTime.toLocalDateTime());

        // ZONED_DATE_TIME_BASIC_TYPE
        ZonedDateTime zonedDateTime = offsetDateTime.toZonedDateTime();
        Timestamp zonedDateTimeTSWithoutTimeZone = Timestamp.valueOf(zonedDateTime.toLocalDateTime());
        Timestamp zonedDateTimeTSWithTimeZone = Timestamp.valueOf(zonedDateTime.toLocalDateTime());

        // INSTANT_BASIC_TYPE
        Instant instant = offsetDateTime.toInstant();
        Timestamp instantTSWithoutTimeZone = Timestamp.valueOf(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
        Timestamp instantTSWithTimeZone = Timestamp.valueOf(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());

        // DATE_TIMESTAMP_BASIC_TYPE
        Date date = DateUtil.toDate(localDateTime);
        Timestamp dateTSWithoutTimeZone = new Timestamp(date.getTime());
        Timestamp dateTSWithTimeZone = new Timestamp(date.getTime());

        // CALENDAR_TIMESTAMP_BASIC_TYPE
        Calendar calendar = DateUtil.toCalendar(date);
        Timestamp calendarTSWithoutTimeZone = new Timestamp(calendar.getTimeInMillis());
        Timestamp calendarTSWithTimeZone = new Timestamp(calendar.getTimeInMillis());

        return Stream.of( //
                toArguments(TestBasicTypes.JDBC_TIMESTAMP_BASIC_TYPE, jdbcTimestamp, jdbcTSWithoutTimeZone, jdbcTSWithTimeZone), //
                toArguments(TestBasicTypes.LOCAL_DATE_TIME_BASIC_TYPE, localDateTime, localDateTimeTSWithoutTimeZone, localDateTimeTSWithTimeZone), //
                toArguments(TestBasicTypes.OFFSET_DATE_TIME_BASIC_TYPE, offsetDateTime, offsetDateTimeTSWithoutTZ, offsetDateTimeTSWithTZ), //
                toArguments(TestBasicTypes.ZONED_DATE_TIME_BASIC_TYPE, zonedDateTime, zonedDateTimeTSWithoutTimeZone, zonedDateTimeTSWithTimeZone), //
                toArguments(TestBasicTypes.INSTANT_BASIC_TYPE, instant, instantTSWithoutTimeZone, instantTSWithTimeZone), //
                toArguments(TestBasicTypes.DATE_TIMESTAMP_BASIC_TYPE, date, dateTSWithoutTimeZone, dateTSWithTimeZone), //
                toArguments(TestBasicTypes.CALENDAR_TIMESTAMP_BASIC_TYPE, calendar, calendarTSWithoutTimeZone, calendarTSWithTimeZone) //
        );
    }

    private static Arguments toArguments(BasicType<?> basicType, Object value, Object valueWithoutTimeZone, Object ValueWithTimeZone) {
        return Arguments.of(toNamed(basicType), value, valueWithoutTimeZone, ValueWithTimeZone);
    }

    private static Named<BasicType<?>> toNamed(BasicType<?> basicType) {
        return Named.of(basicType.getJavaType().getSimpleName(), basicType);
    }

}
