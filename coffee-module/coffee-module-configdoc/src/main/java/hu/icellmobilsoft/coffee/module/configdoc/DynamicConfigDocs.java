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
package hu.icellmobilsoft.coffee.module.configdoc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.StringUtils;

/**
 * Documentation file will be generated based on the annotated configuration key holder classes and fields to.
 *
 * @author mark.petrenyi
 * @since 1.10.0
 */
@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.CLASS)
public @interface DynamicConfigDocs {

    /**
     * (Optional) template class used for documentation
     *
     * @return template class, {@link NoTemplate} as default.
     */
    Class<?> template() default NoTemplate.class;

    /**
     * The variables used to replace placeholders in the template
     *
     * @return the variables
     */
    String[] templateVariables() default {};

    /**
     * Configuration title, can contain placeholders.
     *
     * @return the title
     */
    String title() default StringUtils.EMPTY;

    /**
     * Configuration description, can contain placeholders.
     *
     * @return the description
     */
    String description() default StringUtils.EMPTY;

    /**
     * Default class for {@link #template()}, indicating no value set
     */
    final class NoTemplate {

    }

}
