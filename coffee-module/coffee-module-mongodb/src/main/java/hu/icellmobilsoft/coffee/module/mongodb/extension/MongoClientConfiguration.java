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
package hu.icellmobilsoft.coffee.module.mongodb.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * Annotation required for using `hu.icellmobilsoft.coffee.module.mongodb.extension.MongoDbClient` and initialization of `MongoService` classes.
 * 
 * @author czenczl
 * @since 1.1.0
 *
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface MongoClientConfiguration {

    /**
     * Config key of the desired Mongo DB connection. <br>
     * ie. if connection details are defined in the project-*.yml by the keys: {@code coffee.mongodb.exampleDB.*=...} then configKey should be
     * "exampleDB"
     *
     * @return config key
     */
    @Nonbinding
    String configKey();

    /**
     * Qualifier literal instance
     */
    final class Literal extends AnnotationLiteral<MongoClientConfiguration> implements MongoClientConfiguration {

        private static final long serialVersionUID = 1L;

        /**
         * config key
         */
        final String configKey;

        /**
         * Instantiates the literal with configKey
         * 
         * @param configKey
         *            config key
         */
        public Literal(String configKey) {
            this.configKey = configKey;
        }

        @Nonbinding
        @Override
        public String configKey() {
            return configKey;
        }

    }
}
