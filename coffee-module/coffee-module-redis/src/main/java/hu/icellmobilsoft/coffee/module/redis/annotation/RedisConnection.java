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
package hu.icellmobilsoft.coffee.module.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Qualifier for instantiating {@link hu.icellmobilsoft.coffee.module.redis.service.RedisService}
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
public @interface RedisConnection {

    /**
     * Config key of the desired redis connection. <br>
     * ie. if connection details are defined in the project-*.yml by the keys: {@code coffee.redis.auth.*=...} then configKey should be "auth"
     * 
     * @return
     */
    @Nonbinding
    String configKey();

    /**
     * Supports inline instantiation of the {@link RedisConnection} qualifier.
     *
     * @author mark.petrenyi
     *
     */
    public static final class Literal extends AnnotationLiteral<RedisConnection> implements RedisConnection {

        private static final long serialVersionUID = 1L;

        private final String configKey;

        public Literal(String configKey) {
            this.configKey = configKey;
        }

        @Nonbinding
        public String configKey() {
            return configKey;
        }

    }

}
