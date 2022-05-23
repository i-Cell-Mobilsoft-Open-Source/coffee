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
