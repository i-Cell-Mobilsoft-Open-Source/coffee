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
package hu.icellmobilsoft.coffee.tool.utils.number;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import hu.icellmobilsoft.coffee.tool.utils.number.NumberUtil;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing NumberUtil")
class NumberUtilTest {

    @TestInstance(PER_CLASS)
    @Nested
    @DisplayName("Testing toInt()")
    class ToIntTest {

        @DisplayName("Testing toInt()")
        @ParameterizedTest(name = "Testing toInt() with Number:[{0}], expected int:[{1}]")
        // given
        @MethodSource("givenWeHaveTestData")
        void withoutDefault(Number source, int expected) {

            // when
            int actual = NumberUtil.toInt(source);

            // then
            assertEquals(expected, actual);
        }

        @DisplayName("Testing toInt() with null input and defaultValue")
        @Test
        void withDefault() {
            // given
            int defaultValue = 120;

            // when
            int actual = NumberUtil.toInt(null, defaultValue);

            // then
            assertEquals(defaultValue, actual);
        }

        Stream<Arguments> givenWeHaveTestData() {
            return Stream.of(//
                    Arguments.arguments(Integer.valueOf("1"), 1), //
                    Arguments.arguments(Integer.valueOf("15"), 15), //
                    Arguments.arguments(Double.valueOf("1670.13"), 1670), //
                    Arguments.arguments(new BigDecimal("1670.13"), 1670), //
                    Arguments.arguments(Long.valueOf("100"), 100), //
                    Arguments.arguments(null, 0)//
            );
        }

    }

    @TestInstance(PER_CLASS)
    @Nested
    @DisplayName("Testing toDouble()")
    class ToDoubleTest {

        @DisplayName("Testing toDouble()")
        @ParameterizedTest(name = "Testing toDouble() with Number:[{0}], expected double:[{1}]")
        // given
        @MethodSource("givenWeHaveTestData")
        void withoutDefault(Number source, double expected) {

            // when
            double actual = NumberUtil.toDouble(source);

            // then
            assertEquals(expected, actual);
        }

        @DisplayName("Testing toDouble() with null input and defaultValue")
        @Test
        void withDefault() {
            // given
            double defaultValue = 12.34;

            // when
            double actual = NumberUtil.toDouble(null, defaultValue);

            // then
            assertEquals(defaultValue, actual);
        }

        Stream<Arguments> givenWeHaveTestData() {
            return Stream.of(//
                    Arguments.arguments(Integer.valueOf("1"), 1d), //
                    Arguments.arguments(Integer.valueOf("15"), 15d), //
                    Arguments.arguments(Double.valueOf("1670.13"), 1670.13d), //
                    Arguments.arguments(new BigDecimal("1670.13"), 1670.13d), //
                    Arguments.arguments(Long.valueOf("100"), 100d), //
                    Arguments.arguments(null, 0d)//
            );
        }

    }
}
