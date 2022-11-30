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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing RandomUtil")
class RandomUtilTest {
    private static final String ID_REGEX = "[0-9A-Za-z]{16}";

    @Test
    @DisplayName("Testing generateId() against regex:" + ID_REGEX)
    void generateId() {
        // given

        // when
        for (int i = 0; i < 1297; i++) {
            String actual = RandomUtil.generateId();

            // then
            Assertions.assertNotNull(actual);
            Assertions.assertTrue(actual.matches(ID_REGEX));
        }
    }

    @DisplayName("Testing paddl()")
    @ParameterizedTest(name = "Testing paddl() with inputString:[{0}],length:3, padd:0; expected:[{1}]")
    // given
    @CsvSource(value = { //
            // "String str, String expected",
            "a,00a", //
            "aa,0aa", //
            "aaa,aaa", //
            "aaaa,aaaa"//
    })
    void paddL(String str, String expected) {
        // given
        int length = 3;
        char padd = '0';

        // when
        String actual = RandomUtil.paddL(str, length, padd);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing convertToRadix()")
    @ParameterizedTest(name = "Testing convertToRadix() with decimal input:[{0}], radix:[{1}]; expected:[{2}]")
    // given
    @CsvSource(value = { //
            // "long input, long radix, String expected",
            "1,62,1", //
            "123,62,1z", //
            "1625374,16, 18CD1E"//
    })
    void convertToRadix(long input, long radix, String expected) {
        // when
        String actual = RandomUtil.convertToRadix(input, radix);

        // then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Testing generateToken()")
    void generateToken() {
        // when

        // then
        Assertions.assertNotNull(RandomUtil.generateToken());
    }

    @Test
    @DisplayName("Testing null values")
    void nullValues() {
        Assertions.assertNull(RandomUtil.paddL(null, 0, 'a'));
        Assertions.assertTrue(StringUtils.isBlank(RandomUtil.convertToRadix(0, 0)));
    }
}
