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

import com.opencsv.bean.AbstractBeanField;

/**
 * Allows us to specify a class that will perform the translation from source to destination. For special needs, we can implement a class that takes
 * the source field from the CSV and translates it into a form of our choice. The source column name and position may also be specified.
 *
 * @author martin.nagy
 * @since 1.8.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvCustomBindByNamePosition {

    /**
     * The class that takes care of the conversion. Every custom converter must be descended from {@link com.opencsv.bean.AbstractBeanField} and
     * override the method {@link com.opencsv.bean.AbstractBeanField#convert(java.lang.String)}.
     *
     * @return The implementation that can convert to the type of this field.
     */
    Class<? extends AbstractBeanField> converter();

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
     * If the same hu.icellmobilsoft.coffee.configuration applies to multiple profiles, simply list all applicable profile names here. This parameter is an array of strings.
     * </p>
     * <p>
     * The empty string, which is the default value, specifies the default profile and will be used if no annotation for the specific profile being
     * used can be found, or if no profile is specified.
     * </p>
     *
     * @return The names of the profiles this hu.icellmobilsoft.coffee.configuration is for
     * @since 5.4
     */
    String[] profiles() default "";
}
