/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.config.watcher.provider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import hu.icellmobilsoft.coffee.module.config.watcher.ReloadingPropertiesConfigSource;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Provides an implementation of the {@link ConfigSourceProvider} interface that loads a single properties file as a configuration source. The
 * properties file is dynamically determined at runtime based on the `RELOADING_PROPERTIES_FILE` environment variable. If the environment variable is
 * not set, it defaults to `/app/config.properties`.
 *
 * @author martin.nagy
 * @since 2.13.0
 */
public class ReloadingPropertiesConfigSourceProvider implements ConfigSourceProvider {
    private static final Logger log = Logger.getLogger(ReloadingPropertiesConfigSourceProvider.class);

    /**
     * Default constructor
     */
    public ReloadingPropertiesConfigSourceProvider() {
        // empty constructor
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        Config config = ConfigProvider.getConfig();

        Path path = config.getOptionalValue("reloading.properties.file", Path.class).orElseGet(() -> Paths.get("/app/config.properties"));
        boolean enableMissingFile = config.getOptionalValue("reloading.properties.enableMissingFile", boolean.class).orElse(false);

        if (!Files.exists(path)) {
            if (enableMissingFile) {
                log.warn("Properties file not found: [{0}]", path);
                return List.of();
            }
            throw new IllegalArgumentException("Properties file not found: " + path);
        }

        try {
            return List.of(new ReloadingPropertiesConfigSource(path.toUri().toURL()));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read properties file: " + path, e);
        }
    }
}
