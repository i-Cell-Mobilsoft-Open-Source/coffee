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

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
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
 * {@link ArgumentsProvider} to providing {@link java.sql.Time} test cases for BatchService.
 *
 * @author csaba.balogh
 * @version 2.0.0
 */
public class TimeArgumentsProvider implements ArgumentsProvider {

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        ZoneOffset systemZoneOffset = OffsetTime.now().getOffset();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();

        // JDBC_TIME_BASIC_TYPE
        Time jdbcTime = Time.valueOf(localTime);
        Time jdbcTimeWithoutTimeZone = Time.valueOf(localTime);
        Time jdbcTimeWithTimeZone = Time.valueOf(localTime);

        // LOCAL_TIME_BASIC_TYPE
        LocalTime localTimeInSystemDefault = OffsetTime.of(localTime, systemZoneOffset).toLocalTime();
        Time localTimeTimeWithoutTimeZone = Time.valueOf(localTime);
        Time localTimeTimeWithTimeZone = Time.valueOf(localTimeInSystemDefault);

        // OFFSET_TIME_BASIC_TYPE
        OffsetTime offsetTime = localTime.atOffset(systemZoneOffset);
        Time offsetTimeTimeWithoutTimeZone = Time.valueOf(offsetTime.toLocalTime());
        Time offsetTimeTimeWithTimeZone = Time.valueOf(offsetTime.toLocalTime());

        // UTIL_DATE_TIME_BASIC_TYPE
        Date date = DateUtil.toDate(localDate);
        Time dateTimeWithoutTimeZone = new Time(date.getTime());
        Time dateTimeWithTimeZone = new Time(date.getTime());

        // CALENDAR_TIME_BASIC_TYPE
        Calendar calendar = DateUtil.toCalendar(date);
        Time calendarTimeWithoutTimeZone = new Time(calendar.getTimeInMillis());
        Time calendarTimeWithTimeZone = new Time(calendar.getTimeInMillis());

        return Stream.of( //
                toArguments(TestBasicTypes.JDBC_TIME_BASIC_TYPE, jdbcTime, jdbcTimeWithoutTimeZone, jdbcTimeWithTimeZone), //
                toArguments(TestBasicTypes.LOCAL_TIME_BASIC_TYPE, localTime, localTimeTimeWithoutTimeZone, localTimeTimeWithTimeZone), //
                toArguments(TestBasicTypes.OFFSET_TIME_BASIC_TYPE, offsetTime, offsetTimeTimeWithoutTimeZone, offsetTimeTimeWithTimeZone), //
                toArguments(TestBasicTypes.UTIL_DATE_TIME_BASIC_TYPE, date, dateTimeWithoutTimeZone, dateTimeWithTimeZone), //
                toArguments(TestBasicTypes.CALENDAR_TIME_BASIC_TYPE, calendar, calendarTimeWithoutTimeZone, calendarTimeWithTimeZone) //
        );
    }

    private static Arguments toArguments(BasicType<?> basicType, Object value, Object valueWithoutTimeZone, Object ValueWithTimeZone) {
        return Arguments.of(toNamed(basicType), value, valueWithoutTimeZone, ValueWithTimeZone);
    }

    private static Named<BasicType<?>> toNamed(BasicType<?> basicType) {
        return Named.of(basicType.getJavaType().getSimpleName(), basicType);
    }

}
