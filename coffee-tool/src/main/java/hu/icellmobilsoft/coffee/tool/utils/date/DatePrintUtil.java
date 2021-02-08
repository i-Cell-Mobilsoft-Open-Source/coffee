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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

/**
 * Date print util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class DatePrintUtil {

    /**
     * TemporalAccessor (LocalDate) to String. Output format is '2011-12-03'.
     *
     * @param temporal
     *            {@link TemporalAccessor} (interface for java.time types) to be formatted
     * @return {@link DateTimeFormatter#ISO_DATE} formatted {@code String}
     * @see DateTimeFormatter#ISO_DATE
     */
    public static String isoDate(TemporalAccessor temporal) {
        if (temporal == null) {
            return null;
        }
        return DateTimeFormatter.ISO_DATE.format(temporal);
    }

    /**
     * OffsetTime to String. Output format is '10:15:30+01:00'.
     *
     * @param temporal
     *            {@link TemporalAccessor} (interface for java.time types) to be formatted
     * @return {@link DateTimeFormatter#ISO_OFFSET_TIME} formatted {@code String}
     * @see DateTimeFormatter#ISO_OFFSET_TIME
     */
    public static String isoOffsetTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_TIME.format(temporal);
    }

    /**
     * TemporalAccessor to String. Output format is '2011-12-03T10:15:30+01:00'.
     *
     * @param temporal
     *            {@link TemporalAccessor} (interface for java.time types) to be formatted
     * @return {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} formatted {@code String}
     * @see DateTimeFormatter#ISO_OFFSET_DATE_TIME
     */
    public static String isoOffsetDateTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(temporal);
    }

    /**
     * Prints java time in custom format.
     *
     * @param temporalAccessor
     *            {@link TemporalAccessor} (interface for java.time types) to be formatted
     * @param pattern
     *            nullable custom date format pattern, if null then toString() is used which is ISO.
     * @return {@code String} in desired format
     */
    public static String printDate(TemporalAccessor temporalAccessor, String pattern) {
        if (temporalAccessor == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            // java time implementációknál a típusnak megfelő ISO formátumban adja vissza
            return temporalAccessor.toString();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(temporalAccessor);
    }

    /**
     * Prints {@code Date} in custom format.
     *
     * @param date
     *            {@code Date} to print
     * @param pattern
     *            nullable custom date format pattern, if null then {@link Date#toString()} is used
     * @return {@code String} in desired format
     */
    public static String printDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            return date.toString();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * Prints {@code Calendar} in custom format.
     *
     * @param cal
     *            {@code Calendar} to print
     * @param pattern
     *            nullable custom date format pattern
     * @return {@code String} in desired format
     */
    public static String printCalendar(Calendar cal, String pattern) {
        if (cal == null) {
            return null;
        }
        return printDate(cal.getTime(), pattern);
    }
}
