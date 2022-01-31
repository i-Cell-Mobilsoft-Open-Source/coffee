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
package hu.icellmobilsoft.coffee.module.redisstream.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;

/**
 * Stream producer pointer and definition. Example:
 * 
 * <pre>
 * &#64;Inject
 * &#64;RedisStreamProducer(configKey = "sample", group = "streamGroup")
 * private RedisStreamHandler redisStreamHandler;
 * ...
 * redisStreamHandler.publish(string);
 * </pre>
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface RedisStreamProducer {
    /**
     * Config key of the desired redis connection. <br>
     * ie. if connection details are defined in the project-*.yml by the keys: {@code coffee.redis.auth.*=...} then configKey should be "auth"
     * 
     * @see RedisConnection#configKey()
     * 
     * @return config key
     */
    @Nonbinding
    String configKey();

    /**
     * Stream consumer group name. This is name for group and also key for konfigurable values for {@link StreamGroupConfig}
     * 
     * @return group name for redis stream in redis
     */
    @Nonbinding
    String group();

    /**
     * Default empty literal
     */
    AnnotationLiteral<RedisStreamProducer> LITERAL = new Literal("", "");

    /**
     * AnnotationLiteral for RedisStreamProducer annotation
     * 
     * @author imre.scheffer
     * @since 1.3.0
     */
    final class Literal extends AnnotationLiteral<RedisStreamProducer> implements RedisStreamProducer {

        private static final long serialVersionUID = 1L;

        /**
         * config key
         */
        final String configKey;
        /**
         * redis stream group
         */
        final String group;

        /**
         * Instantiates the literal with configKey and redis stream group
         * 
         * @param configKey
         *            config key
         * @param group
         *            redis stream group
         */
        public Literal(String configKey, String group) {
            this.configKey = configKey;
            this.group = group;
        }

        @Nonbinding
        @Override
        public String configKey() {
            return configKey;
        }

        @Nonbinding
        @Override
        public String group() {
            return group;
        }
    }
}
