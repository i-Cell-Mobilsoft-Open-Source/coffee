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

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author balazs.joo
 */
@DisplayName("Testing EncodeUtil")
public class EncodeUtilTest {

    @DisplayName("Testing Sha512()")
    @ParameterizedTest(name = "Testing Sha512() with input:[{0}]; expected:[{1}]")
    // given
    @MethodSource("Sha512Provider")
    void Sha512(String str, String expected) {

        String actual = EncodeUtil.Sha512(str);

        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing byteToHex()")
    @ParameterizedTest(name = "Testing byteToHex() with input:[{0}]; expected:[{1}]")
    // given
    @MethodSource("byteToStringProvider")
    void byteToHex(byte[] bytes, String expected) {

        String actual = EncodeUtil.byteToHex(bytes);

        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> Sha512Provider() {
        return Stream.of(//
                Arguments.arguments("vfds32reqcCDACf23rfca", "2AF497FD142FB8312FE6659426B028B04FFABEE7B5627772984E157EE13DF0B9519EC9BFE523E4FC335E33763A0F73A7BA1CB0AB3505AEFCABE4EE90C283FEA0"), //
                Arguments.arguments("c15611651erd1qwec1612DFEFcvacas", "DCC71079D7396B41F2841A4E00441645E2005D9E41910675F330B304473C64309CF1367C19F84F660FDBB7494E076167FA2725D109D2CAE5F95F23B51A827E3C"), //
                Arguments.arguments("123r31_...dwq615c5_qEE'D", "EF2D2CECE40BE0B0DE80F3CA7919ACB6CA4271B08F2FFF6FCDBC9ECC54997EB7B53002815D157149CAC2ED5059D9BA91F8467CD98F4DB9B21DA25C745D9EEBCF"), //
                Arguments.arguments(null, null)//
        );
    }

    static Stream<Arguments> byteToStringProvider() {
        return Stream.of(//
                Arguments.arguments(new byte[] { 1, 0, 111, -10, 0, 0, 0 }, "01006ff6000000"), //
                Arguments.arguments(new byte[] { 1, -128, 36, -10, 0, 43, 10, 0, 43, 10 }, "018024f6002b0a002b0a"), //
                Arguments.arguments(new byte[] { 8, 36, -10, 0, 43, 10, 0 }, "0824f6002b0a00"), //
                Arguments.arguments(null, null)//
        );
    }
}
