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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * From-To megadásra alkalmazható annotació.
 *
 * @author imre.scheffer
 * @see Version
 * @since 1.0.0
 */
@Documented
@Qualifier
@Retention(RUNTIME)
@Target({ ANNOTATION_TYPE, TYPE })
public @interface Range {

    /**
     * Tól-, nemkötelező
     * 
     * @return default ""
     */
    @Nonbinding
    String from() default "";

    /**
     * -Ig, nemkötelező
     * 
     * @return default ""
     */
    @Nonbinding
    String to() default "";

    public final class RangeLiteral extends AnnotationLiteral<Range> implements Range {

        private static final long serialVersionUID = 1L;

        final String from;

        final String to;

        public RangeLiteral() {
            this.from = "";
            this.to = "";
        }

        public RangeLiteral(final String from, final String to) {
            this.from = from;
            this.to = to;
        }

        @Nonbinding
        @Override
        public String from() {
            return from;
        }

        @Nonbinding
        @Override
        public String to() {
            return to;
        }
    }
}
