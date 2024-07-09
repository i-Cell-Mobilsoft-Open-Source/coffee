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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

/**
 * @author balazs.joo
 */
@DisplayName("Testing DateXmlUtil")
class DateXmlUtilTest {

    private static Calendar gregorianCalendar;
    private static Date date;
    private static OffsetDateTime offsetDateTime;
    private static ZonedDateTime zonedDateTime;
    private static LocalDate localDate;
    private static XMLGregorianCalendar xmlGregorianCalendar;
    private static XMLGregorianCalendar xmlGregorianCalendarZ;
    private static XMLGregorianCalendar xmlGregorianCalendarUtc;
    private static XMLGregorianCalendar xmlGregorianCalendarTimeless;
    private static XMLGregorianCalendar xmlGregorianCalendarTimelessZoneless;

    @BeforeAll
    static void setUpBeforeClass() {
        date = new Date(1549898614051L);
        offsetDateTime = date.toInstant().atOffset(ZoneOffset.UTC);
        zonedDateTime = ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDate = offsetDateTime.toLocalDate();

        gregorianCalendar = GregorianCalendar.getInstance();
        gregorianCalendar.setTime(date);
        try {
            xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gregorianCalendar);

        } catch (DatatypeConfigurationException e) {
            xmlGregorianCalendar = null;
        }
        try {
            xmlGregorianCalendarZ = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gregorianCalendar);
            xmlGregorianCalendarZ.setTimezone(0);

        } catch (DatatypeConfigurationException e) {
            xmlGregorianCalendarZ = null;
        }
        try {
            xmlGregorianCalendarUtc = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gregorianCalendar);
            xmlGregorianCalendarUtc = xmlGregorianCalendarUtc.normalize();

        } catch (DatatypeConfigurationException e) {
            xmlGregorianCalendarUtc = null;
        }
        try {
            xmlGregorianCalendarTimeless = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(offsetDateTime.getYear(),
                    offsetDateTime.getMonthValue(), offsetDateTime.getDayOfMonth(), 0);

        } catch (DatatypeConfigurationException e) {
            xmlGregorianCalendarTimeless = null;
        }
        try {
            xmlGregorianCalendarTimelessZoneless = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(offsetDateTime.getYear(),
                    offsetDateTime.getMonthValue(), offsetDateTime.getDayOfMonth(), DatatypeConstants.FIELD_UNDEFINED);

        } catch (DatatypeConfigurationException e) {
            xmlGregorianCalendarTimelessZoneless = null;
        }
    }

    @Nested
    @DisplayName("Testing toXMLGregorianCalendar()")
    class ToXMLGregorianCalendar {

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from GregorianCalendar")
        void fromCalendar() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(gregorianCalendar);

            // then
            assertEquals(xmlGregorianCalendar, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from non GregorianCalendar, JRE <= 8")
        @EnabledForJreRange(max = JRE.JAVA_8)
        void fromJapaneseCalendarJ8() {
            // given
            Calendar source = Calendar.getInstance(TimeZone.getTimeZone("UTC"), new Locale("ja", "JP", "JP"));
            source.set(Calendar.MILLISECOND, 51);
            // NOTE: Up until Java version 8u211, you need to use the old Japanese era format for this.
            // https://www.oracle.com/technetwork/java/javase/8u211-relnotes-5290139.html
            source.set(31, 1, 11, 15, 23, 34);

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(source);

            // then
            assertEquals(xmlGregorianCalendarUtc, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from non GregorianCalendar, JRE >= 11")
        @EnabledForJreRange(min = JRE.JAVA_11)
        void fromJapaneseCalendarJ11() {
            // given
            Calendar source = Calendar.getInstance(TimeZone.getTimeZone("UTC"), new Locale("ja", "JP", "JP"));
            source.set(Calendar.MILLISECOND, 51);
            // NOTE: Starting from Java version 8u211, a new Japanese era format must be used for this.
            // https://www.oracle.com/technetwork/java/javase/8u211-relnotes-5290139.html
            source.set(1, 1, 11, 15, 23, 34);

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(source);

            // then
            assertEquals(xmlGregorianCalendarUtc, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from GregorianCalendar with offset")
        void fromCalendarOffset() {
            // given
            GregorianCalendar inputSource = (GregorianCalendar) gregorianCalendar;
            int offsetInputInMinutes = 35;
            XMLGregorianCalendar expected = (XMLGregorianCalendar) xmlGregorianCalendar.clone();
            expected.setTimezone(offsetInputInMinutes);

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(inputSource, offsetInputInMinutes);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from Date")
        void fromDate() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(date);

            // then
            assertEquals(xmlGregorianCalendar, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendarNoTimeZone() from Date")
        void noTimeZoneFromDate() {
            // given
            XMLGregorianCalendar expected = (XMLGregorianCalendar) xmlGregorianCalendar.clone();
            expected.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarNoTimeZone(date);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from OffsetDateTime")
        void fromOffsetDateTime() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(offsetDateTime);

            // then
            assertEquals(xmlGregorianCalendar, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendar() from LocalDate")
        void fromLocalDate() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendar(localDate);

            // then
            assertEquals(xmlGregorianCalendarTimeless, actual);
        }
    }

    @Nested
    @DisplayName("Testing toCalendar()")
    class ToCalendarTest {
        @Test
        @DisplayName("Testing toCalendar() from XMLGregorianCalendar")
        void fromXmlGregorianCalendar() {
            // given

            // when
            Calendar actual = DateXmlUtil.toCalendar(xmlGregorianCalendar);

            // then
            assertNotNull(actual);
            assertEquals(actual.compareTo(gregorianCalendar), 0);
        }

    }

    @Nested
    @DisplayName("Testing toXMLGregorianCalendarAsUTC()")
    class ToXMLGregorianCalendarAsUTC {
        @Test
        @DisplayName("Testing toXMLGregorianCalendarAsUTC() from Calendar")
        void fromCalendar() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarAsUTC(gregorianCalendar);

            // then
            assertNotNull(actual);
            assertEquals(xmlGregorianCalendarZ, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendarAsUTC() from Date")
        void fromDate() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarAsUTC(date);

            // then
            assertNotNull(actual);
            assertEquals(xmlGregorianCalendarZ, actual);
        }
    }

    @Nested
    @DisplayName("Testing toXMLGregorianCalendarDateOnly()")
    class ToXMLGregorianCalendarDateOnly {
        @Test
        @DisplayName("Testing toXMLGregorianCalendarDateOnly() from Calendar")
        void fromCalendar() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarDateOnly(gregorianCalendar);

            // then
            assertNotNull(actual);
            assertEquals(xmlGregorianCalendarTimeless, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendarDateOnly() from Date")
        void fromDate() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarDateOnly(date);

            // then
            assertNotNull(actual);
            assertEquals(xmlGregorianCalendarTimeless, actual);
        }
    }

    @Nested
    @DisplayName("Testing toXMLGregorianCalendarInUTC()")
    class ToXMLGregorianCalendarInUTC {
        @Test
        @DisplayName("Testing toXMLGregorianCalendarInUTC() from Calendar")
        void fromCalendar() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarInUTC(gregorianCalendar);

            // then
            assertNotNull(actual);
            assertEquals(xmlGregorianCalendarUtc, actual);
        }

        @Test
        @DisplayName("Testing toXMLGregorianCalendarInUTC() from Date")
        void fromDate() {
            // given

            // when
            XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarInUTC(date);

            // then
            assertNotNull(actual);
            assertEquals(xmlGregorianCalendarUtc, actual);
        }
    }

    @Test
    @DisplayName("Testing toDate()")
    void toDate() {
        // given

        // when
        Date actual = DateXmlUtil.toDate(xmlGregorianCalendar);

        // then
        assertEquals(date, actual);
    }

    @Test
    @DisplayName("Testing toXMLGregorianCalendarFromISO()")
    void toXMLGregorianCalendarFromISO() {
        // given
        String stringISODate = "2019-02-11T15:23:34.051Z";

        // when
        XMLGregorianCalendar actual = DateXmlUtil.toXMLGregorianCalendarFromISO(stringISODate);

        // then
        assertEquals(xmlGregorianCalendar, actual);
    }

    @Test
    @DisplayName("Testing toDateAsLocal()")
    void toDateAsLocal() {
        // given

        // when
        Date actual = DateXmlUtil.toDateAsLocal(xmlGregorianCalendar);

        // then
        assertEquals(date, actual);
    }

    @Test
    @DisplayName("Testing toDateOnly()")
    void toDateOnly() {
        // given

        // when
        Date actual = DateXmlUtil.toDateOnly(xmlGregorianCalendar);

        // then
        assertEquals(DateUtil.clearTimePart(date), actual);
    }

    @Test
    @DisplayName("Testing toZonedDateTime()")
    void toZonedDateTime() {
        // given

        // when
        ZonedDateTime actual = DateXmlUtil.toZonedDateTime(xmlGregorianCalendar);

        // then
        assertEquals(zonedDateTime.toOffsetDateTime(), actual.toOffsetDateTime());
    }

    @Nested
    @DisplayName("Testing toLocalDate()")
    class ToLocalDate {
        @Test
        @DisplayName("from xmlGregorianCalendar with time")
        void fromXmlGregorianCalendar() {
            // given

            // when
            LocalDate actual = DateXmlUtil.toLocalDate(xmlGregorianCalendar);

            // then
            assertEquals(localDate, actual);
        }

        @Test
        @DisplayName("from xmlGregorianCalendar without time")
        void fromXmlWithoutTime() {
            // given

            // when
            LocalDate actual = DateXmlUtil.toLocalDate(xmlGregorianCalendarTimeless);

            // then
            assertEquals(localDate, actual);
        }
    }

    @Test
    @DisplayName("Testing clearTime()")
    void clearTime() {
        // given

        // when
        XMLGregorianCalendar actual = DateXmlUtil.clearTime(xmlGregorianCalendar);

        // then
        assertEquals(xmlGregorianCalendarTimelessZoneless, actual);
    }

    @Test
    @DisplayName("Testing cloneXMLGregorianCalendar()")
    void cloneXMLGregorianCalendar() {
        // given

        // when
        XMLGregorianCalendar actual = DateXmlUtil.cloneXMLGregorianCalendar(xmlGregorianCalendar);

        // then
        assertEquals(xmlGregorianCalendar, actual);
        assertNotSame(xmlGregorianCalendar, actual);
    }

    @Test
    @DisplayName("Testing null conversions")
    void testNullConversions() {
        // given
        Date nullDate = null;
        Calendar nullCalendar = null;
        GregorianCalendar nullGregorianCalendar = null;
        XMLGregorianCalendar nullXmlGregorianCalendar = null;
        OffsetDateTime nullOffsetDateTime = null;
        // when, then
        assertNull(DateXmlUtil.toXMLGregorianCalendar(nullDate));
        assertNull(DateXmlUtil.toXMLGregorianCalendar(nullCalendar));
        assertNull(DateXmlUtil.toXMLGregorianCalendar(nullGregorianCalendar, 0));
        assertNull(DateXmlUtil.toXMLGregorianCalendarNoTimeZone(null));
        assertNull(DateXmlUtil.toCalendar(nullXmlGregorianCalendar));
        assertNull(DateXmlUtil.toDate(nullXmlGregorianCalendar));
        assertNull(DateXmlUtil.toXMLGregorianCalendarFromISO(null));
        assertNull(DateXmlUtil.toXMLGregorianCalendar(nullOffsetDateTime));
        assertNull(DateXmlUtil.toDateAsLocal(null));
        assertNull(DateXmlUtil.toDateOnly(null));
        assertNull(DateXmlUtil.toXMLGregorianCalendarAsUTC(nullCalendar));
        assertNull(DateXmlUtil.toXMLGregorianCalendarAsUTC(nullDate));
        assertNull(DateXmlUtil.toXMLGregorianCalendarDateOnly(nullCalendar));
        assertNull(DateXmlUtil.toXMLGregorianCalendarDateOnly(nullDate));
        assertNull(DateXmlUtil.toZonedDateTime(null));
        assertNull(DateXmlUtil.toXMLGregorianCalendarInUTC(nullCalendar));
        assertNull(DateXmlUtil.toXMLGregorianCalendarInUTC(nullDate));
        assertNull(DateXmlUtil.clearTime(null));
    }

}
