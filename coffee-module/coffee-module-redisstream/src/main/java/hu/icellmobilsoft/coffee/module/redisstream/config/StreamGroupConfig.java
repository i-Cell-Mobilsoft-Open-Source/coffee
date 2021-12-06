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
package hu.icellmobilsoft.coffee.module.redisstream.config;

import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.microprofile.config.Config;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Redis stream group configuration implementation. Key-value par has standard format like yaml file:
 *
 * @author imre.scheffer
 * @since 1.3.0
 */
public abstract class StreamGroupConfig {

    /**
     * Config delimiter
     */
    public static final String KEY_DELIMITER = ".";

    @Inject
    protected Config config;

    private String configKey;

    /**
     * Prefix for all configs
     */
    public static final String REDISSTREAM_PREFIX = "coffee.redisstream";

    public static final String STREAM_PREFIX = "stream";

    public static final String READ_PREFIX = "read";

    public static final String CONNECTION_PREFIX = "connection";

    public static final String KEY_PREFIX = "key";

    public static final String TIMEOUT_PREFIX = "timeoutmillis";

    public static final String POOL_PREFIX = "pool";

    /**
     * Prefix for stream.read.timeoutmillis config
     */
    public static final String TIMEOUT_KEY_PATH = STREAM_PREFIX + KEY_DELIMITER + READ_PREFIX + KEY_DELIMITER + TIMEOUT_PREFIX;

    /**
     * Prefix for stream.connection.key config
     */
    public static final String CONNECTION_KEY_PATH = STREAM_PREFIX + KEY_DELIMITER + CONNECTION_PREFIX + KEY_DELIMITER + KEY_PREFIX;

    /**
     * Getter for the field {@code configKey}.
     *
     * @return configKey
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Setter for the field {@code configKey}.
     *
     * @param configKey
     *            configKey
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * Defines the redis connection pool configuration key to be used.
     *
     * @return pool config.
     */
    public String getPool() {
        return config.getOptionalValue(joinKey(getPoolKeyPath()), String.class).orElse("default");
    }

    /**
     * See https://redis.io/commands/xreadgroup and https://redis.io/commands/xread BLOCK option
     *
     * @return timeout on stream read in millis. Waiting on stream until this time for new message or return null
     */
    public Long getReadTimeoutMillis() {
        return config.getOptionalValue(joinKey(TIMEOUT_KEY_PATH), Long.class).orElse(6000L);
    }

    /**
     * Defines the redis connection configuration key to be used.
     *
     * @return redis key.
     */
    public Optional<String> getConnectionKeyReference() {
        return config.getOptionalValue(joinKey(CONNECTION_KEY_PATH), String.class);
    }

    private String getPoolKeyPath() {
        return getStreamTypePath() + KEY_DELIMITER + POOL_PREFIX;
    }

    private String getStreamTypePath() {
        return config.getValue(joinKey(getStreamTypeKey()), String.class);

    }

    /**
     * Configuration parameter key for defining consumer- or producer -stream settings.
     *
     * @return streamType.
     */
    public abstract String getStreamTypeKey();

    protected String joinKey(String key) {
        return String.join(KEY_DELIMITER, REDISSTREAM_PREFIX, getConfigKey(), key);
    }
}
