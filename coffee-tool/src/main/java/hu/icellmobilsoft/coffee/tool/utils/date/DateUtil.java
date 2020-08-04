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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Date util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class DateUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(DateUtil.class);

    /** Constant <code>DEFAULT_FULL_PATTERN="yyyy-MM-dd'T'HH:mm:ss.SSSZ"</code> */
    public static final String DEFAULT_FULL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Creates a new calendar instance from a util Date.
     *
     * @param date
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
     * Convert to Calendar, change value and converts to Date.
     *
     * @param date
     * @param value
     * @param measureUnit
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
     * <p>parse.</p>
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
     * Gets back the number of calendar days between 2 dates.
     * For example: 2020.01.01 23:59:59 - 2020.01.02 00:00:01. The result is 1 day.
     * It is not depend on summer time change.
     *
     * @param dateFrom date from
     * @param dateTo   date to
     * @return number of days
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
     * Gets back a date without time part.
     *
     * @param date
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
     * Clearing hour, minute, second and millis. This not zeroing ZONE_OFFSET!
     *
     * @param cal
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
     * Gets back from a custom date the ellapsed seconds.
     *
     * @param date
     *            any date
     * @return ellapsed seconds from a day
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
     * Gets back the end time of date.
     *
     * @param day
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
     * java 8 ZonedDateTime from ISO format string date
     *
     * @param stringISODate
     *            {@value #DEFAULT_FULL_PATTERN}
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
     * <p>toGregorianCalendar.</p>
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
     * Calendar -> ZonedDateTime converter Calendar belso idozona szerint (classic)
     *
     * @param cal
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
     * Date -> ZonedDateTime converter lokalis idozona szerint (classic)
     *
     * @param date
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date -> OffsetDateTime converter lokalis idozona szerint (classic)
     *
     * @param date
     */
    public static OffsetDateTime toOffsetDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date -> LocalDate converter lokalis idozona szerint (classic)
     *
     * @param date
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return toZonedDateTime(date).toLocalDate();
    }

    /**
     * Date -> LocalTime converter lokalis idozona szerint (classic)
     *
     * @param date
     */
    public static LocalTime toLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        return toZonedDateTime(date).toLocalTime();
    }

    /**
     * Dátumok közti hónap kulönbség
     *
     * @param dateFrom
     * @param dateTo
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
     * ZonedDateTime -> java.util.Date converter
     *
     * @param zonedDateTime
     */
    public static Date toDate(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * OffsetDateTime -> java.util.Date converter
     *
     * @param offsetDateTime
     */
    public static Date toDate(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return Date.from(offsetDateTime.toInstant());
    }

    /**
     * Start ZonedDateTime of given YearMonth
     *
     * @param yearMonth
     */
    public static ZonedDateTime startZonedDateTime(YearMonth yearMonth) {
        if (yearMonth == null) {
            return null;
        }
        return yearMonth.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault());
    }

    /**
     * End ZonedDateTime of given YearMonth
     *
     * @param yearMonth
     */
    public static ZonedDateTime endZonedDateTime(YearMonth yearMonth) {
        if (yearMonth == null) {
            return null;
        }
        return yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
    }

    /**
     * java.util.Date -> java.time.LocalDateTime konverter
     *
     * @param date
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * java.time.LocalDateTime -> java.util.Date konverter
     *
     * @param localDateTime
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
