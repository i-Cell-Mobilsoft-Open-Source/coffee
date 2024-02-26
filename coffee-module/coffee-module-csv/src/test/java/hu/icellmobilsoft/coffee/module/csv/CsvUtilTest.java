/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.csv;

import static com.opencsv.ICSVParser.DEFAULT_SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.opencsv.CSVParserBuilder;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.csv.configuration.CsvWriterConfig;

/**
 * Class for testing {@link CsvUtil}
 *
 * @author martin.nagy
 * @since 1.8.0
 */
class CsvUtilTest {

    private static final String FNP_TEST_CSV = "/%1$s/test.csv";
    private static final String FNP_TEST_WITH_COMMA_CSV = "/%1$s/test_with_comma.csv";
    private static final String FNP_TEST_WITH_COMMA_QUOTE_CSV = "/%1$s/test_with_comma_quote.csv";

    private static List<TestBean> TEST_BEANS;
    private static String TEST_CSV;
    private static String TEST_WITH_COMMA_CSV;
    private static String TEST_WITH_COMMA_QUOTE_CSV;

    @BeforeAll
    static void beforeAll() throws IOException {
        TEST_BEANS = List.of( //
                new TestBean(11, "foo", true, LocalDate.of(2021, 11, 23), TestBean.Status.IN_PROGRESS), //
                new TestBean(12, "bar", false, LocalDate.of(2020, 1, 2), TestBean.Status.DONE) //
        );

        String os = System.lineSeparator().equals("\r\n") ? "win" : "unix";
        String testCsv = String.format(FNP_TEST_CSV, os);
        String testWithCommaCsv = String.format(FNP_TEST_WITH_COMMA_CSV, os);
        String testWithCommaQuoteCsv = String.format(FNP_TEST_WITH_COMMA_QUOTE_CSV, os);

        TEST_CSV = new String(CsvUtilTest.class.getResourceAsStream(testCsv).readAllBytes(), StandardCharsets.UTF_8);
        TEST_WITH_COMMA_CSV = new String(CsvUtilTest.class.getResourceAsStream(testWithCommaCsv).readAllBytes(), StandardCharsets.UTF_8);
        TEST_WITH_COMMA_QUOTE_CSV = new String(
                CsvUtilTest.class.getResourceAsStream(testWithCommaQuoteCsv).readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void shouldConvertToCsv() throws BaseException {
        // GIVEN
        List<TestBean> beans = TEST_BEANS;
        // WHEN
        String csv = CsvUtil.toCsv(beans, TestBean.class);
        // THEN
        assertEquals(TEST_CSV, csv);
    }

    @Test
    void shouldConvertToCsvWithCommaAndQuote() throws BaseException {
        // GIVEN
        List<TestBean> beans = TEST_BEANS;
        CsvWriterConfig csvWriterConfig = new CsvWriterConfig.Builder()
                .withQuotechar('\'')
                .withSeparator(',')
                .build();
        // WHEN
        String csv = CsvUtil.toCsv(beans, TestBean.class, csvWriterConfig);
        // THEN
        assertEquals(TEST_WITH_COMMA_QUOTE_CSV, csv);
    }

    @Test
    void shouldConvertToCsvWithDefaultConfig() throws BaseException {
        // GIVEN
        List<TestBean> beans = TEST_BEANS;
        CsvWriterConfig csvWriterConfig = new CsvWriterConfig.Builder().build();
        // WHEN
        String csv = CsvUtil.toCsv(beans, TestBean.class, csvWriterConfig);
        // THEN
        assertEquals(TEST_CSV, csv);
    }

    @Test
    void shouldParseCsvString() throws BaseException {
        // GIVEN
        String csv = TEST_CSV;
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(csv, TestBean.class);
        // THEN
        assertEquals(TEST_BEANS, beans);
    }

    @Test
    void shouldParseCsvWithCommaQuote() throws BaseException {
        // GIVEN
        String csv = TEST_WITH_COMMA_QUOTE_CSV;
        CSVParserBuilder csvParserBuilder = new CSVParserBuilder()
                .withSeparator(',')
                .withQuoteChar('\'');
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(csv, TestBean.class, csvParserBuilder);
        // THEN
        assertEquals(TEST_BEANS, beans);
    }

    @Test
    void shouldParseCsvStream() throws BaseException {
        // GIVEN
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_CSV.getBytes(StandardCharsets.UTF_8));
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(inputStream, TestBean.class);
        // THEN
        assertEquals(TEST_BEANS, beans);
    }

    @Test
    void shouldParseCsvStreamComma() throws BaseException {
        // GIVEN
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_WITH_COMMA_CSV.getBytes(StandardCharsets.UTF_8));
        CSVParserBuilder csvParserBuilder = new CSVParserBuilder().withSeparator(',');
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(inputStream, TestBean.class, csvParserBuilder);
        // THEN
        assertEquals(TEST_BEANS, beans);
    }

    @Test
    void shouldParseCsvStreamCommaQuote() throws BaseException {
        // GIVEN
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_WITH_COMMA_QUOTE_CSV.getBytes(StandardCharsets.UTF_8));
        CSVParserBuilder csvParserBuilder = new CSVParserBuilder().withSeparator(DEFAULT_SEPARATOR).withQuoteChar('\'');
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(inputStream, TestBean.class, csvParserBuilder);
        // THEN
        assertEquals(TEST_BEANS, beans);
    }

}