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
package hu.icellmobilsoft.coffee.module.configdoc.data;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DynamicDocData}
 * 
 * @author mark.petrenyi
 * @since 1.10.0
 */
@DisplayName("Testing hu.icellmobilsoft.coffee.module.configdoc.data.DynamicDocData")
class DynamicDocDataTest {

    public static final String TEST_TITLE = "title";
    public static final String TEST_DESCRIPTION = "description";
    public static final String TEST_VAR_1 = "var1";

    @Test
    void testMergeNullFields() {
        // given
        DynamicDocData base = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        DynamicDocData toBeMerged = new DynamicDocData();
        DynamicDocData expected = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        // when
        base.merge(toBeMerged);
        // then
        Assertions.assertEquals(expected, base);
        Assertions.assertNotEquals(toBeMerged, base);
    }

    @Test
    void testMergeNull() {
        // given
        DynamicDocData base = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        DynamicDocData expected = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        // when
        base.merge(null);
        // then
        Assertions.assertEquals(expected, base);
    }

    @Test
    void testMergeEmptyFields() {
        // given
        DynamicDocData base = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        DynamicDocData toBeMerged = createData("", "", DynamicDocDataTest.class, new String[0]);
        toBeMerged.setTemplateClassName("");
        DynamicDocData expected = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        // when
        base.merge(toBeMerged);
        // then
        Assertions.assertEquals(expected, base);
        Assertions.assertNotEquals(toBeMerged, base);
    }

    @Test
    void testMergeValueOverrides() {
        // given
        DynamicDocData base = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        DynamicDocData toBeMerged = createData("override title", "override descripition", DynamicDocData.class, TEST_VAR_1, "var2");
        DynamicDocData unexpected = createData(TEST_TITLE, TEST_DESCRIPTION, DynamicDocDataTest.class, TEST_VAR_1);
        // when
        base.merge(toBeMerged);
        // then
        Assertions.assertEquals(toBeMerged, base);
        Assertions.assertNotEquals(unexpected, base);
    }

    @Test
    void testComparator() {
        // given
        List<DynamicDocData> dataList = List.of(//
                createData("a", "v2"), //
                createData("b", "v1"), //
                createData("a", "v1", "v2"), //
                createData("b", new String[0]), //
                createData("b"), //
                createData("a"), //
                createData("a", "v1")//
        );

        List<DynamicDocData> expected = List.of(//
                createData("a"), //
                createData("a", "v1"), //
                createData("a", "v1", "v2"), //
                createData("a", "v2"), //
                createData("b"), //
                createData("b", new String[0]), //
                createData("b", "v1")//
        );
        // when
        List<DynamicDocData> actual = dataList.stream().sorted(DynamicDocData.COMPARATOR).collect(Collectors.toList());
        // then
        Assertions.assertEquals(expected, actual);
    }

    private DynamicDocData createData(String title, String... variables) {
        return createData(title, null, null, variables);
    }

    private DynamicDocData createData(String title, String description, Class<?> templateClass, String... variables) {
        DynamicDocData data = new DynamicDocData();
        data.setTitle(title);
        data.setDescription(description);
        data.setTemplateClassName(templateClass != null ? templateClass.getCanonicalName() : null);
        data.setTemplateVariables(variables);
        return data;
    }

}
