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
package hu.icellmobilsoft.coffee.tool.utils.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

/**
 * ConfigUtil
 * 
 * @author czenczl
 * @author speter555
 * @since 1.2.0
 */
public class ConfigUtil {

    /**
     * Get the default configuration sources
     * <ol>
     * <li>System properties</li>
     * <li>Environment properties</li>
     * <li>/META-INF/microprofile-config.properties</li>
     * </ol>
     * 
     * @return default microprofile config without other configsources
     */
    // Important - The {@link ConfigBuilder#build()} method is implemented by the SmallRyeConfigBuilder class within the SmallRye Config implementation.
    // When using this, a new instance of the SmallRyeConfig class is created, and during instantiation, the generateDottedProperties method consumes a significant amount of CPU resources.
    // Therefore, it is necessary to handle default config storage in such a way that it does not pose a problem both during CDI usage and outside of CDI.
    //
    // We fix this on the classloader where we manage our code, ensuring that if the application server initializes
    // with a different classloader, it always finds our own 'microprofile-config.properties' file.
    private Config config;

    private ConfigUtil() {
        config = ConfigProviderResolver.instance().getBuilder().forClassLoader(Thread.currentThread().getContextClassLoader()).addDefaultSources().build();
    }

    /**
     * Get default configs
     * 
     * @return {@link Config} instance
     */
    public Config defaultConfig() {
        return config;
    }

    /**
     * Get ConfigUtil instance
     * 
     * @return {@link ConfigUtil} instance
     */
    public static ConfigUtil getInstance() {
        return ConfigHolder.INSTANCE;
    }

    private static class ConfigHolder {
        public static final ConfigUtil INSTANCE = new ConfigUtil();

    }

}
