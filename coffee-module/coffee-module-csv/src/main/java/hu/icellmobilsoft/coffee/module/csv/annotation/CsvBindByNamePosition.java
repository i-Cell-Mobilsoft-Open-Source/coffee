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
package hu.icellmobilsoft.coffee.module.csv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kellett, mert a "gyári" nem tudott sorrendezést, ami tudott, az nem gyártott headert
 *
 * @author andras.bognar
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvBindByNamePosition {

    /**
     * Whether or not the annotated field is required to be present in every data set of the input.
     *
     * @return If the field is required to contain information.
     */
    boolean required() default false;

    /**
     * The column position in the input that is used to fill the annotated field.
     *
     * @return The position of the column in the CSV file from which this field should be taken. This column number is zero-based.
     */
    int position();

    /**
     * If not specified, the name of the column must be identical to the name of the field.
     *
     * @return The name of the column in the CSV file from which this field should be taken.
     */
    String column() default "";

    /**
     * Defines the locale to be used for decoding the argument.
     * <p>
     * If not specified, the current default locale is used. The locale must be one recognized by {@link java.util.Locale}. Locale conversion is
     * supported for the following data types:
     * <ul>
     * <li>byte and {@link java.lang.Byte}</li>
     * <li>float and {@link java.lang.Float}</li>
     * <li>double and {@link java.lang.Double}</li>
     * <li>int and {@link java.lang.Integer}</li>
     * <li>long and {@link java.lang.Long}</li>
     * <li>short and {@link java.lang.Short}</li>
     * <li>{@link java.math.BigDecimal}</li>
     * <li>{@link java.math.BigInteger}</li>
     * <li>All time data types supported by {@link com.opencsv.bean.CsvDate}</li>
     * </ul>
     * <p>
     * The locale must be in a format accepted by {@link java.util.Locale#forLanguageTag(java.lang.String)}
     * </p>
     * <p>
     * Caution must be exercized with the default locale, for the default locale for numerical types does not mean the locale of the running program,
     * such as en-US or de-DE, but rather <em>no</em> locale. Numbers will be parsed more or less the way the Java compiler would parse them. That
     * means, for instance, that thousands separators in long numbers are not permitted, even if the locale of the running program would accept them.
     * When dealing with locale-sensitive data, it is always best to specify the locale explicitly.
     * </p>
     *
     * @return The locale selected. The default is indicated by an empty string.
     */
    String locale() default "";
}
