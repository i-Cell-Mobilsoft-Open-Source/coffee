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

import javax.enterprise.inject.Vetoed;

/**
 * Util for number types.
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
@Vetoed
public class NumberUtil {

    private static final int DEFAUT_INT_VALUE = 0;
    private static final double DEFAUT_DOUBLE_VALUE = 0d;

    /**
     * Number > int
     *
     * @param number
     */
    public static int toInt(Number number) {
        return toInt(number, DEFAUT_INT_VALUE);
    }

    /**
     * Number > int
     *
     * @param number
     * @param defaultValue
     */
    public static int toInt(Number number, int defaultValue) {
        return number != null ? number.intValue() : defaultValue;
    }

    /**
     * Number > double
     *
     * @param number
     */
    public static double toDouble(Number number) {
        return toDouble(number, DEFAUT_DOUBLE_VALUE);
    }

    /**
     * Number > double
     *
     * @param number
     * @param defaultValue
     */
    public static double toDouble(Number number, double defaultValue) {
        return number != null ? number.doubleValue() : defaultValue;
    }
}
