/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.crypto;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Test class for {@link SecretKeyUtil}
 *
 * @author Imre Scheffer
 * @since 2.5.0
 */
@DisplayName("Testing SecretKeyUtil")
public class SecretKeyUtilTest {

    @Nested
    @DisplayName("Testing defaultAES256SecretKeySpec()")
    class IsDifferenceGreaterThanTest {

        private static final String TEST_KEY = "test key";
        private final byte[] ENCODED_TEST_KEY_BYTE = new byte[] { 63, -109, -72, -16, 23, 115, -21, -95, 84, 82, -33, 19, 2, 59, 101, 63, -75, 67,
                -21, 87, 126, -29, -47, 18, 33, -65, 12, 63, -105, -123, -102, 74 };

        @Test
        @DisplayName("Testing defaultAES256SecretKeySpec() with test key")
        void withTestKey() throws BaseException {
            // given

            // when
            SecretKeySpec actual = SecretKeyUtil.defaultAES256SecretKeySpec(TEST_KEY.toCharArray());
            // then
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(new String(ENCODED_TEST_KEY_BYTE, StandardCharsets.UTF_8),
                    new String(actual.getEncoded(), StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("Testing defaultAES256SecretKeySpec() with blank key")
        void withBlankKey() {
            // given

            // when
            // then
            Assertions.assertThrows(InvalidParameterException.class, () -> SecretKeyUtil.defaultAES256SecretKeySpec(null));
            Assertions.assertThrows(InvalidParameterException.class, () -> SecretKeyUtil.defaultAES256SecretKeySpec("".toCharArray()));
        }
    }
}
