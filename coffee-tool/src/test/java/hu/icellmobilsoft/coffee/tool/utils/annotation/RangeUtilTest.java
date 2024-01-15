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
package hu.icellmobilsoft.coffee.tool.utils.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import hu.icellmobilsoft.coffee.cdi.annotation.Range;
import hu.icellmobilsoft.coffee.cdi.annotation.Range.RangeLiteral;

/**
 * RangeUtil test class
 *
 * @author imre.scheffer
 */
@DisplayName("Testing RangeUtil")
public class RangeUtilTest {

    @DisplayName("Testing inRange")
    @ParameterizedTest(name = "Testing inRange - input:[{0}]-[{1}], expected:[{2}]")
    // given
    @CsvSource(value = { //
            ",," + true, //
            "0,," + true, //
            ",0," + false, //
            ",2," + true, //
            "1,," + true, //
            ",1," + true, //
            "1,1," + true, //
            "0,0.1," + false, //
            "1.1,," + false, //
            "1.1,2," + false, //
    })
    void inRange(String from, String to, Boolean expected) {
        Range r = new RangeLiteral(from, to);

        // when
        boolean in = RangeUtil.inRange(r, "1");

        // then
        String message = MessageFormat.format("Failed test with value [{0}]-[{1}]", from, to);
        assertEquals(expected, in, message);
    }

    @DisplayName("Testing inRanges")
    @ParameterizedTest(name = "Testing inRanges - input:[{0}], expected:[{1}]")
    // given
    @CsvSource(value = { //
            "0," + true, //
            "0.1," + false, //
            "1," + true, //
            "1.1," + false, //
            "2," + true, //
            "2.1," + true, //
            "3.1," + false, //
            "4," + true, //
            "5.1," + true, //
    })
    void inRanges(String version, Boolean expected) {
        Range r1 = new RangeLiteral("0", "0");
        Range r2 = new RangeLiteral("1", "1");
        Range r3 = new RangeLiteral("2", "3");
        Range r4 = new RangeLiteral("4", "");

        // when
        boolean in = RangeUtil.inRanges(new Range[] { r1, r2, r3, r4 }, version);

        // then
        String message = MessageFormat.format("Failed test with value version [{0}]", version);
        assertEquals(expected, in, message);
    }

    @Test
    @DisplayName("Testing null values")
    void nullValues() {
        Range r = new RangeLiteral("1", "1");
        Assertions.assertFalse(RangeUtil.inRange(null, null));
        Assertions.assertFalse(RangeUtil.inRange(r, null));
        Assertions.assertFalse(RangeUtil.inRanges(null, null));
        Assertions.assertFalse(RangeUtil.inRanges(new Range[] { r }, null));
    }
}
