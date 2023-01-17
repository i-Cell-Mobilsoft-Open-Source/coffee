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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Class for testing {@link CsvUtil}
 * 
 * @author martin.nagy
 * @since 1.8.0
 */
class CsvUtilTest {

    private static List<TestBean> TEST_BEANS;
    private static String TEST_CSV;

    @BeforeAll
    static void beforeAll() throws IOException {
        TEST_BEANS = List.of( //
                new TestBean(11, "foo", true, LocalDate.of(2021, 11, 23), TestBean.Status.IN_PROGRESS), //
                new TestBean(12, "bar", false, LocalDate.of(2020, 1, 2), TestBean.Status.DONE) //
        );
        TEST_CSV = new String(CsvUtilTest.class.getResourceAsStream("/test.csv").readAllBytes(), StandardCharsets.UTF_8);
    }

    @DisabledOnOs(value = OS.WINDOWS, disabledReason = "CsvUtil.toCsv using ICSVWriter.DEFAULT_LINE_END, it needs to be optimized on windows (developed without windows)")
    @Test
    void shouldConvertToCsv() throws BaseException {
        // GIVEN
        List<TestBean> beans = TEST_BEANS;
        // WHEN
        String csv = CsvUtil.toCsv(beans, TestBean.class);
        // THEN
        System.out.println(csv);
        assertEquals(TEST_CSV, csv);
    }

    @Test
    void shouldParseCsvString() throws BaseException {
        // GIVEN
        String csv = TEST_CSV;
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(csv, TestBean.class);
        // THEN
        System.out.println(beans);
        assertEquals(TEST_BEANS, beans);
    }

    @Test
    void shouldParseCsvStream() throws BaseException {
        // GIVEN
        ByteArrayInputStream inputStream = new ByteArrayInputStream(TEST_CSV.getBytes(StandardCharsets.UTF_8));
        // WHEN
        List<TestBean> beans = CsvUtil.toBean(inputStream, TestBean.class);
        // THEN
        System.out.println(beans);
        assertEquals(TEST_BEANS, beans);
    }

}
