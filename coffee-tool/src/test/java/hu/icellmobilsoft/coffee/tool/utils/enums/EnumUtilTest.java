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
package hu.icellmobilsoft.coffee.tool.utils.enums;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import hu.icellmobilsoft.coffee.tool.utils.enums.EnumUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing EnumUtil")
class EnumUtilTest {

    private enum TestingEnumFrom {
        ABC, XYZ, NOT_PRESENT
    }

    private enum TestingEnumTo {
        ABC, XYZ
    }

    @DisplayName("Testing convert()")
    @ParameterizedTest(name = "Testing convert() with TestingEnumFrom:[{0}], expected TestingEnumTo:[{1}]")
    // given
    @MethodSource("enumProvider")
    void convert(TestingEnumFrom input, TestingEnumTo expected) {
        // when
        TestingEnumTo actual = EnumUtil.convert(input, TestingEnumTo.class);

        // then
        assertEquals(expected, actual);
    }

    @DisplayName("Testing equalName()")
    @ParameterizedTest(name = "Testing equalName() TestingEnumFrom:[{0}], with TestingEnumTo:[{1}], expected:[{2}]")
    // given
    @MethodSource("enumProvider")
    void equalName(TestingEnumFrom from, TestingEnumTo to, Boolean expected) {
        // when
        Assertions.assertEquals(EnumUtil.equalName(from, to), expected);
    }

    static Stream<Arguments> enumProvider() {
        return Stream.of(//
                Arguments.arguments(TestingEnumFrom.ABC, TestingEnumTo.ABC, Boolean.TRUE), //
                Arguments.arguments(TestingEnumFrom.XYZ, TestingEnumTo.XYZ, Boolean.TRUE), //
                Arguments.arguments(TestingEnumFrom.NOT_PRESENT, null, Boolean.FALSE), //
                Arguments.arguments(null, null, Boolean.TRUE)//
        );
    }
}
