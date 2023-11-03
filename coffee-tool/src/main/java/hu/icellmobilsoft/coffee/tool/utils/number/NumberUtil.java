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

/**
 * Util for number types.
 *
 * @author karoly.tamas
 * @since 1.0.0
 */
public class NumberUtil {

    private static final int DEFAUT_INT_VALUE = 0;
    private static final double DEFAUT_DOUBLE_VALUE = 0d;

    /**
     * Default constructor, constructs a new object.
     */
    public NumberUtil() {
        super();
    }

    /**
     * Converts {@link Number} to {@code int}.
     *
     * @param number
     *            {@code Number} to convert
     * @return converted {@code int} or {@code DEFAUT_INT_VALUE} if {@code number} is null
     */
    public static int toInt(Number number) {
        return toInt(number, DEFAUT_INT_VALUE);
    }

    /**
     * Converts {@link Number} to {@code int}.
     *
     * @param number
     *            {@code Number} to convert
     * @param defaultValue
     *            default {@code int} value
     * @return converted {@code int} or {@code defaultValue} if {@code number} is null
     */
    public static int toInt(Number number, int defaultValue) {
        return number != null ? number.intValue() : defaultValue;
    }

    /**
     * Converts {@link Number} to {@code double}.
     *
     * @param number
     *            {@code Number} to convert
     * @return converted {@code double} or {@code DEFAUT_DOUBLE_VALUE} if {@code number} is null
     */
    public static double toDouble(Number number) {
        return toDouble(number, DEFAUT_DOUBLE_VALUE);
    }

    /**
     * Converts {@link Number} to {@code double}.
     *
     * @param number
     *            {@code Number} to convert
     * @param defaultValue
     *            default {@code double} value
     * @return converted {@code double} or {@code defaultValue} if {@code number} is null
     */
    public static double toDouble(Number number, double defaultValue) {
        return number != null ? number.doubleValue() : defaultValue;
    }
}
