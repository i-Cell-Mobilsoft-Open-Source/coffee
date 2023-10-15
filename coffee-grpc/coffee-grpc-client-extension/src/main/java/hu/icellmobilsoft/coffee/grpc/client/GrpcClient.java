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
package hu.icellmobilsoft.coffee.grpc.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * Qualifier for implementation of {@link io.grpc.stub.AbstractBlockingStub}
 *
 * @author czenczl
 * @since 2.1.0
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface GrpcClient {

    /**
     * Config key of the desired gRPC client connection. <br>
     * 
     * @return config key
     */
    @Nonbinding
    String configKey();

    /**
     * Supports inline instantiation of the {@link GrpcClient} qualifier.
     *
     * @author czenczl
     *
     */
    final class Literal extends AnnotationLiteral<GrpcClient> implements GrpcClient {

        private static final long serialVersionUID = 1L;

        /**
         * Redis configuration key
         */
        private final String configKey;

        /**
         * Instantiates the literal with configKey
         *
         * @param configKey
         *            config key
         */
        public Literal(String configKey) {
            this.configKey = configKey;
        }

        @Override
        @Nonbinding
        public String configKey() {
            return configKey;
        }

    }
}
