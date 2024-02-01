/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.cdi.configsource;

import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Main ConfigSource for configurable options
 * 
 * Example of settings in microprofile-config.properties:
 * 
 * <pre>
 * hu.icellmobilsoft.coffee.module.etcd.producer.CachedEtcdConfigSource.enabled = false #default true
 * </pre>
 * 
 * @author Imre Scheffer
 * @since 2.6.0
 */
public abstract class ConfigurableConfigSource implements ConfigSource {

    private final Config config;

    /**
     * Config source "enabled" key in configuration
     */
    public static final String CONFIG_ENABLED = ".enabled";

    /**
     * Default constructor, constructs a new object.
     */
    public ConfigurableConfigSource() {
        super();
        this.config = createConfig();
    }

    private Config createConfig() {
        return ConfigProviderResolver.instance().getBuilder().addDefaultSources().build();
    }

    /**
     * Called to return the properties in this config source when it is enabled
     * 
     * @return the map containing the properties in this config source
     */
    abstract protected Map<String, String> getPropertiesIfEnabled();

    /**
     * Return the properties, unless disabled return empty
     * 
     * @return the map containing the properties in this config source or empty if disabled
     */
    @Override
    public Map<String, String> getProperties() {
        return isEnabled() ? getPropertiesIfEnabled() : Collections.emptyMap();
    }

    /**
     * Is config source enabled in default sources.
     * 
     * @return by configuration, default true
     */
    protected boolean isEnabled() {
        Config cnf = getConfig();
        return cnf.getOptionalValue(getInstanceEnableKey(), Boolean.class)
                .orElse(cnf.getOptionalValue(getClassEnableKey(), Boolean.class).orElse(true));
    }

    private String getClassEnableKey() {
        return getClass().getName() + CONFIG_ENABLED;
    }

    private String getInstanceEnableKey() {
        return getName() + CONFIG_ENABLED;
    }

    /**
     * Get microprofile config with default sources
     * 
     * @return microprofile config instance
     */
    protected Config getConfig() {
        return this.config;
    }

}
