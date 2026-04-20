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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

import hu.icellmobilsoft.coffee.module.config.watcher.ReloadingPropertiesConfigSource;

/**
 * A configuration source provider that dynamically loads and provides multiple {@link ConfigSource} instances from property files located in a
 * specified directory. The directory is determined by the `RELOADING_PROPERTIES_FILE_DIR` environment variable. If the environment variable is not
 * set, the default directory is `/app/config`.
 * <p>
 * The implementation scans the specified directory for `.properties` files and creates a {@link ReloadingPropertiesConfigSource} for each file. These
 * configuration sources support automatic reloading, enabling the application to dynamically update configuration during runtime when property files
 * are modified.
 *
 * @author martin.nagy
 * @since 2.10.1
 */
public class DirectoryReloadingPropertiesConfigSourceProvider implements ConfigSourceProvider {

    /**
     * Default constructor
     */
    public DirectoryReloadingPropertiesConfigSourceProvider() {
        // empty constructor
    }

    @Override
    public Iterable<ConfigSource> getConfigSources(ClassLoader classLoader) {
        Path dir = Paths.get(Optional.ofNullable(System.getenv("RELOADING_PROPERTIES_FILE_DIR")).orElse("/app/config"));

        try (Stream<Path> fileStream = Files.list(dir)) {
            List<Path> files = fileStream.filter(path -> path.toString().endsWith(".properties")).toList();

            Collection<ConfigSource> configSources = new ArrayList<>(files.size());
            for (Path file : files) {
                configSources.add(new ReloadingPropertiesConfigSource(file.toUri().toURL()));
            }
            return configSources;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read properties files from directory: " + dir, e);
        }
    }
}
