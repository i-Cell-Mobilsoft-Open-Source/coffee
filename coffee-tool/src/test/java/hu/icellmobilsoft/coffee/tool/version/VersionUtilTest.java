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

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import hu.icellmobilsoft.coffee.tool.version.VersionUtil;

/**
 * Test VersionUtil.
 *
 * @author balazs.joo
 */
@DisplayName("Testing VersionUtil")
public class VersionUtilTest {

    @DisplayName("Testing equalOrAbove()")
    @ParameterizedTest(name = "Testing equalOrAbove() with inputString:[{0}]; expected:[{1}]; result:[{2}]")
    // given
    @CsvSource(value = { //
            // "String str, String expected",
            "a,00a,false", //
            "aa,0aa,false", //
            "aaa,AAA,true", //
            "vaẞsvßafaaßgaAFCAẞẞ,VAẞSVẞAFAAẞGAAFCAẞẞ,true",//
            "1m3,1Milestone3,true",//
            "1.0-alpha-1-SNAPSHOT,1.0-alpha-1,false",//
            ",,false",//
            "0a,,false",//
            ",0a,false",//
    })
    void equalOrAbove(String v1, String v2, boolean expected) {

        Boolean actual = VersionUtil.equalOrAbove(v1, v2);

        Assertions.assertEquals(expected, actual);
    }

    @DisplayName("Testing below()")
    @ParameterizedTest(name = "Testing below() with inputString:[{0}]; expected:[{1}]; result:[{2}]")
    // given
    @CsvSource(value = { //
            // "String str, String expected",
            "a,00a,true", //
            "aa,0aa,true", //
            "aaa,AAA,false", //
            "vaẞsvßafaaßgaAFCAẞẞ,VAẞSVẞAFAAẞGAAFCAẞẞ,false",//
            "1m3,1Milestone3,false",//
            "1.0-alpha-1-SNAPSHOT,1.0-alpha-1,true"//
    })
    void below(String v1, String v2, boolean expected) {
        Boolean actual = VersionUtil.below(v1, v2);
        Assertions.assertEquals(expected, actual);
    }

}
