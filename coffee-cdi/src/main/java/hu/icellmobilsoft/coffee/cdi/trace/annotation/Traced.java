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
package hu.icellmobilsoft.coffee.cdi.trace.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;

/**
 * InterceptorBinding to trace methods
 * <p>
 * Based on org.eclipse.microprofile.opentracing.Traced
 * 
 * @author czenczl
 * @since 1.3.0
 */
@InterceptorBinding
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
public @interface Traced {

    /**
     * Trace span attribute component, represents io.opentracing.tag.Tags.COMPONENT or span attribute <br>
     * low-cardinality identifier of the module, library, or package that is instrumented.
     * 
     * @return component
     */
    @Nonbinding
    String component() default "";

    /**
     * Trace span attribute kind, represents io.opentracing.tag.Tags.SPAN_KIND or span attribute <br>
     * hints at the relationship between spans, e.g. CLIENT/SERVER.
     * 
     * @return kind
     */
    @Nonbinding
    String kind() default SpanAttribute.INTERNAL;

    /**
     * Trace span attribute dbType, represents io.opentracing.tag.Tags.DB_TYPE or span attribute <br>
     * 
     * DB_TYPE indicates the type of Database, e.g. "redis"
     * 
     * @return dbType
     */
    @Nonbinding
    String dbType() default "";

    /**
     * Default empty literal
     */
    AnnotationLiteral<Traced> LITERAL = new Literal("", "", "");

    /**
     * AnnotationLiteral for Traced annotation
     * 
     * @author czenczl
     * @since 2.1.0
     */
    final class Literal extends AnnotationLiteral<Traced> implements Traced {

        private static final long serialVersionUID = 1L;

        /**
         * Trace component
         */
        final String component;
        /**
         * Trace kind
         */
        final String kind;
        /**
         * Trace dbType
         */
        final String dbType;

        /**
         * Instantiates the literal with component, kind and dbType
         * 
         * @param component
         *            trace component, e.g. jetcd, jedis, database
         * @param kind
         *            trace kind, e.g. CLIENT, SERVER
         * @param dbType
         *            trace dbType, e.g. relational, etcd, redis
         */
        public Literal(String component, String kind, String dbType) {
            this.component = component;
            this.kind = kind;
            this.dbType = dbType;
        }

        @Nonbinding
        @Override
        public String component() {
            return component;
        }

        @Nonbinding
        @Override
        public String kind() {
            return kind;
        }

        @Nonbinding
        @Override
        public String dbType() {
            return dbType;
        }
    }

}
