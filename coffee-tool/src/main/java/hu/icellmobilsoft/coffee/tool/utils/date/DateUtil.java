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

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Date util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class DateUtil {

    private static Logger LOGGER = Logger.getLogger(DateUtil.class);

    /** Constant <code>DEFAULT_FULL_PATTERN="yyyy-MM-dd'T'HH:mm:ss.SSSZ"</code> */
    public static final String DEFAULT_FULL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Creates a new calendar instance from a {@link Date}.
     *
     * @param date
     *            {@code Date} to convert
     * @return {@link Calendar} or null if {@code date} is null
     */
    public static Calendar toCalendar(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Converts to {@link Calendar}, then changes value and converts back to {@link Date}.
     *
     * @param date
     *            {@code Date} to add value to
     * @param value
     *            value to change the {@code date} with
     * @param measureUnit
     *            unit of {@code value}
     * @return modified {@code Date} or null if {@code date} is null
     */
    public static Date addValueToDate(Date date, int value, int measureUnit) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(measureUnit, value);
        return calendar.getTime();
    }

    /**
     * Parses {@code String} to {@link Date}.
     * 
     * @param dateString
     *            value to parse
     * @param pattern
     *            to parse the dateString with
     * @return parsed {@code Date} or null if {@code dateString} or {@code pattern} is null
     */
    public static Date parse(String dateString, String pattern) {
        if (StringUtils.isBlank(pattern) || StringUtils.isBlank(dateString)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateString);
        } catch (ParseException e) {
            LOGGER.error("Parsing stringDate [" + dateString + "] with pattern: [" + pattern + "] to Date failed: " + e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Returns the number of calendar days between 2 dates. Independent of summer time change. For example: 2020.01.01 23:59:59 - 2020.01.02 00:00:01.
     * The result is 1 day.
     *
     * @param dateFrom
     *            date from
     * @param dateTo
     *            date to
     * @return number of days or 0 if {@code dateFrom} or {@code dateTo} is null
     */
    public static long daysBetween(Date dateFrom, Date dateTo) {
        if (dateFrom == null || dateTo == null) {
            return 0;
        }
        LocalDate fromDate = dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toDate = dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Math.abs(ChronoUnit.DAYS.between(fromDate, toDate));
    }

    /**
     * Returns given {@link Date} without time part.
     *
     * @param date
     *            {@code Date} to clear
     * @return cleared {@code Date} or null if {@code date} is null
     */
    public static Date clearTimePart(Date date) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return clearTimePart(cal).getTime();
    }

    /**
     * Returns given {@link Calendar} without hour, minute, second and millis parts. Does not clear ZONE_OFFSET!
     *
     * @param cal
     *            {@code Calendar} to clear
     * @return cleared {@code Calendar} or null if {@code cal} is null
     */
    public static Calendar clearTimePart(final Calendar cal) {
        if (cal == null) {
            return null;
        }
        Calendar calendar = (Calendar) cal.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Returns the elapsed number of seconds of a {@link Date}.
     *
     * @param date
     *            {@code Date} to calculate with
     * @return elapsed seconds of the {@code date} or 0 if {@code date} is null
     */
    public static int secondInDay(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        return hour * 60 * 60 + min * 60 + sec;
    }

    /**
     * Sets given {@link Date}'s time part to end of day (23:59:59:999) then returns it.
     *
     * @param day
     *            {@code Date} to calculate with
     * @return end time of {@code day} or null if {@code day} is null
     */
    public static Date endTimeOfDate(Date day) {
        if (day == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * java 8 {@link ZonedDateTime} from ISO format {@code String} date
     *
     * @param stringISODate
     *            {@value #DEFAULT_FULL_PATTERN}
     * @return parsed {@code ZonedDateTime} or null if unable to parse
     */
    public static ZonedDateTime toZoneDateTime(String stringISODate) {
        if (StringUtils.isBlank(stringISODate)) {
            return null;
        }
        ZonedDateTime zonedDateTime = null;
        try {
            DateTimeFormatter f = DateTimeFormatter.ISO_INSTANT;
            zonedDateTime = ZonedDateTime.parse(stringISODate, f).withZoneSameInstant(ZoneId.systemDefault());
        } catch (DateTimeParseException e1) {
            try {
                DateTimeFormatter f = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                zonedDateTime = ZonedDateTime.parse(stringISODate, f).withZoneSameInstant(ZoneId.systemDefault());
            } catch (DateTimeParseException e2) {
                LOGGER.error("Error in stringISODate parsing [" + stringISODate + "] with formats ISO_INSTANT and ISO_OFFSET_DATE_TIME:", e2);
            }
        }
        return zonedDateTime;
    }

    /**
     * Convert {@link Calendar} to {@link GregorianCalendar}
     * 
     * @param c
     *            {@code Calendar} to convert
     * @return {@code GregorianCalendar} instance or null if {@code c} is null
     */
    protected static GregorianCalendar toGregorianCalendar(Calendar c) {
        if (c == null) {
            return null;
        }
        GregorianCalendar gc;
        if (c instanceof GregorianCalendar) {
            gc = (GregorianCalendar) c.clone();
        } else {
            gc = new GregorianCalendar();
            gc.setTimeInMillis(c.getTimeInMillis());
        }
        return gc;
    }

    /**
     * {@link Calendar} to {@link ZonedDateTime} converter {@code Calendar} belso idozona szerint (classic)
     *
     * @param cal
     *            {@code Calendar} to convert
     * @return {@code ZonedDateTime} instance or null if {@code cal} is null
     */
    public static ZonedDateTime toZonedDateTime(Calendar cal) {
        if (cal == null) {
            return null;
        }
        if (cal instanceof GregorianCalendar) {
            return ((GregorianCalendar) cal).toZonedDateTime();
        } else
            return ZonedDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId());
    }

    /**
     * {@link Date} to {@link ZonedDateTime} converter with system default zone id
     *
     * @param date
     *            {@code Date} to convert
     * @return {@code ZonedDateTime} instance or null if {@code date} is null
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * {@link Date} to {@link OffsetDateTime} converter with system default zone id
     *
     * @param date
     *            {@code Date} to convert
     * @return {@code OffsetDateTime} instance or null if {@code date} is null
     */
    public static OffsetDateTime toOffsetDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * {@link LocalDateTime} - {@link OffsetDateTime} converter with system default zone id
     *
     * @param localDateTime
     *            {@code LocalDateTime} to convert
     * @return {@code OffsetDateTime} the offset date time in UTC
     */
    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        ZonedDateTime serverZonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return serverZonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toOffsetDateTime();
    }

    /**
     * {@link Date} to {@link LocalDate} converter with system default zone id
     *
     * @param date
     *            {@code Date} to convert
     * @return {@code LocalDate} instance or null if {@code date} is null
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return toZonedDateTime(date).toLocalDate();
    }

    /**
     * {@link Date} to {@link LocalTime} converter with system default zone id
     *
     * @param date
     *            {@code Date} to convert
     * @return {@code LocalTime} instance or null if {@code date} is null
     */
    public static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        return toZonedDateTime(date).toLocalTime();
    }

    /**
     * Returns the number of months between two {@link Date}s. The "from" month is inclusive, the "to" is exclusive.
     *
     * @param dateFrom
     *            from {@code Date}, inclusive
     * @param dateTo
     *            to {@code Date}, exclusive
     * @return Number of months between {@code dateFrom} and {@code dateTo} or null if {@code dateFrom} or {@code dateTo} is null
     */
    public static long monthsBetween(Date dateFrom, Date dateTo) {
        if (dateFrom == null || dateTo == null) {
            return 0;
        }
        OffsetDateTime from = toOffsetDateTime(dateFrom);
        OffsetDateTime to = toOffsetDateTime(dateTo);
        return ChronoUnit.MONTHS.between(from, to);
    }

    /**
     * {@link ZonedDateTime} to {@link Date} converter
     *
     * @param zonedDateTime
     *            {@code ZonedDateTime} to convert
     * @return {@code Date} instance or null if {@code zonedDateTime} is null
     */
    public static Date toDate(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * {@link OffsetDateTime} to {@link Date} converter
     *
     * @param offsetDateTime
     *            {@code OffsetDateTime} to convert
     * @return {@code Date} instance or null if {@code offsetDateTime} is null
     */
    public static Date toDate(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return Date.from(offsetDateTime.toInstant());
    }

    /**
     * {@link YearMonth} to {@link ZonedDateTime} converter using start of first day in given month with system default zone id
     *
     * @param yearMonth
     *            {@code YearMonth} to convert
     * @return {@code ZonedDateTime} instance with start of first day or null if {@code yearMonth} is null
     */
    public static ZonedDateTime startZonedDateTime(YearMonth yearMonth) {
        if (yearMonth == null) {
            return null;
        }
        return yearMonth.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault());
    }

    /**
     * {@link YearMonth} to {@link ZonedDateTime} converter using end of last day in given month with system default zone id
     *
     * @param yearMonth
     *            {@code YearMonth} to convert
     * @return {@code ZonedDateTime} instance with end of last day or null if {@code yearMonth} is null
     */
    public static ZonedDateTime endZonedDateTime(YearMonth yearMonth) {
        if (yearMonth == null) {
            return null;
        }
        return yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
    }

    /**
     * {@link Date} to {@link LocalDateTime} converter with system default zone id
     *
     * @param date
     *            {@code Date} to convert
     * @return {@code LocalDateTime} instance or null if {@code date} is null
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * {@link LocalDateTime} to {@link Date} converter with system default zone id
     *
     * @param localDateTime
     *            {@code LocalDateTime} to convert
     * @return {@code Date} instance or null if {@code localDateTime} is null
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Returns a {@link Date} instance created from {@link LocalDate} with system default zone id
     *
     * @param localDate
     *            localDate to convert
     * @return {@code Date} instance or null if {@code localDate} is empty
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Actual date + time in UTC. When it is serialized, the local 2020-08-26 10:34:28 "2020-08-26T08:34:28.955024Z" format takes place instead of the
     * default "2020-08-26T10:34:28.955024+02:00"
     *
     * @return {@code OffsetDateTime}, example: "2020-08-26T08:34:28.955024Z"
     */
    public static OffsetDateTime nowUTC() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Actual date + time in UTC. When it is serialized, the local 2020-08-26 10:34:28 "2020-08-26T08:34:28.955024Z" format takes place instead of the
     * default "2020-08-26T10:34:28.955024+02:00"
     *
     * @return {@code OffsetDateTime}, example: "2020-08-26T08:34:28.955Z"
     */
    public static OffsetDateTime nowUTCTruncatedToMillis() {
        return OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    /**
     * Returns the last day of the month of the target date.
     *
     * @param baseDate
     *            {@code LocalDate} the base date
     * @return {@code LocalDate}, the last day of the month of the target date.
     */
    public static LocalDate lastDayOfMonth(LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }
        return LocalDate.of(baseDate.getYear(), baseDate.getMonth(), baseDate.lengthOfMonth());
    }

    /**
     * Returns the last day of quarter-year of the target date.
     *
     * @param baseDate
     *            {@code LocalDate} the base date
     * @return {@code LocalDate}, the last day of quarter-year of the target date.
     */
    public static LocalDate lastDayOfQuarter(LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }
        // Same year
        int year = baseDate.getYear();
        // First month of the quarter
        Month firstMonthOfQuarter = baseDate.getMonth().firstMonthOfQuarter();
        // Last month of the quarter
        Month lastMonthOfTheQuarter = firstMonthOfQuarter.plus(2);
        return LocalDate.of(year, lastMonthOfTheQuarter, lastMonthOfTheQuarter.length(Year.isLeap(year)));
    }

    /**
     * Returns the last day of the target date's year.
     * 
     * @param baseDate
     *            {@code LocalDate} the base date
     * @return {@code LocalDate} the last day of the target date's year. (yyyy.12.31)
     */
    public static LocalDate lastDayOfYear(LocalDate baseDate) {
        if (baseDate == null) {
            return null;
        }
        int year = baseDate.getYear();
        return LocalDate.of(year, Month.DECEMBER, 31);
    }

    /**
     * Parses a date as {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} format. In case of any formatting problem, it throws {@link BaseException}.
     *
     * @param isoDateTime
     *            date to be parsed {@code String}
     * @return {@code OffsetDateTime} date time object
     * @throws BaseException
     *             in case of bad date format
     */
    public static OffsetDateTime tryToParseToOffsetDateTime(String isoDateTime) throws BaseException {
        if (StringUtils.isBlank(isoDateTime)) {
            return null;
        }

        try {
            return OffsetDateTime.parse(isoDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new BaseException(CoffeeFaultType.INVALID_INPUT, MessageFormat.format("Invalid ISO date time format: [{0}]", isoDateTime));
        }
    }
}
