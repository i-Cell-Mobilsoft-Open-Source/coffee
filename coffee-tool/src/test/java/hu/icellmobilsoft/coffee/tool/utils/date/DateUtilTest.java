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
package hu.icellmobilsoft.coffee.tool.utils.date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing DateUtil")
class DateUtilTest {

    private static final String DATE_PATTERN = "yyyy.MM.dd. HH:mm:ss.SSSX";

    private static ZonedDateTime zonedDateTime;
    private static OffsetDateTime offsetDateTime;
    private static LocalDate localDate;
    private static LocalDateTime localDateTime;
    private static LocalTime localTime;
    private static Calendar calendar;
    private static GregorianCalendar gregorianCalendar;
    private static Date date;
    private static Calendar dateOnly;
    private static Date timelessDate;
    private static Date endOfDay;
    private static Date datePlusTwoDays;
    private static Date datePlusTwoMonths;
    private static ZonedDateTime startOfMonth;
    private static ZonedDateTime endOfMonth;
    private static LocalDate lastDayOfQuarter;
    private static LocalDate lastDayOfYear;
    private static String isoDateTime;

    @BeforeAll
    static void setUpBeforeClass() {
        // TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        // Base dateTime is // 2019-02-11T15:23:34.051Z -- in epoch millis(UTC): 1549898614051
        zonedDateTime = ZonedDateTime.of(2019, 2, 11, 15, 23, 34, 51000000, ZoneOffset.UTC);

        zonedDateTime = zonedDateTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());

        offsetDateTime = zonedDateTime.toOffsetDateTime();

        date = new Date(1549898614051L); // 2019-02-11T15:23:34.051Z
        dateOnly = DateUtil.clearTimePart(Calendar.getInstance());
        dateOnly.set(2019, Calendar.FEBRUARY, 11);

        localDate = LocalDate.of(2019, 2, 11);
        localDateTime = LocalDateTime.ofInstant(date.toInstant(), TimeZone.getDefault().toZoneId());
        localTime = LocalTime.now(Clock.fixed(date.toInstant(), TimeZone.getDefault().toZoneId()));

        // 00:00:00 in default timezone
        Instant startTimeInstant = ZonedDateTime.of(2019, 2, 11, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()).toInstant();
        timelessDate = Date.from(startTimeInstant);// new Date(1549843200000l);

        // 23:59:59.999 in default timezone
        Instant endTimeInstant = ZonedDateTime.of(2019, 2, 11, 23, 59, 59, 999000000, TimeZone.getDefault().toZoneId()).toInstant();
        endOfDay = Date.from(endTimeInstant);

        datePlusTwoDays = new Date(1550071414051L); // 2019-02-13T15:23:34.051Z
        datePlusTwoMonths = new Date(1554996214051L); // 2019-04-11T15:23:34.051Z
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalendar.setTime(date);

        startOfMonth = ZonedDateTime.of(2019, 2, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        endOfMonth = ZonedDateTime.of(2019, 2, 28, 23, 59, 59, 999999999, ZoneId.systemDefault());

        lastDayOfQuarter = LocalDate.of(2019, 3, 31);
        lastDayOfYear = LocalDate.of(2019, 12, 31);

        isoDateTime = "2019-02-11T16:23:34.051+01:00"; // ISO-8601 standard
    }

    @Nested
    @DisplayName("Testing toCalendar()")
    class ToCalendarTest {

        @Test
        @DisplayName("Testing toCalendar() from Date")
        void fromDate() {
            // given

            // when
            Calendar actual = DateUtil.toCalendar(date);

            // then
            assertNotNull(actual);
            assertEquals(actual.compareTo(calendar), 0);
        }
    }

    @Test
    @DisplayName("Testing addValueToDate()")
    void addValueToDate() {
        // given

        // when
        Date actual = DateUtil.addValueToDate(date, 2, Calendar.DAY_OF_MONTH);

        // then
        assertEquals(datePlusTwoDays, actual);
    }

    @Test
    @DisplayName("Testing parse()")
    void parse() {
        // given

        // when
        Date actual = DateUtil.parse("2019.02.11. 15:23:34.051Z", DATE_PATTERN);

        // then
        assertEquals(date, actual);
    }

    @Test
    @DisplayName("Testing daysBetween()")
    void daysBetween() {
        // given

        // when
        long days = DateUtil.daysBetween(date, datePlusTwoDays);

        // then
        assertEquals(2, days);
    }

    @Nested
    @DisplayName("Testing clearTimePart()")
    class ClearTimePartTest {
        @Test
        @DisplayName("Testing clearTimePart() from Date")
        void fromDate() {
            // given

            // when
            Date actual = DateUtil.clearTimePart(date);

            // then
            assertEquals(timelessDate, actual);
        }

        @Test
        @DisplayName("Testing clearTimePart() from Calendar")
        void fromCalendar() {
            // given
            Calendar expected = Calendar.getInstance();
            expected.set(2019, 1, 11, 0, 0, 0);
            expected.set(Calendar.MILLISECOND, 0);

            // when
            Calendar actual = DateUtil.clearTimePart(calendar);

            // then
            assertEquals(0, expected.compareTo(actual));
        }
    }

    @Nested
    @DisplayName("Testing toZonedDateTime()")
    class ToZonedDateTimeTest {
        @Test
        @DisplayName("Testing toZonedDateTime() from Date")
        void fromDate() {
            // given

            // when
            ZonedDateTime actual = DateUtil.toZonedDateTime(date);

            // then
            assertEquals(zonedDateTime, actual);
        }

        @Test
        @DisplayName("Testing toZonedDateTime() from Calendar")
        void fromCalendar() {
            // given
            Calendar expected = Calendar.getInstance();
            expected.set(2019, 1, 11, 0, 0, 0);
            expected.set(Calendar.MILLISECOND, 0);

            // when
            ZonedDateTime actual = DateUtil.toZonedDateTime(calendar);

            // then
            assertEquals(zonedDateTime, actual);
        }
    }

    @Test
    @DisplayName("Testing toOffsetDateTime() from Date")
    void toOffsetDateTimeFromDate() {
        // given

        // when
        OffsetDateTime actual = DateUtil.toOffsetDateTime(date);

        // then
        assertTrue(offsetDateTime.isEqual(actual));
    }

    @Test
    @DisplayName("Testing toOffsetDateTime() from LocalDateTime")
    void toOffsetDateTimeFromLocalDateTime() {
        // given

        // when
        OffsetDateTime actual = DateUtil.toOffsetDateTime(localDateTime);

        // then
        assertTrue(offsetDateTime.isEqual(actual));
    }

    @Test
    @DisplayName("Testing startTimeOfDate()")
    void startTimeOfDate() {
        // given

        // when
        Date actual = DateUtil.clearTimePart(date);

        // then
        assertEquals(timelessDate, actual);
    }

    @Test
    @DisplayName("Testing secondInDay()")
    void secondInDay() {
        // given
        int expected = ((zonedDateTime.getHour() * 60) + zonedDateTime.getMinute()) * 60 + zonedDateTime.getSecond();

        // when
        int actual = DateUtil.secondInDay(date);

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing endTimeOfDate()")
    void endTimeOfDate() {
        // given

        // when
        Date actual = DateUtil.endTimeOfDate(date);

        // then
        assertEquals(endOfDay, actual);
    }

    @Nested
    @DisplayName("Testing toZoneDateTime()")
    class ToZoneDateTimeTest {
        @Test
        @DisplayName("Testing toZoneDateTime() from ISO")
        void fromISO() {
            // given
            String input = "2019-02-11T15:23:34.051Z";

            // when
            ZonedDateTime actual = DateUtil.toZoneDateTime(input);

            // then
            assertTrue(zonedDateTime.isEqual(actual));
        }

        @Test
        @DisplayName("Testing toZoneDateTime() from ISO with offset")
        void fromISOWithOffset() {
            // given
            String input = "2019-02-11T16:23:34.051+01:00";

            // when
            ZonedDateTime actual = DateUtil.toZoneDateTime(input);

            // then
            assertTrue(zonedDateTime.isEqual(actual));
        }
    }

    @Test
    @DisplayName("Testing toGregorianCalendar() from Calendar")
    void toGregorianCalendar() {
        // given
        // when
        GregorianCalendar actual = DateUtil.toGregorianCalendar(calendar);

        // then
        assertEquals(actual, gregorianCalendar);
    }

    @Test
    @DisplayName("Testing toLocalDate() from Date")
    void toLocalDate() {
        // given
        // when
        LocalDate actual = DateUtil.toLocalDate(date);

        // then
        assertEquals(actual, localDate);
    }

    @Test
    @DisplayName("Testing toLocalTime() from Date")
    void toLocalTime() {
        // given
        // when
        LocalTime actual = DateUtil.toLocalTime(date);

        // then
        assertEquals(localTime, actual);
    }

    @Test
    @DisplayName("Testing monthsBetween()")
    void monthsBetween() {
        // given
        // when
        Long actual = DateUtil.monthsBetween(date, datePlusTwoMonths);

        // then
        assertEquals(2, actual);
    }

    @Test
    @DisplayName("Testing toDate() from ZonedDateTime")
    void toDateFromZonedDateTime() {
        // given
        // when
        Date actual = DateUtil.toDate(zonedDateTime);

        // then
        assertEquals(date, actual);
    }

    @Test
    @DisplayName("Testing toDate() from OffsetDateTime")
    void toDateFromOffsetDateTime() {
        // given
        // when
        Date actual = DateUtil.toDate(offsetDateTime);

        // then
        assertEquals(date, actual);
    }

    @Test
    @DisplayName("Testing startZonedDateTime() from YearMonth")
    void startZonedDateTime() {
        // given
        // when
        ZonedDateTime actual = DateUtil.startZonedDateTime(YearMonth.of(2019, 2));

        // then
        assertEquals(startOfMonth, actual);
    }

    @Test
    @DisplayName("Testing endZonedDateTime() from YearMonth")
    void endZonedDateTime() {
        // given
        // when
        ZonedDateTime actual = DateUtil.endZonedDateTime(YearMonth.of(2019, 2));

        // then
        assertEquals(endOfMonth, actual);
    }

    @Test
    @DisplayName("Testing toLocalDateTime() from Date")
    void toLocalDateTime() {
        // given
        // when
        LocalDateTime actual = DateUtil.toLocalDateTime(date);

        // then
        assertEquals(localDateTime, actual);
    }

    @Test
    @DisplayName("Testing toDate() from LocalDateTime")
    void toDateFromLocalDateTime() {
        // given
        // when
        Date actual = DateUtil.toDate(localDateTime);

        // then
        assertEquals(date, actual);
    }

    @Test
    @DisplayName("Testing toDate() from LocalDate")
    void toDateFromLocalDate() {
        // given
        // when
        Date actual = DateUtil.toDate(localDate);

        // then
        assertEquals(dateOnly.getTime(), actual);
    }

    @Test
    @DisplayName("Testing nowUTC()")
    void nowUtc() {
        // given

        // when
        OffsetDateTime actual = DateUtil.nowUTC();

        // then
        assertNotNull(actual);
    }

    @Test
    @DisplayName("Testing nowUTCTruncatedToMillis()")
    void nowUTCTruncatedToMillis() {
        // given

        // when
        OffsetDateTime actual = DateUtil.nowUTCTruncatedToMillis();

        // then
        assertNotNull(actual);
    }

    @Test
    @DisplayName("Testing lastDayOfMonth() from LocalDate")
    void lastDayOfMonth() {
        // given
        LocalDate expected = endOfMonth.toLocalDate();
        // when
        LocalDate actual = DateUtil.lastDayOfMonth(localDate);
        // then
        assertEquals(actual.compareTo(expected), 0);
    }

    @Test
    @DisplayName("Testing lastDayOfQuarter() from LocalDate")
    void lastDayOfQuarter() {
        // given

        // when
        LocalDate actual = DateUtil.lastDayOfQuarter(localDate);
        // then
        assertEquals(actual.compareTo(lastDayOfQuarter), 0);
    }

    @Test
    @DisplayName("Testing lastDayOfYear() from LocalDate")
    void lastDayOfYearTest() {
        // given

        // when
        LocalDate actual = DateUtil.lastDayOfYear(localDate);
        // then
        assertEquals(actual.compareTo(lastDayOfYear), 0);
    }

    @Nested
    @DisplayName("Testing tryToParseToOffsetDateTime()")
    class TryToParseToOffsetDateTimeTest {

        @Test
        @DisplayName("Testing tryToParseToOffsetDateTime() from valid isoDateTime")
        void tryToParseToOffsetDateTime() throws Exception {
            // given

            // when
            OffsetDateTime actual = DateUtil.tryToParseToOffsetDateTime(isoDateTime);

            // then
            assertEquals(actual.compareTo(offsetDateTime), 0);
        }

        @Test
        @DisplayName("Testing tryToParseToOffsetDateTime() from valid isoDateTime")
        void tryToParseToOffsetDateTimeShouldThrowException() throws Exception {
            // given
            String invalidIsoDateTime = "invalidIsoDate";
            // when

            // then
            assertThrows(BaseException.class, () -> DateUtil.tryToParseToOffsetDateTime(invalidIsoDateTime));
        }
    }

    @Test
    @DisplayName("Testing null conversions")
    void testNullConversions() throws BaseException {
        // given
        Date nullDate = null;
        Calendar nullCalendar = null;
        ZonedDateTime nullZonedDateTime = null;
        LocalDateTime nullLocalDateTime = null;
        OffsetDateTime nullOffsetDateTime = null;
        LocalDate nullLocalDate = null;

        // when, then
        assertNull(DateUtil.toCalendar(nullDate));
        assertNull(DateUtil.addValueToDate(null, 0, 0));
        assertNull(DateUtil.parse("dateString", null));
        assertNull(DateUtil.parse(null, "pattern"));
        assertEquals(0, DateUtil.daysBetween(null, null));
        assertEquals(0, DateUtil.daysBetween(new Date(), null));
        assertEquals(0, DateUtil.daysBetween(null, new Date()));
        assertNull(DateUtil.clearTimePart(nullDate));
        assertNull(DateUtil.clearTimePart(nullCalendar));
        assertEquals(0, DateUtil.secondInDay(null));
        assertNull(DateUtil.endTimeOfDate(null));
        assertNull(DateUtil.toZoneDateTime(null));

        assertNull(DateUtil.toGregorianCalendar(null));
        assertNull(DateUtil.toZonedDateTime(nullCalendar));
        assertNull(DateUtil.toZonedDateTime(nullDate));
        assertNull(DateUtil.toLocalDate(null));
        assertNull(DateUtil.toLocalTime(null));
        assertEquals(0, DateUtil.monthsBetween(null, null));
        assertNull(DateUtil.toDate(nullZonedDateTime));
        assertNull(DateUtil.startZonedDateTime(null));
        assertNull(DateUtil.endZonedDateTime(null));
        assertNull(DateUtil.toLocalDateTime(null));
        assertNull(DateUtil.toDate(nullLocalDateTime));
        assertNull(DateUtil.toOffsetDateTime(nullDate));
        assertNull(DateUtil.toOffsetDateTime(nullLocalDateTime));
        assertNull(DateUtil.toDate(nullOffsetDateTime));
        assertNull(DateUtil.toDate(nullLocalDate));
        assertNull(DateUtil.lastDayOfMonth(nullLocalDate));
        assertNull(DateUtil.lastDayOfQuarter(nullLocalDate));
        assertNull(DateUtil.lastDayOfYear(nullLocalDate));
        assertNull(DateUtil.tryToParseToOffsetDateTime(null));
    }

}
