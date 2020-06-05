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
package hu.icellmobilsoft.coffee.tool.utils.bigdecimal;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.tool.utils.bigdecimal.BigDecimalUtil;

/**
 * @author balazs.joo
 */
@DisplayName("Testing BigDecimalUtil")
public class BigDecimalUtilTest {

    private static final BigDecimal[] BIG_DECIMAL_ARRAY = { new BigDecimal("1") };
    private static final Collection<BigDecimal> BIG_DECIMAL_COLLECTION = Collections.singletonList(new BigDecimal("1"));

    @Test
    @DisplayName("Testing containsBigDecimal(BigDecimal[], BigDecimal)")
    void containsBigDecimalArrayTest() {
        Assertions.assertTrue(BigDecimalUtil.containsBigDecimal(BIG_DECIMAL_ARRAY, new BigDecimal("1")));
        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(BIG_DECIMAL_ARRAY, new BigDecimal("10")));
        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(new BigDecimal[] {}, new BigDecimal("10")));
    }

    @Test
    @DisplayName("Testing containsBigDecimal(Collection<BigDecimal>, BigDecimal)")
    void containsBigDecimalCollectionTest() {
        Assertions.assertTrue(BigDecimalUtil.containsBigDecimal(BIG_DECIMAL_COLLECTION, new BigDecimal("1")));
        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(BIG_DECIMAL_COLLECTION, new BigDecimal("10")));
        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(Collections.emptyList(), new BigDecimal("10")));
    }

    @Test
    @DisplayName("Testing null values")
    void nullValues() {

        BigDecimal[] bigDecimalArray = null;
        Collection<BigDecimal> bigDecimals = null;

        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(BIG_DECIMAL_ARRAY, null));
        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(BIG_DECIMAL_COLLECTION, null));

        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(bigDecimalArray, new BigDecimal("10")));
        Assertions.assertFalse(BigDecimalUtil.containsBigDecimal(bigDecimals, new BigDecimal("10")));
    }
}
