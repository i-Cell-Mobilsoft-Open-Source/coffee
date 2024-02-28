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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import jakarta.enterprise.context.RequestScoped;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Class for testing {@link CsvUtil#toLocalizedCsv}
 *
 * @author martin.nagy
 * @since 1.8.0
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
class LocalizedCsvUtilTest {

    private static final String FNP_LOCALIZED_TEST_CSV = "/%1$s/localizedTest.csv";
    private static List<LocalizedTestBean> TEST_BEANS;
    private static String TEST_CSV;

    @WeldSetup
// EE10: WELD-001335: Ambiguous dependencies for type LocalizedHeaderColumnNameWithPositionMappingStrategy with qualifiers
//    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld().addBeanClass(LocalizedHeaderColumnNameWithPositionMappingStrategy.class)
//            .scanClasspathEntries().enableDiscovery()).activate(RequestScoped.class).build();
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld().enableDiscovery()).activate(RequestScoped.class).build();

    @BeforeAll
    static void beforeAll() throws IOException {
        TEST_BEANS = List.of( //
                new LocalizedTestBean(11, "foo", true, LocalDate.of(2021, 11, 23), LocalizedTestBean.Status.IN_PROGRESS), //
                new LocalizedTestBean(12, null, false, LocalDate.of(2020, 1, 2), LocalizedTestBean.Status.DONE) //
        );

        String os = System.lineSeparator().equals("\r\n") ? "win" : "unix";
        String localizedTestCsv = String.format(FNP_LOCALIZED_TEST_CSV, os);

        TEST_CSV = new String(LocalizedCsvUtilTest.class.getResourceAsStream(localizedTestCsv).readAllBytes(), StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldConvertToCsv() throws BaseException {
        // GIVEN
        List<LocalizedTestBean> beans = TEST_BEANS;
        // WHEN
        String csv = CsvUtil.toLocalizedCsv(beans, LocalizedTestBean.class, "hu");
        // THEN
        System.out.println(csv);
        assertEquals(TEST_CSV, csv);
    }

}
