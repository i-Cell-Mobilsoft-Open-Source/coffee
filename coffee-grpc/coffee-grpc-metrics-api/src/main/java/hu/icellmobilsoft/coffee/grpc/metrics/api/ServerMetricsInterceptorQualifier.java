/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.grpc.metrics.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

/**
 * gRPC server finds the annotated interceptor for metrics collection
 * 
 * @author czenczl
 * @since 2.1.0
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface ServerMetricsInterceptorQualifier {

    /**
     * Literal for injection
     * 
     * @author czenczl
     * @since 2.1.0
     */
    public static final class Literal extends AnnotationLiteral<ServerMetricsInterceptorQualifier> implements ServerMetricsInterceptorQualifier {

        private static final long serialVersionUID = 1L;

        /**
         * Default constructor, constructs a new object.
         */
        public Literal() {
            super();
        }

    }
}
