/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redispubsub;

import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.reactive.messaging.spi.ConnectorFactory;

/**
 * Extracts the common configuration for the {@code coffee-redis-pubsub} connector. <br>
 * PM: It would be good to generate using ConnectorAttribute annotation, but it's still experimental under WF, so for now, copy-paste and fix the javadoc in the generated code.
 * 
 * @author mark.petrenyi
 * @since 1.13.0
 */
public class PubSubConnectorCommonConfiguration {
    /**
     * The connector configuration
     */
    protected final Config config;

    /**
     * Creates a new PubSubConnectorCommonConfiguration.
     *
     * @param config
     *            the connector configuration
     */
    public PubSubConnectorCommonConfiguration(Config config) {
        this.config = config;
    }

    /**
     * Returns config.
     *
     * @return the connector configuration
     */
    public Config config() {
        return this.config;
    }

    /**
     * Retrieves the value stored for the given alias.
     *
     * @param <T>
     *            the targeted type
     * @param alias
     *            the attribute alias, must not be {@code null} or blank
     * @param type
     *            the targeted type
     * @return the configuration value for the given alias, empty if not set
     */
    protected <T> Optional<T> getFromAlias(String alias, Class<T> type) {
        return ConfigProvider.getConfig().getOptionalValue(alias, type);
    }

    /**
     * Retrieves the value stored for the given alias. Returns the default value if not present.
     *
     * @param <T>
     *            the targeted type
     * @param alias
     *            the attribute alias, must not be {@code null} or blank
     * @param type
     *            the targeted type
     * @param defaultValue
     *            the default value
     * @return the configuration value for the given alias, empty if not set
     */
    protected <T> T getFromAliasWithDefaultValue(String alias, Class<T> type, T defaultValue) {
        return getFromAlias(alias, type).orElse(defaultValue);
    }

    /**
     * Gets channel.
     *
     * @return the channel name
     */
    public String getChannel() {
        return config.getValue(ConnectorFactory.CHANNEL_NAME_ATTRIBUTE, String.class);
    }

    /**
     * Gets the connection-key value from the configuration. Attribute Name: connection-key Description: Coffee redis connection key coffee.redis.*
     * Mandatory: yes
     *
     * @return the connection-key
     */
    public String getConnectionKey() {
        return config.getOptionalValue("connection-key", String.class).orElseThrow(() -> new IllegalArgumentException(
                "The attribute `connection-key` on connector 'coffee-redis-pubsub' (channel: " + getChannel() + ") must be set"));
    }

    /**
     * Gets the pool-key value from the configuration. Attribute Name: pool-key Description: Coffee redis pool key coffee.redis.*.pool.* Default
     * Value: default
     *
     * @return the pool-key
     */
    public String getPoolKey() {
        return config.getOptionalValue("pool-key", String.class).orElse("default");
    }

    /**
     * Gets the pub-sub-channel value from the configuration. Attribute Name: pub-sub-channel Description: Optional name of pub/sub channel, defaults
     * to microprofile stream channel, workaround to have both publisher and subscriber for the same channel within a service.
     *
     * @return the pub-sub-channel
     */
    public Optional<String> getPubSubChannel() {
        return config.getOptionalValue("pub-sub-channel", String.class);
    }

    /**
     * Validate.
     */
    public void validate() {
        getConnectionKey();
    }
}
