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
import java.util.regex.Matcher;

/**
 * Specifies a binding between a column name and index of the CSV input and a field in a bean.
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

    /**
     * If this is anything but an empty string, it will be used as a regular expression to extract part of the input before conversion to the bean
     * field.
     * <p>
     * An empty string behaves as if the regular expression {@code ^(.*)$} had been specified.
     * </p>
     * <p>
     * The regular expression will be compiled and every field of input will be passed through it, naturally after the input has been normalized
     * (quotations and escape characters removed). The first capture group will be extracted, and that string will be passed on to the appropriate
     * conversion routine for the bean field in question.
     * </p>
     * <p>
     * This makes it possible to easily convert input fields with forms like {@code Grade: 94.2} into {@code 94.2}, which can then be converted to a
     * floating point bean field, all without writing a custom converter.
     * </p>
     * <p>
     * The regular expression is applied to the entire string in question (i.e. with {@link Matcher#matches()}), instead of just the beginning of the
     * string ({@link Matcher#lookingAt()}) or anywhere in the string ({@link Matcher#find()}). If it fails to match, the input string is passed
     * unchanged to the appropriate conversion routine for the bean field. The reason for this is two-fold:
     * </p>
     * <ol>
     * <li>The matching may occur against an empty string. If the field is not required, this is legitimate, but it's likely the regular expression is
     * not written to accommodate this possibility, and it may indeed not be at all desirable to.</li>
     * <li>If there is an error in either the regular expression or the input that causes the match to fail, there is a good likelihood that the
     * subsequent conversion will fail with a {@link com.opencsv.exceptions.CsvDataTypeMismatchException} if the input is not being converted into a
     * simple string.</li>
     * </ol>
     * <p>
     * This is the inverse operation of {@link #format()}.
     * </p>
     *
     * @return A regular expression, the first capture group of which will be used for conversion to the bean field
     */
    String capture() default "";

    /**
     * If this is anything but an empty string, it will be used as a format string for {@link java.lang.String#format(String, Object...)} on writing.
     * <p>
     * An empty string behaves as if the format string {@code "%s"} had been specified.
     * </p>
     * <p>
     * The format string, if it is not empty, should contain one and only one {@code %s}, which will be replaced by the string value of the bean field
     * after conversion. If, however, the bean field is empty, then the output will be empty as well, as opposed to passing an empty string to this
     * format string and using that as the output.
     * </p>
     * <p>
     * This is the inverse operation of {@link #capture()}.
     * </p>
     *
     * @return A format string for writing fields
     */
    String format() default "";

    /**
     * A profile can be used to annotate the same field differently for different inputs or outputs.
     * <p>
     * Perhaps you have multiple input sources, and they all use different header names or positions for the same data. With profiles, you don't have
     * to create different beans with the same fields and different annotations for each input. Simply annotate the same field multiple times and
     * specify the profile when you parse the input.
     * </p>
     * <p>
     * The same applies to output: if you want to be able to represent the same data in multiple CSV formats (that is, with different headers or
     * orders), annotate the bean fields multiple times with different profiles and specify which profile you want to use on writing.
     * </p>
     * <p>
     * Results are undefined if profile names are not unique.
     * </p>
     * <p>
     * If the same configuration applies to multiple profiles, simply list all applicable profile names here. This parameter is an array of strings.
     * </p>
     * <p>
     * The empty string, which is the default value, specifies the default profile and will be used if no annotation for the specific profile being
     * used can be found, or if no profile is specified.
     * </p>
     *
     * @return The names of the profiles this configuration is for
     */
    String[] profiles() default "";
}
