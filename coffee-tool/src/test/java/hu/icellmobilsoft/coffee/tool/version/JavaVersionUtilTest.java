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
package hu.icellmobilsoft.coffee.tool.version;

import java.security.InvalidParameterException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import hu.icellmobilsoft.coffee.tool.version.JavaVersion;
import hu.icellmobilsoft.coffee.tool.version.JavaVersionUtil;

/**
 * Test JavaVersionUtil.
 *
 * @author arnold.bucher
 */
@DisplayName("Testing JavaVersionUtil")
public class JavaVersionUtilTest {

    @DisplayName("Testing java versions")
    @ParameterizedTest(name = "Testing below() with version:[{0}];")
    // given
    @CsvSource(value = {//
            "12.0.2",//
            "11.0.4",//
            "10.0.99",//
            "9.0.99",//
            "1.8.0_221",//
            "1.8.0_111",//
            "1.7.0_231",//
            "1.6.0_221",//
            "1.5.0_99",//
            "1.4.2_99",//
            "1.4.2_99_A0A"//
    })
    void javaVersionTest(String version) {
        JavaVersion javaVersion = JavaVersionUtil.getJavaVersion(version);
        String[] versionParts = version.split("\\.");

        Assertions.assertEquals(versionParts[0], javaVersion.getMajor().toString());
        Assertions.assertEquals(versionParts[1], javaVersion.getFeature().toString());
        Assertions.assertEquals(versionParts[2], javaVersion.getPatch());
        if (versionParts[2].contains("_")) {
            Assertions.assertNotNull(javaVersion.getPatchUpdate());
        }
    }

    @Test
    @DisplayName("Testing wrong java versions")
    void wrongJavaVersionTest() {
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion("1AAA.4.2_99"));
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion("1.4BBBB.2_99"));
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion("1.6.0_221CCCC"));
        Assertions.assertNull(JavaVersionUtil.getJavaVersion(".").getMajor());
        Assertions.assertEquals(1, JavaVersionUtil.getJavaVersion("1").getMajor());
        Assertions.assertEquals(1, JavaVersionUtil.getJavaVersion("1.").getMajor());
        Assertions.assertEquals(2, JavaVersionUtil.getJavaVersion("1.2").getFeature());
        Assertions.assertEquals(2, JavaVersionUtil.getJavaVersion("1.2.").getFeature());
        Assertions.assertEquals("3", JavaVersionUtil.getJavaVersion("1.2.3").getPatch());
        Assertions.assertEquals("3_", JavaVersionUtil.getJavaVersion("1.2.3_").getPatch());
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion("1.2.3_!$%#"));
    }

    @Test
    @DisplayName("Testing missing java version")
    void missingJavaVersionTest() {
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion(null));
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion(""));
        Assertions.assertThrows(InvalidParameterException.class, () -> JavaVersionUtil.getJavaVersion(" "));
    }

    @Test
    @DisplayName("Testing print java version")
    void printJavaVersionTest() {
        String expected = "JavaVersion{major=1, feature=8, patch='0_111', patchUpdate=111}";
        Assertions.assertEquals(expected, JavaVersionUtil.getJavaVersion("1.8.0_111").toString());
    }

}
