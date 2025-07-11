/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.docgen.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Documentation file will be generated based on the annotated configuration key holder classes and fields.
 * 
 * @author martin.nagy
 * @since 1.9.0
 */
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface ConfigDoc {

    /**
     * (Optional) {@code true} if the field should be excluded from the documentation. Default {@code false}
     * 
     * @return {@code true} if the field should be excluded from the documentation.
     */
    boolean exclude() default false;

    /**
     * (Optional) The description of the configuration key. Defaults to the javadoc.
     * 
     * @return the description of the configuration key
     */
    String description() default "";

    /**
     * (Optional) The default value for the configuration key
     * 
     * @return the default value for the configuration key
     */
    String defaultValue() default "";

    /**
     * (Optional) the version since the configuration key available
     * 
     * @return the version since the configuration key available
     * @since 1.10.0
     */
    String since() default "";

    /**
     * (Optional) {@code true} if the field is a startup parameter. Default {@code false}
     *
     * @return {@code true} if the field is a startup parameter
     * @since 2.7.0
     */
    boolean isStartupParam() default false;

    /**
     * (Optional) {@code true} if the field can be overridden at runtime. Default {@code false}
     *
     * @return {@code true} if the field can be overridden at runtime
     * @since 2.7.0
     */
    boolean isRuntimeOverridable() default false;

    /**
     * (Optional) the default title of the table can be overridden
     *
     * @return the title
     * @since 2.7.0
     */
    String title() default "";

    /**
     * (Optional) The level of the generated table in the adoc file. If given it needs to be in the range of [0,5] otherwise we use fallback to 3.
     *
     * @return the title level value
     * @since 2.7.0
     */
    int titleHeadingLevel() default 3;

}
