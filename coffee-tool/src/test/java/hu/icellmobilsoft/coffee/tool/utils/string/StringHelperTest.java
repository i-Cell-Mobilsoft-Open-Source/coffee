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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;
import io.smallrye.config.inject.ConfigExtension;

/**
 * @author mark.petrenyi
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Testing StringHelper")
class StringHelperTest {

    @SuppressWarnings("unchecked")
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld().addExtensions(ConfigExtension.class))
            .activate(RequestScoped.class).build();

    @BeforeEach
    public void init() {
        System.clearProperty(IConfigKey.LOG_SENSITIVE_KEY_PATTERN);
    }

    @DisplayName("Testing maskPropertyValue with pattern configured")
    @ParameterizedTest(name = "Testing maskPropertyValue(\"{0}\", \"{1}\"); expected:[{2}]; with pattern configured")
    // given
    @MethodSource("maskPropertyValueTestParamsCustomPattern")
    void maskPropertyValue(String key, String value, String expected) {
        // given
        System.setProperty(IConfigKey.LOG_SENSITIVE_KEY_PATTERN, ".*?alma.*?");
        // when
        String actual = StringHelper.maskPropertyValue(key, value);
        // then
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing maskPropertyValue with default pattern")
    @ParameterizedTest(name = "Testing maskPropertyValue(\"{0}\", \"{1}\"); expected:[{2}]; with default pattern")
    // given
    @MethodSource("maskPropertyValueTestParamsDefaultPattern")
    void maskPropertyValueDefaultPattern(String key, String value, String expected) {
        // given
        // when
        String actual = StringHelper.maskPropertyValue(key, value);
        // then
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing maskValueInXmlJson with pattern configured")
    @ParameterizedTest(name = "Testing maskValueInXmlJson(\"{0}\"); expected:[{1}]; with pattern configured")
    // given
    @MethodSource("maskValueInXmlJsonTestParamsCustomPattern")
    void maskValueInXmlJson(String text, String expected) {
        // given
        System.setProperty(IConfigKey.LOG_SENSITIVE_KEY_PATTERN, ".*?alma.*?");
        // when
        String actual = StringHelper.maskValueInXmlJson(text);
        // then
        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing maskValueInXmlJson with default pattern")
    @ParameterizedTest(name = "Testing maskValueInXmlJson(\"{0}\"); expected:[{1}]; with default pattern")
    // given
    @MethodSource("maskValueInXmlJsonTestParamsDefaultPattern")
    void maskValueInXmlJsonDefaultPattern(String text, String expected) {
        // given
        // when
        String actual = StringHelper.maskValueInXmlJson(text);
        // then
        Assertions.assertEquals(expected, actual);
    }

    static Stream<Arguments> maskPropertyValueTestParamsCustomPattern() {
        return Stream.of(//
                // Arguments.arguments(key, value, expected)
                Arguments.arguments("ab cAlmaX YZ", "teszt", "*"), //
                Arguments.arguments("ab cpasswordX YZ ", "teszt", "teszt") //
        );
    }

    static Stream<Arguments> maskPropertyValueTestParamsDefaultPattern() {
        return Stream.of(//
                // Arguments.arguments(key, value, expected)
                Arguments.arguments("ab cAlmaX YZ", "teszt", "teszt"), //
                Arguments.arguments("ab cpasswordX YZ", "teszt", "*") //
        );
    }

    static Stream<Arguments> maskValueInXmlJsonTestParamsCustomPattern() {
        return Stream.of(//
                // Arguments.arguments(key, value, expected)
                Arguments.arguments("<abcAlmaXYZ>teszt</abcAlmaXYZ>", "<abcAlmaXYZ>*</abcAlmaXYZ>"), //
                Arguments.arguments("<abcpasswordXYZ>teszt</abcpasswordXYZ>", "<abcpasswordXYZ>teszt</abcpasswordXYZ>") //
        );
    }

    static Stream<Arguments> maskValueInXmlJsonTestParamsDefaultPattern() {
        return Stream.of(//
                // Arguments.arguments(key, value, expected)
                Arguments.arguments("<abcAlmaXYZ>teszt</abcAlmaXYZ>", "<abcAlmaXYZ>teszt</abcAlmaXYZ>"), //
                Arguments.arguments("<abcpasswordXYZ>teszt</abcpasswordXYZ>", "<abcpasswordXYZ>*</abcpasswordXYZ>") //
        );
    }

}
