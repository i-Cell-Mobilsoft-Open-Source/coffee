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
package hu.icellmobilsoft.coffee.tool.utils.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.JsonConversionException;

/**
 * @author mark.petrenyi
 * @author bucherarnold
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing JsonUtil")
class JsonUtilTest {
    private static final String TEST_STRING = "testString";
    private static final String TEST_STRING_BASE64 = "dGVzdFN0cmluZw==";
    private static final String DATE_AS_LONG = "1549898614051";
    private static final String INVALID_JSON = "{\"sd:wqe}";
    private static final String TEST_OBJECT_AS_JSON = "{" + //
            "\"date\":" + DATE_AS_LONG + "," + //
            "\"xmlGregorianCalendar\":\"2019-02-11T15:23:34.051Z\"," + //
            "\"bytes\":\"" + TEST_STRING_BASE64 + "\"," + //
            "\"string\":\"test1\"," + //
            "\"clazz\":\"hu.icellmobilsoft.coffee.tool.utils.json.JsonUtilTest\"," + //
            "\"offsetDateTime\":\"2019-02-11T15:23:34.051Z\"," + //
            "\"offsetTime\":\"15:23:34.051Z\"," + //
            "\"localDate\":\"2019-02-11\"," + //
            "\"duration\":\"P1Y1M1DT1H1M1S\"," + //
            "\"yearMonth\":\"2010-05\"" + //
            "}";
    private static final String INVALID_JSON_OBJECT_YEAR_MONTH = "{" + //
            "\"yearMonth\": {}" + //
            "}";
    private static final String INVALID_JSON_ARRAY_YEAR_MONTH = "{" + //
            "\"yearMonth\": []" + //
            "}";

    private TestObject givenWeHaveTestObject() throws DatatypeConfigurationException {
        TestObject testObject = new TestObject();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2019, 02, 11, 15, 23, 34, 51000000, ZoneOffset.UTC);
        Calendar gregorianCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        Duration duration = DatatypeFactory.newInstance().newDuration(true, 1, 1, 1, 1, 1, 1);
        gregorianCalendar.setTime(Date.from(zonedDateTime.toInstant()));
        YearMonth yearMonth = YearMonth.of(2010, 5);

        Date date = new Date(Long.parseLong(DATE_AS_LONG));
        testObject.setDate(date);
        testObject.setXmlGregorianCalendar(DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gregorianCalendar));
        testObject.setBytes(TEST_STRING.getBytes());
        testObject.setString("test1");
        testObject.setClazz(JsonUtilTest.class);
        testObject.setOffsetDateTime(zonedDateTime.toOffsetDateTime());
        testObject.setOffsetTime(zonedDateTime.toOffsetDateTime().toOffsetTime());
        testObject.setLocalDate(zonedDateTime.toLocalDate());
        testObject.setDuration(duration);
        testObject.setYearMonth(yearMonth);
        return testObject;
    }

    @Nested
    @DisplayName("Testing object creation")
    class ObjectTest {

        @Nested
        @DisplayName("Testing toObject()")
        class ToObjectTest {

            @Test
            @DisplayName("Testing toObject() with invalid input JSON")
            void invalidJSON() {
                // given

                // when
                TestObject actual = JsonUtil.toObjectOpt(INVALID_JSON, TestObject.class).orElse(null);

                // then
                assertNull(actual);
            }

            @Test
            @DisplayName("Testing toObject() with valid input JSON")
            void validJSON() throws Exception {
                // given
                TestObject expected = givenWeHaveTestObject();

                // when
                TestObject actual = JsonUtil.toObject(TEST_OBJECT_AS_JSON, TestObject.class);

                // then
                assertEquals(expected, actual);
            }

            @Test
            @DisplayName("Testing toObject() with invalid JSON object YearMonth")
            void invalidJsonObjectYearMonth() {
                // given

                // when
                TestObject actual = JsonUtil.toObjectOpt(INVALID_JSON_OBJECT_YEAR_MONTH, TestObject.class).orElse(null);

                // then
                assertNull(actual);
            }

            @Test
            @DisplayName("Testing toObject() with invalid JSON array YearMonth")
            void invalidJsonArrayYearMonth() {
                // given

                // when
                TestObject actual = JsonUtil.toObjectOpt(INVALID_JSON_ARRAY_YEAR_MONTH, TestObject.class).orElse(null);

                // then
                assertNull(actual);
            }

            @Test
            @DisplayName("Testing with unknown properties should throw exception")
            void validJsonWithUnknowProperties() {
                // given
                String tmpTestObject = TEST_OBJECT_AS_JSON.replaceFirst("test1", "test1\",\"unknownPropertyAAA\":\"AAA");

                // when
                Executable operation = () -> JsonUtil.toObject(tmpTestObject, TestObject.class);

                // then
                assertThrows(JsonConversionException.class, operation);
            }

            @Test
            @DisplayName("Testing with unknown properties optional")
            void validOptionalJsonWithUnknowProperties() {
                // given
                String tmpTestObject = TEST_OBJECT_AS_JSON.replaceFirst("test1", "test1\",\"unknownPropertyAAA\":\"AAA");

                // when
                TestObject actual = JsonUtil.toObjectOpt(tmpTestObject, TestObject.class).orElse(null);

                // then
                assertNull(actual);
            }
        }

        @Nested
        @DisplayName("Testing toObjectEx()")
        class ToObjectExTest {

            @Test
            @DisplayName("Test toObjectEx() with valid input JSON")
            void validJSON() throws BaseException, DatatypeConfigurationException {
                // given
                TestObject expected = givenWeHaveTestObject();

                // when
                TestObject actual = JsonUtil.toObjectEx(TEST_OBJECT_AS_JSON, TestObject.class);

                // then
                assertEquals(expected, actual);
            }

            @Test
            @DisplayName("Test toObjectEx() with invalid input JSON")
            void invalidJSON() {
                // given

                // when
                Executable operation = () -> JsonUtil.toObjectEx(INVALID_JSON, TestObject.class);

                // then
                assertThrows(BaseException.class, operation);
            }

            @Test
            @DisplayName("Testing toObjectEx() with invalid JSON object YearMonth")
            void invalidJsonObjectYearMonth() {
                // given

                // when
                Executable operation = () -> JsonUtil.toObjectEx(INVALID_JSON_OBJECT_YEAR_MONTH, TestObject.class);

                // then
                assertThrows(BaseException.class, operation);
            }

            @Test
            @DisplayName("Testing toObjectEx() with invalid JSON array YearMonth")
            void invalidJsonArrayYearMonth() {
                // given

                // when
                Executable operation = () -> JsonUtil.toObjectEx(INVALID_JSON_ARRAY_YEAR_MONTH, TestObject.class);

                // then
                assertThrows(BaseException.class, operation);
            }
        }

        @Nested
        @DisplayName("Testing toObjectGson()")
        class ToObjectGsonTest {

            @Test
            @DisplayName("Test toObjectGson with valid input JSON")
            void validJSON() throws DatatypeConfigurationException {
                // given
                TestObject expected = givenWeHaveTestObject();

                // when
                TestObject actual = JsonUtil.toObjectGson(TEST_OBJECT_AS_JSON, TestObject.class);

                // then
                assertEquals(expected, actual);

            }

            @Test
            @DisplayName("Test toObjectGson with invalid input JSON")
            void invalidJSON() {
                // given

                // when
                Executable operation = () -> JsonUtil.toObjectGson(INVALID_JSON, TestObject.class);

                // then
                assertThrows(Exception.class, operation);

            }

            @Test
            @DisplayName("Testing toObjectGson with invalid JSON object YearMonth")
            void invalidJsonObjectYearMonth() {
                // given

                // when
                Executable operation = () -> JsonUtil.toObjectGson(INVALID_JSON_OBJECT_YEAR_MONTH, TestObject.class);

                // then
                assertThrows(Exception.class, operation);
            }

            @Test
            @DisplayName("Testing toObjectGson with invalid JSON array YearMonth")
            void invalidJsonArrayYearMonth() {
                // given

                // when
                Executable operation = () -> JsonUtil.toObjectGson(INVALID_JSON_ARRAY_YEAR_MONTH, TestObject.class);

                // then
                assertThrows(Exception.class, operation);
            }
        }

    }

    @Nested
    @DisplayName("Testing json creation")
    class JSONTest {

        @Nested
        @DisplayName("Testing toJson()")
        class ToJsonTest {

            @Test
            @DisplayName("Testing toJson() with parsable Object")
            void parsable() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                // parse the json strings to avoid whitespace and ordering related differences
                JsonElement expectedJSONObject = JsonParser.parseString(TEST_OBJECT_AS_JSON);

                // when
                String actual = JsonUtil.toJson(source);

                // then
                JsonElement actualJSONObject = JsonParser.parseString(actual);
                assertEquals(expectedJSONObject, actualJSONObject);
            }

            @Test
            @DisplayName("Testing toJson() with unparsable Object should not throw exception")
            void unparsable() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                XMLGregorianCalendar unparseableXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                source.setXmlGregorianCalendar(unparseableXMLGregorianCalendar);

                // when
                String actual = JsonUtil.toJsonOpt(source).orElse(null);

                // then
                assertNull(actual);
            }

            @Test
            @DisplayName("Testing toJson() with unparsable Object should throw exception")
            void unparsableWithException() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                XMLGregorianCalendar unparseableXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                source.setXmlGregorianCalendar(unparseableXMLGregorianCalendar);

                // when
                Executable operation = () -> JsonUtil.toJson(source);

                // then
                assertThrows(JsonConversionException.class, operation);
            }

        }

        @Nested
        @DisplayName("Testing toJsonEx()")
        class ToJsonExTest {

            @Test
            @DisplayName("Testing toJsonGson() with parsable Object")
            void parsable() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                // parse the json strings to avoid whitespace and ordering related differences
                JsonElement expectedJSONObject = JsonParser.parseString(TEST_OBJECT_AS_JSON);

                // when
                String actual = JsonUtil.toJsonEx(source);

                // then
                JsonElement actualJSONObject = JsonParser.parseString(actual);
                assertEquals(expectedJSONObject, actualJSONObject);
            }

            @Test
            @DisplayName("Testing toJsonGson() with unparsable Object")
            void unparsable() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                XMLGregorianCalendar unparseableXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                source.setXmlGregorianCalendar(unparseableXMLGregorianCalendar);

                // when
                Executable operation = () -> JsonUtil.toJsonEx(source);

                // then
                assertThrows(BaseException.class, operation);
            }

        }

        @Nested
        @DisplayName("Testing toJsonGson()")
        class ToJsonGsonTest {

            @Test
            @DisplayName("Testing toJsonGson() with parsable Object")
            void parsable() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                // parse the json strings to avoid whitespace and ordering related differences
                JsonElement expectedJSONObject = JsonParser.parseString(TEST_OBJECT_AS_JSON);

                // when
                String actual = JsonUtil.toJsonGson(source);

                // then
                JsonElement actualJSONObject = JsonParser.parseString(actual);
                assertEquals(expectedJSONObject, actualJSONObject);
            }

            @Test
            @DisplayName("Testing toJsonGson() with unparsable Object")
            void unparsable() throws Exception {
                // given
                TestObject source = givenWeHaveTestObject();
                XMLGregorianCalendar unparseableXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                source.setXmlGregorianCalendar(unparseableXMLGregorianCalendar);

                // when
                Executable operation = () -> JsonUtil.toJsonGson(source);

                // then
                assertThrows(Exception.class, operation);
            }
        }
    }

}
