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
package hu.icellmobilsoft.coffee.cdi.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Any;
import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Verzió szerinti annotáció jelölés. Minta használat:
 * <table border=1px>
 * <tr>
 * <th>Példa</th>
 * <th>Érvényes</th>
 * <th>Nem érvényes</th>
 * </tr>
 * <tr>
 * <td>@Version(include = @Range(from = "1.0"))</td>
 * <td>1.0+, 1.0.0 ...</td>
 * <td>0.9 ...</td>
 * </tr>
 * <tr>
 * <td>@Version(include = @Range(from = "1.0", to = "1.6.1"))</td>
 * <td>1.0-1.6.1, 1.6.1.0 ...</td>
 * <td>1.6.1.1, 1.6.2, 1.7 ...</td>
 * </tr>
 * <tr>
 * <td>@Version(include = {@Range(from = "1.0", to = "1.6"), @Range(from = "2.0")})</td>
 * <td>1.0-1.6, 1.6.0, 2.0+ ...</td>
 * <td>1.6.1, 1.9 ...</td>
 * </tr>
 * </table>
 *
 * @author imre.scheffer
 * @see Range
 * @since 1.0.0
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target(TYPE)
public @interface Version {

    /**
     * Megadott verziókra (beleérve) aktiválódik a feltétel.
     * 
     * @return
     */
    @Nonbinding
    Range[] include() default {};

    /**
     * Supports inline instantiation of the {@link Any} qualifier.
     *
     * @author imre.scheffer
     *
     */
    public static final class Literal extends AnnotationLiteral<Version> implements Version {

        public static final Literal INSTANCE = new Literal();

        private static final long serialVersionUID = 1L;

        @Nonbinding
        @Override
        public Range[] include() {
            return new Range[] {};
        }
    }

    /**
     * Supports inline instantiation of the {@link Any} qualifier.
     *
     * @author imre.scheffer
     *
     */
    public static final class VersionLiteral extends AnnotationLiteral<Version> implements Version {

        public static final Literal INSTANCE = new Literal();

        private static final long serialVersionUID = 1L;

        private final Range[] include;

        /**
         * Create annotation with paramaters
         * 
         * @param include
         */
        public VersionLiteral(final Range[] include) {
            this.include = include;
        }

        @Nonbinding
        @Override
        public Range[] include() {
            return include;
        }
    }
}
