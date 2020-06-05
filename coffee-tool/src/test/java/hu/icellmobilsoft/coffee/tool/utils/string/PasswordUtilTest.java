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
package hu.icellmobilsoft.coffee.tool.utils.string;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.commons.util.StringUtils;

import hu.icellmobilsoft.coffee.tool.utils.string.PasswordUtil;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing PasswordUtil")
class PasswordUtilTest {
    private static final String TEST_STRONG_PASSWORD = "p@ssStrong1";
    private static final String TEST_LONG_WEAK_PASSWORD = "weakweakweak";
    private static final String TEST_WEAK_PASSWORD = "weak";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_PASSWORD_NEG = "passwordneg";
    private static final String TEST_PASSWORD_HASHED = "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86";

    @Test
    @DisplayName("Testing encodeString()")
    void encodeString() {
        // given

        // when
        String actual = PasswordUtil.encodeString(TEST_PASSWORD);

        // then
        assertEquals(TEST_PASSWORD_HASHED, actual);
    }

    @DisplayName("Testing isSame()")
    @ParameterizedTest(name = "Testing isSame() with candidate:[{0}], expected:[{1}]")
    // given
    @CsvSource(value = { //
            TEST_PASSWORD + "," + true, //
            TEST_PASSWORD_NEG + "," + false//
    })
    void isSame(String candidate, Boolean expected) {
        // when
        boolean actual = PasswordUtil.isSame(candidate, TEST_PASSWORD_HASHED);

        // then
        assertEquals(expected, actual);
    }

    @DisplayName("Testing isStrong")
    @ParameterizedTest(name = "Testing isStrong - input:[{0}], expected:[{1}]")
    // given
    @CsvSource(value = { //
            TEST_STRONG_PASSWORD + "," + true, //
            TEST_LONG_WEAK_PASSWORD + "," + false, //
            TEST_WEAK_PASSWORD + "," + false//
    })
    void isStrong(String candidate, Boolean expected) {
        // when
        boolean actual = PasswordUtil.isStrong(candidate);

        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing null values")
    void nullValues() {

        Assertions.assertNull(PasswordUtil.encodeString(null));
        Assertions.assertTrue(PasswordUtil.isSame(null, null));
        Assertions.assertFalse(PasswordUtil.isSame("TEST", null));
        Assertions.assertFalse(PasswordUtil.isSame(null, "TEST"));
        Assertions.assertTrue(PasswordUtil.isSame("", null));
        Assertions.assertFalse(PasswordUtil.isSame(null, ""));
        Assertions.assertFalse(PasswordUtil.isStrong(null));
    }
}
