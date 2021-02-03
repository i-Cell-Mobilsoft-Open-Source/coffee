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
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.enterprise.inject.Vetoed;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Date XML util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class DateXmlUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(DateXmlUtil.class);

    private static final String TIMESTAMP_CONVERT_ERROR_MSG = "Timestamp convert [{0}] to XmlGregorianCalendar error: ";

    /**
     * Converts {@link Calendar} to {@link XMLGregorianCalendar}
     *
     * @param c
     *            {@code Calendar} to convert
     * @return converted {@code XMLGregorianCalendar} or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Calendar c) {
        if (c == null) {
            return null;
        }
        try {
            if (c instanceof GregorianCalendar) {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) c);
            } else {
                GregorianCalendar gc = new GregorianCalendar(c.getTimeZone());
                gc.setTimeInMillis(c.getTimeInMillis());
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            }
        } catch (DatatypeConfigurationException e) {
            LOGGER.error(MessageFormat.format(TIMESTAMP_CONVERT_ERROR_MSG, c), e);
            return null;
        }
    }

    /**
     * Converts {@link Date} to {@link XMLGregorianCalendar}
     *
     * @param date
     *            {@code Date} to convert
     * @return converted {@code XMLGregorianCalendar} or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }
        return toXMLGregorianCalendar(DateUtil.toCalendar(date));
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link GregorianCalendar} with specific timezone offset.
     *
     * @param calendar
     *            {@code GregorianCalendar} to convert
     * @param offsetInMinutes
     *            timezone offset defined in minutes
     * @return converted {@code XMLGregorianCalendar} or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(GregorianCalendar calendar, int offsetInMinutes) {
        if (calendar == null) {
            return null;
        }
        XMLGregorianCalendar xmlCalendar;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(GregorianCalendar.YEAR),
                    calendar.get(GregorianCalendar.MONTH) + 1, calendar.get(GregorianCalendar.DAY_OF_MONTH), offsetInMinutes);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error(MessageFormat.format(TIMESTAMP_CONVERT_ERROR_MSG, calendar), e);
            return null;
        }

        xmlCalendar.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        xmlCalendar.setMinute(calendar.get(Calendar.MINUTE));
        xmlCalendar.setSecond(calendar.get(Calendar.SECOND));
        xmlCalendar.setMillisecond(calendar.get(Calendar.MILLISECOND));

        return xmlCalendar;
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link Date} with undefined timezone.
     *
     * @param date
     *            {@code Date} to convert
     * @return converted {@code XMLGregorianCalendar} or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarNoTimeZone(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return toXMLGregorianCalendar(calendar, DatatypeConstants.FIELD_UNDEFINED);
    }

    /**
     * Converts {@link XMLGregorianCalendar} to {@link Calendar}
     *
     * @param xmlGregorianCalendar
     *            {@code XMLGregorianCalendar} to convert
     * @return converted {@code Calendar} or null if error
     */
    public static Calendar toCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar();
    }

    /**
     * Converts {@link XMLGregorianCalendar} to {@link Date}
     *
     * @param xmlGregorianCalendar
     *            {@code XMLGregorianCalendar} to convert
     * @return converted {@code Date} or null if error
     */
    public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link String} ISO Date (such as 2011-12-03T10:15:30Z). Date is converted to current
     * timezone.
     *
     * @param stringISODate
     *            yyyy-MM-dd'T'HH:mm:ss.SSSZ format
     * @return converted {@code XMLGregorianCalendar} null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarFromISO(String stringISODate) {
        if (StringUtils.isBlank(stringISODate)) {
            return null;
        }
        try {
            ZonedDateTime zonedDateTime = DateUtil.toZoneDateTime(stringISODate);
            if (zonedDateTime != null) {
                GregorianCalendar gcal = GregorianCalendar.from(zonedDateTime);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            }
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("Error in stringISODate convert [" + stringISODate + "] to XmlGregorianCalendar:", e);
        }
        return null;
    }

    /**
     * Converts {@link OffsetDateTime} to {@link XMLGregorianCalendar}
     *
     * @param offsetDateTime
     *            {@code OffsetDateTime} to convert
     * @return converted {@code XMLGregorianCalendar} or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return toXMLGregorianCalendar(GregorianCalendar.from(offsetDateTime.toZonedDateTime()));
    }

    /**
     * Converts {@link LocalDate} to {@link XMLGregorianCalendar}
     *
     * @param localDate
     *            {@code LocalDate} to convert
     * @return converted {@code XMLGregorianCalendar} (eg. "2020-03-03Z") or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(),
                    DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED, 0);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error("Error in stringISODate convert [" + localDate.toString() + "] to XmlGregorianCalendar:", e);
        }
        return null;
    }

    /**
     * Creates a new {@link Date} instance from an {@link XMLGregorianCalendar} with timezone overwritten to local timezone. Use this when there is no
     * timestamp stored in database (oracle), but the input is in UTC.
     *
     * @param xmlGregorianCalendar
     *            {@code XMLGregorianCalendar} to convert
     * @return {@code Date} in local timezone or null if {@code xmlGregorianCalendar} is null
     */
    public static Date toDateAsLocal(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        Calendar cal = xmlGregorianCalendar.toGregorianCalendar();
        // az oracle figyelmen kivul hadja a timezone erteket sima date tipusu mezonel,
        // es ezert ugy tekintjuk az input ISO datumot mindha sajat idozonaban lenne
        cal.setTimeZone(TimeZone.getDefault());
        return cal.getTime();
    }

    /**
     * {@link XMLGregorianCalendar}-bol kiszedi a datumot (ido nullazva). Hasznalhato foleg birthDate mezoknel es ott ahol xsd:date tipus van
     * hasznalva.<br>
     * <br>
     * Azt a problemat orvosolja hogy long-kent beadott datum (UTC zero ido) json formatumban a {@code XMLGregorianCalendar} helyi idore forditja, es
     * igy mindig belekerul ido eltolodas.<br>
     * <ul>
     * <li>json datum: 1515452400000 (Tue Jan 09 00:00:00 CET 2018)</li>
     * <li>ebbol XMLGregorianCalendar: 2018-01-09T01:00:00+01:00</li>
     * <li>(hibas) toDate(XMLGregorianCalendar): 2018-01-09 01:00:00</li>
     * <li>(jo) toDateOnly(XMLGregorianCalendar): 2018-01-09 00:00:00</li>
     * </ul>
     *
     * @param xmlGregorianCalendar
     *            {@code XMLGregorianCalendar} to convert
     * @return {@code Date} without time part or null if {@code xmlGregorianCalendar} is null
     */
    public static Date toDateOnly(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        LocalDate localDate = toZonedDateTime(xmlGregorianCalendar).toLocalDate();
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link Calendar} with timezone overwritten to UTC.
     *
     * @see #toDateAsLocal(XMLGregorianCalendar)
     * @param c
     *            {@code Calendar} to convert
     * @return {@code XMLGregorianCalendar} in UTC or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarAsUTC(Calendar c) {
        if (c == null) {
            return null;
        }
        GregorianCalendar gc = DateUtil.toGregorianCalendar(c);
        XMLGregorianCalendar xmlCalendar = toXMLGregorianCalendar(gc, 0);

        if (xmlCalendar == null) {
            return null;
        }

        xmlCalendar.setMillisecond(gc.get(Calendar.MILLISECOND));
        return xmlCalendar;
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link Date} with timezone overwritten to UTC.
     *
     * @see #toDateAsLocal(XMLGregorianCalendar)
     * @param date
     *            {@code Date} to convert
     * @return {@code XMLGregorianCalendar} in UTC or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarAsUTC(Date date) {
        if (date == null) {
            return null;
        }
        return toXMLGregorianCalendarAsUTC(DateUtil.toCalendar(date));
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link Calendar} without time part in UTC (YYYY-MM-DDZ format).
     *
     * @param c
     *            {@code Calendar} to convert
     * @return {@code XMLGregorianCalendar} without time part in UTC or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarDateOnly(Calendar c) {
        if (c == null) {
            return null;
        }
        XMLGregorianCalendar xmlCalendar;
        try {
            xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                    c.get(Calendar.DAY_OF_MONTH), 0);
        } catch (DatatypeConfigurationException e) {
            LOGGER.error(MessageFormat.format(TIMESTAMP_CONVERT_ERROR_MSG, c), e);
            return null;
        }
        return xmlCalendar;
    }

    /**
     * Creates a new {@link XMLGregorianCalendar} instance from a {@link Date} without time part in UTC (YYYY-MM-DDZ format).
     *
     * @param date
     *            {@code Date} to convert
     * @return {@code XMLGregorianCalendar} without time part in UTC or null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarDateOnly(Date date) {
        if (date == null) {
            return null;
        }
        return toXMLGregorianCalendarDateOnly(DateUtil.toCalendar(date));
    }

    /**
     * {@link XMLGregorianCalendar} to {@link ZonedDateTime} converter with system default zone id
     *
     * @param xmlCal
     *            {@code XMLGregorianCalendar} to convert
     * @return {@code ZonedDateTime} instance or null if {@code xmlCal} is null
     */
    public static ZonedDateTime toZonedDateTime(XMLGregorianCalendar xmlCal) {
        if (xmlCal == null) {
            return null;
        }
        return xmlCal.toGregorianCalendar().toZonedDateTime();
    }

    /**
     * {@link XMLGregorianCalendar} to {@link LocalDate} converter
     *
     * @param xmlGregorianCalendar
     *            {@code XMLGregorianCalendar} to convert
     * @return {@code LocalDate} instance or null if {@code xmlGregorianCalendar} is null
     */
    public static LocalDate toLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return LocalDate.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay());
    }

    /**
     * Sets the input {@link Calendar}'s time zone to UTC, then applies {@link #toXMLGregorianCalendar(Calendar)}. Transforming time to UTC can result
     * time differences.
     *
     * @param cal
     *            {@code Calendar} to transform
     * @return null if null input or "2018-03-07T14:15:01.000Z" formatted XMLGregorianCalendar
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarInUTC(Calendar cal) {
        if (cal == null) {
            return null;
        }

        Calendar calendar = (Calendar) cal.clone();
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.of("UTC")));
        return toXMLGregorianCalendar(calendar);
    }

    /**
     * Same as {@link #toXMLGregorianCalendarInUTC(Calendar)} for java.util.Date input.
     *
     * @param date
     *            {@code Date} to transform
     * @return null if null input or "2018-03-07T14:15:01.000Z" formatted XMLGregorianCalendar
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarInUTC(Date date) {
        return toXMLGregorianCalendarInUTC(DateUtil.toCalendar(date));
    }

    /**
     * Clears time part of given {@link XMLGregorianCalendar}.
     *
     * @param xmlCal
     *            {@code XMLGregorianCalendar} to clear
     * @return {@code XMLGregorianCalendar} without time fields or null if {@code xmlCal} is null
     */
    public static XMLGregorianCalendar clearTime(XMLGregorianCalendar xmlCal) {
        if (xmlCal == null) {
            return null;
        }
        XMLGregorianCalendar dateOnly = (XMLGregorianCalendar) xmlCal.clone();
        dateOnly.setFractionalSecond(null);
        dateOnly.setHour(DatatypeConstants.FIELD_UNDEFINED);
        dateOnly.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
        dateOnly.setMinute(DatatypeConstants.FIELD_UNDEFINED);
        dateOnly.setSecond(DatatypeConstants.FIELD_UNDEFINED);
        dateOnly.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        return dateOnly;
    }

    /**
     * Null safe clone of {@link XMLGregorianCalendar}.
     *
     * @param xmlGregorianCalendar
     *            {@code XMLGregorianCalendar} to clone
     * @return {@code XMLGregorianCalendar} instance clone or null if {@code xmlGregorianCalendar} is null
     */
    public static XMLGregorianCalendar cloneXMLGregorianCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return (XMLGregorianCalendar) xmlGregorianCalendar.clone();
    }
}
