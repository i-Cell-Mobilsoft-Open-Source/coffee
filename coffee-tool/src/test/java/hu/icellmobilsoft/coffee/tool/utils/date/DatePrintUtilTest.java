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
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author imre.scheffer
 */
@DisplayName("Testing DatePrintUtil")
public class DatePrintUtilTest {

    private static final String DATE_PATTERN = "yyyy.MM.dd. HH:mm:ss.SSSX";

    private static ZonedDateTime zonedDateTime;
    private static Date date;
    private static Calendar calendar;

    @BeforeAll
    static void setUpBeforeClass() {
        // TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        // Base dateTime is // 2019-02-11T15:23:34.051Z -- in epoch millis(UTC): 1549898614051
        zonedDateTime = ZonedDateTime.of(2019, 2, 11, 15, 23, 34, 51000000, ZoneOffset.UTC);

        zonedDateTime = zonedDateTime.withZoneSameInstant(TimeZone.getDefault().toZoneId());

        date = new Date(1549898614051L); // 2019-02-11T15:23:34.051Z

        calendar = Calendar.getInstance();
        calendar.setTime(date);
    }

    @Test
    @DisplayName("Testing printDate()")
    void printDate() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        String expected = formatter.format(zonedDateTime);

        // when
        String actual = DatePrintUtil.printDate(date, DATE_PATTERN);

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing printCalendar()")
    void printCalendar() {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        String expected = formatter.format(zonedDateTime);

        // when
        String actual = DatePrintUtil.printCalendar(calendar, DATE_PATTERN);

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing null conversions")
    void testNullConversions() {
        // given
        Date nullDate = null;
        Calendar nullCalendar = null;

        // when, then
        assertNull(DatePrintUtil.printDate(nullDate, "pattern"));
        assertEquals(date.toString(), DatePrintUtil.printDate(date, null));
        assertNull(DatePrintUtil.printCalendar(nullCalendar, "pattern"));
        assertEquals(calendar.getTime().toString(), DatePrintUtil.printCalendar(calendar, null));
    }
}
