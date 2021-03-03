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
 * Stream consumer pointer and definition. Example:
 * 
 * <pre>
 * &#64;Dependent
 * &#64;RedisStreamConsumer(configKey = "sample", group = "streamGroup")
 * public class SampleConsumer extends AbstractStreamConsumer {
 * </pre>
 * 
 * @author imre.scheffer
 * @since 1.3.0
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface RedisStreamConsumer {

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
     * How many threads start to listening on stream group
     * 
     * @return consumer threads count
     */
    @Nonbinding
    int consumerThreadsCount() default 1;

    /**
     * How many times to try again if an exception occurs in the consumer process
     * 
     * @return retry process count
     */
    @Nonbinding
    int retryCount() default 1;

    /**
     * Default empty literal
     */
    AnnotationLiteral<RedisStreamConsumer> LITERAL = new Literal("", "", 0, 0);

    /**
     * AnnotationLiteral for RedisStreamConsumer annotation
     * 
     * @author imre.scheffer
     * @since 1.3.0
     */
    final class Literal extends AnnotationLiteral<RedisStreamConsumer> implements RedisStreamConsumer {

        private static final long serialVersionUID = 1L;

        final String configKey;
        final String group;
        final int consumerThreadsCount;
        final int retryCount;

        public Literal(String configKey, String group, int consumerThreadsCount, int retryCount) {
            super();
            this.configKey = configKey;
            this.group = group;
            this.consumerThreadsCount = consumerThreadsCount;
            this.retryCount = retryCount;
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

        @Nonbinding
        @Override
        public int consumerThreadsCount() {
            return consumerThreadsCount;
        }

        @Nonbinding
        @Override
        public int retryCount() {
            return retryCount;
        }
    }
}
