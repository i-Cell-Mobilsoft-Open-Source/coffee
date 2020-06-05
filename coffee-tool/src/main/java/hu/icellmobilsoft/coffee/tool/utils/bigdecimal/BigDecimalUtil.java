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

import javax.enterprise.inject.Vetoed;
import javax.xml.datatype.DatatypeConstants;

/**
 * Helper class to handle BigDecimal retain precision of it's double value
 *
 * @author robert.kaplar
 * @since 1.0.0
 */
@Vetoed
public class BigDecimalUtil {

    /**
     * Value 0.01
     */
    public static final BigDecimal PERCENTAGE_1 = new BigDecimal("0.01");

    /**
     * Check that the array contains the specific value
     *
     * @param bigDecimals
     * @param value
     */
    public static boolean containsBigDecimal(BigDecimal[] bigDecimals, BigDecimal value) {
        if (bigDecimals == null || value == null) {
            return false;
        }
        for (BigDecimal item : bigDecimals) {
            if (item.compareTo(value) == DatatypeConstants.EQUAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that the list contains the specific value
     *
     * @param bigDecimals
     * @param value
     */
    public static boolean containsBigDecimal(Collection<BigDecimal> bigDecimals, BigDecimal value) {
        if (bigDecimals == null || value == null) {
            return false;
        }
        for (BigDecimal item : bigDecimals) {
            if (item.compareTo(value) == DatatypeConstants.EQUAL) {
                return true;
            }
        }
        return false;
    }
}
