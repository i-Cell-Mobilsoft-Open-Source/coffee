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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author balazs.joo
 */
@DisplayName("Testing StringUtil")
public class StringUtilTest {

    private static final String TEST_PATTERN = ".*?(pass|secret).*?";

    private static final String TEST_XML = "<test>\n" + //
            "    <user>\n" + //
            "        <userName>Teszt User</userName>\n" + //
            "        <userPasswordHash>Teszt password hash</userPasswordHash>\n" + //
            "        <userSecretKey>Some secret key</userSecretKey>\n" + //
            "    </user>\n" + //
            "</test>";
    private static final String MASKED_XML = "<test>\n" + //
            "    <user>\n" + //
            "        <userName>Teszt User</userName>\n" + //
            "        <userPasswordHash>*</userPasswordHash>\n" + //
            "        <userSecretKey>*</userSecretKey>\n" + //
            "    </user>\n" + //
            "</test>";

    private static final String TEST_JSON = "{\n" + //
            "   \"user\": {\n" + //
            "      \"userName\": \"Teszt User\",\n" + //
            "      \"userPasswordHash\": \"Teszt password hash\",\n" + //
            "      \"userSecretKey\": \"Some secret key\"\n" + //
            "   }\n" + //
            "}";

    private static final String MASKED_JSON = "{\n" + //
            "   \"user\": {\n" + //
            "      \"userName\": \"Teszt User\",\n" + //
            "      \"userPasswordHash\": \"*\",\n" + //
            "      \"userSecretKey\": \"*\"\n" + //
            "   }\n" + //
            "}";

    @DisplayName("Testing upperCase()")
    @ParameterizedTest(name = "Testing upperCase() with inputString:[{0}]; expected:[{1}]; result:[{2}]")
    // given
    @CsvSource(value = { //
            // "String str, String expected",
            "a,00a,false", //
            "aa,0aa,false", //
            "aaa,AAA,true", //
            "vaẞsvßafaaßgaAFCAẞẞ,VAẞSVẞAFAAẞGAAFCAẞẞ,true"//
    })
    void upperCase(String str, String expected, boolean result) {

        String upper = StringUtil.upperCase(str);

        Assertions.assertEquals(StringUtils.equals(upper, expected), result);
    }

    @Test
    @DisplayName("Testing upperCase null value")
    void upperCaseNullValue() {

        Assertions.assertNull(StringUtil.upperCase(null));
    }

    @DisplayName("Testing encodeValue()")
    @ParameterizedTest(name = "Testing encodeValue() with inputString:[{0}]; expected:[{1}]; result:[{2}]")
    // given
    @CsvSource(value = { //
            // "String str, String expected",
            "szőlő,sz%C5%91l%C5%91,true", //
            "szőlő,szőlő,false", //
    })
    void encodeValue(String str, String expected, boolean result) {

        String encoded = StringUtil.encodeValue(str);

        Assertions.assertEquals(StringUtils.equals(encoded, expected), result);
    }

    @Test
    @DisplayName("Testing encodeValue null value")
    void encodeValueNullValue() {

        Assertions.assertNull(StringUtil.encodeValue(null));
    }

    @DisplayName("Testing maskPropertyValue(String key, String value, String keyPattern)")
    @ParameterizedTest(name = "Testing maskPropertyValue(\"{0}\", \"{1}\", \"{2}\"); expected:[{3}]")
    // given
    @MethodSource("maskPropertyValueTestParams")
    void maskPropertyValue(String key, String value, String keyPattern, String expected) {
        // when
        String actual = StringUtil.maskPropertyValue(key, value, keyPattern);
        // then
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing maskValueInXmlJson(String text, String keyPattern)")
    @ParameterizedTest(name = "Testing maskValueInXmlJson(\"{0}\", \"{1}\"); expected:[{2}]")
    // given
    @MethodSource("maskValueInXmlJsonTestParams")
    void maskValueInXmlJson(String text, String keyPattern, String expected) {
        // when
        String actual = StringUtil.maskValueInXmlJson(text, keyPattern);
        // then
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing maskUriAuthenticationCredentials(String uri)")
    @ParameterizedTest(name = "Testing maskUriAuthenticationCredentials(\"{0}\"); expected:[{1}]")
    // given
    @MethodSource("maskUriAuthenticationCredentialsTestParams")
    void maskUriAuthenticationCredentials(String uri, String expected) {
        // when
        String actual = StringUtil.maskUriAuthenticationCredentials(uri);
        // then
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> maskPropertyValueTestParams() {
        return Stream.of(//
                // Arguments.arguments(key, value, keyPattern, expected)
                Arguments.arguments("Password", "abc", TEST_PATTERN, "*"), //
                Arguments.arguments("verySecretToken", "abc", TEST_PATTERN, "*"), //
                Arguments.arguments("userName", "abc", TEST_PATTERN, "abc"), //
                Arguments.arguments("pass", "abc", TEST_PATTERN, "*"), //
                Arguments.arguments("userPassword", "abc", TEST_PATTERN, "*"), //
                Arguments.arguments(null, "abc", TEST_PATTERN, "abc"), //
                Arguments.arguments("userPassword", null, TEST_PATTERN, null), //
                Arguments.arguments("userPassword", "abc", null, "abc")//
        );
    }

    static Stream<Arguments> maskValueInXmlJsonTestParams() {
        return Stream.of(//
                // Arguments.arguments(text, keyPattern, expected)
                Arguments.arguments(TEST_XML, TEST_PATTERN, MASKED_XML), //
                Arguments.arguments(TEST_JSON, TEST_PATTERN, MASKED_JSON), //
                Arguments.arguments("\"userName\":\"abc\"", null, "\"userName\":\"abc\""), //
                Arguments.arguments(null, TEST_PATTERN, null), //
                Arguments.arguments("\"userPassword\":\"abc\"", null, "\"userPassword\":\"abc\"")//
        );
    }

    static Stream<Arguments> maskUriAuthenticationCredentialsTestParams() {
        return Stream.of(//
                // Arguments.arguments(uri, expected)
                Arguments.arguments("mongodb://username:password@localhost:12345", "mongodb://*:*@localhost:12345"), //
                Arguments.arguments("ftp://user:name:pass:word@localhost:12345", "ftp://*:*@localhost:12345"), //
                Arguments.arguments("any:////user:?[name#@:p]ass:word@@localhost:12345", "any://*:*@localhost:12345"), //
                Arguments.arguments("http://localhost:12345", "http://localhost:12345"), //
                Arguments.arguments(null, null), //
                Arguments.arguments("", "")//
        );
    }
}
