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

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;

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
     * Trace tag component, represents io.opentracing.tag.Tags.COMPONENT <br>
     * low-cardinality identifier of the module, library, or package that is instrumented.
     * 
     * @return component
     */
    @Nonbinding
    String component() default "";

    /**
     * Trace tag kind, represents io.opentracing.tag.Tags.SPAN_KIND <br>
     * hints at the relationship between spans, e.g. client/server.
     * 
     * @return kind
     */
    @Nonbinding
    String kind() default "";

    /**
     * Trace tag dbType, represents io.opentracing.tag.Tags.DB_TYPE <br>
     * 
     * DB_TYPE indicates the type of Database, e.g. "redis"
     * 
     * @return dbType
     */
    @Nonbinding
    String dbType() default "";

}
