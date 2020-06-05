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
import org.jboss.logging.Logger;

/**
 * Date util.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class DateXmlUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(DateXmlUtil.class);

    private static final String TIMESTAMP_CONVERT_ERROR_MSG = "Timestamp convert [{0}] to XmlGregorianCalendar error: ";

    /**
     * Convert Calendar to XMLGregorianCalendar
     *
     * @param c
     *            calendar
     * @return null if error
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
     * Creates a new XMLGregorianCalandar instance from a util Date.
     *
     * @param date
     * @return null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }
        return toXMLGregorianCalendar(DateUtil.toCalendar(date));
    }

    /**
     * Creates a new XMLGregorianCalendar instance from a GregorianCalendar with specific timezone offset.
     *
     * @param calendar
     * @param offsetInMinutes
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
     * Creates a new XMLGregorianCalandar instance from a util Date, sets the timezone to undefinied.
     *
     * @param date
     * @return null if error
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
     * Creates a calendar instance from a XMLGregorianCalendar.
     *
     * @param xmlGregorianCalendar
     */
    public static Calendar toCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar();
    }

    /**
     * Creates a Date instance from a XMLGregorianCalendar.
     *
     * @param xmlGregorianCalendar
     */
    public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }

    /**
     * Creates a new XMLGregorianCalandar instance from a String ISO Date (such as 2011-12-03T10:15:30Z). Date is converted to current timezone
     *
     * @param stringISODate
     *            yyyy-MM-dd'T'HH:mm:ss.SSSZ
     * @return null if error
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
     * Convert OffsetDateTime to XMLGregorianCalendar
     *
     * @param offsetDateTime
     * @return null for null
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return toXMLGregorianCalendar(GregorianCalendar.from(offsetDateTime.toZonedDateTime()));
    }

    /**
     * LocalDate to XMLGregorianCalendar converter
     *
     * @param localDate
     * @return "2020-03-03Z"
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
     * XMLGregorianCalendar ban levo datumot ugy veszi mindha local timezone lenne, tehat az UTC-t felulirja local timetozone-al. Hasznos bizonyos
     * esetekben, amikor az oracle-ben nincs tarolva timestamp, de viszont input adatkent UTC-bol indulunk ki es a DB-ben is helyesen kell eltarolni
     *
     * @param xmlGregorianCalendar
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
     * XMLGregorianCalendar-bol kiszedi a datumot (ido nullazva). Hasznalhato foleg birthDate mezoknel es ott ahol xsd:date tipus van hasznalva.<br>
     * <br>
     * Azt a problemat orvosolja hogy long-kent beadott datum (UTC zero ido) json formatumban a XMLGregorianCalendar helyi idore forditja, es igy
     * mindig belekerul ido eltolodas.<br>
     * <ul>
     * <li>json datum: 1515452400000 (Tue Jan 09 00:00:00 CET 2018)</li>
     * <li>ebbol XMLGregorianCalendar: 2018-01-09T01:00:00+01:00</li>
     * <li>(hibas) toDate(XMLGregorianCalendar): 2018-01-09 01:00:00</li>
     * <li>(jo) toDateOnly(XMLGregorianCalendar): 2018-01-09 00:00:00</li>
     * </ul>
     *
     * @param xmlGregorianCalendar
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
     * {@link #toDateAsLocal(XMLGregorianCalendar)} ennek a vissza fele parja
     *
     * @param c
     *            calendar
     * @return null if error
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
     * {@link #toDateAsLocal(XMLGregorianCalendar)} ennek a vissza fele parja
     *
     * @param date
     * @return null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarAsUTC(Date date) {
        if (date == null) {
            return null;
        }
        return toXMLGregorianCalendarAsUTC(DateUtil.toCalendar(date));
    }

    /**
     * Calendar ertekbol csinal egy tiszta datum (YYYY-MM-DDZ ido nelkuli UTCben ertelmezett) XMLGregorianCalendar-t
     *
     * @param c
     *            Calendar
     * @return null if error
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
     * java.util.Date ertekbol csinal egy tiszta datum (YYYY-MM-DDZ ido nelkuli UTCben ertelmezett) XMLGregorianCalendar-t
     *
     * @param date
     * @return null if error
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarDateOnly(Date date) {
        if (date == null) {
            return null;
        }
        return toXMLGregorianCalendarDateOnly(DateUtil.toCalendar(date));
    }

    /**
     * XMLGregorianCalendar -> ZonedDateTime converter XMLGregorianCalendar belso idozona szerint (classic)
     *
     * @param xmlCal
     */
    public static ZonedDateTime toZonedDateTime(XMLGregorianCalendar xmlCal) {
        if (xmlCal == null) {
            return null;
        }
        return xmlCal.toGregorianCalendar().toZonedDateTime();
    }

    /**
     * XMLGregorianCalendar to LocalDate converter
     *
     * @param xmlGregorianCalendar
     */
    public static LocalDate toLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return LocalDate.of(xmlGregorianCalendar.getYear(), xmlGregorianCalendar.getMonth(), xmlGregorianCalendar.getDay());
    }

    /**
     * A bejovo Calendar ido zonajat "UTC"-re alitja, igy a benne levo datum is transformalodik (+/- ido) es a sima
     * {@link #toXMLGregorianCalendar(Calendar)} '2018-03-07T14:15:01.000Z' formatumra rakja az XMLGregorianCalendar objektumot
     *
     * @param cal
     *            calendar
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
     * Ugyan az mint a {@link #toXMLGregorianCalendarInUTC(Calendar)} csak java.util.Date inputra
     *
     * @param date
     */
    public static XMLGregorianCalendar toXMLGregorianCalendarInUTC(Date date) {
        return toXMLGregorianCalendarInUTC(DateUtil.toCalendar(date));
    }

    /**
     * Ido resz torlese
     *
     * @param xmlCal
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
     * Null safe clone of XMLGregorianCalendar
     *
     * @param xmlGregorianCalendar
     */
    public static XMLGregorianCalendar cloneXMLGregorianCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return (XMLGregorianCalendar) xmlGregorianCalendar.clone();
    }
}
