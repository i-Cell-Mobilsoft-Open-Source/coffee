/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.dto.url;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import hu.icellmobilsoft.coffee.dto.adapter.UtcOffsetDateTimeXmlAdapter;

/**
 * Test of UtcOffsetDateTimeXmlAdapter class
 *
 * @author speter555
 * @since 1.10.0
 */
@DisplayName("Testing UtcOffsetDateTimeXmlAdapter")
class UtcOffsetDateTimeXmlAdapterTest {

    @DisplayName("UtcOffsetDateTimeXmlAdapterTest.marshal test - OK")
    @ParameterizedTest(name = "Testing mashal method with [{2}]")
    @MethodSource("mashalTestData")
    void marshal(OffsetDateTime offsetDateTime, String offsetDataTimeText, String testName) {

        UtcOffsetDateTimeXmlAdapter adapter = new UtcOffsetDateTimeXmlAdapter();
        String testText = adapter.marshal(offsetDateTime);
        Assertions.assertNotNull(testText);
        Assertions.assertEquals(offsetDataTimeText, testText);
    }

    private static List<Arguments> mashalTestData() {
        List<Arguments> argumentsList = new ArrayList<>();
        fillArgumentListWithZeroMinAndSecTestData(argumentsList);
        fillArgumentListWithNanoSecTestData(argumentsList);
        fillArgumentListWithGeneralTestData(argumentsList);
        return argumentsList;
    }

    private static void fillArgumentListWithZeroMinAndSecTestData(List<Arguments> argumentsList) {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2022, 5, 16, 12, 0, 0, 0, ZoneOffset.UTC);
        String offsetDataTimeText = "2022-05-16T12:00:00Z";
        argumentsList.add(Arguments.of(offsetDateTime, offsetDataTimeText, "marshalWithZeroMinAndSec"));
    }

    private static void fillArgumentListWithNanoSecTestData(List<Arguments> argumentsList) {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2022, 5, 16, 12, 10, 11, 123456, ZoneOffset.UTC);
        String offsetDataTimeText = "2022-05-16T12:10:11.000123456Z";
        argumentsList.add(Arguments.of(offsetDateTime, offsetDataTimeText, "marshalWithNanoSec"));
    }

    private static void fillArgumentListWithGeneralTestData(List<Arguments> argumentsList) {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2022, 5, 16, 12, 10, 11, 0, ZoneOffset.UTC);
        String offsetDataTimeText = "2022-05-16T12:10:11Z";
        argumentsList.add(Arguments.of(offsetDateTime, offsetDataTimeText, "marshalWithGeneral"));
    }
}
