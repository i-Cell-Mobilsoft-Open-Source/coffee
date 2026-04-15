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
package hu.icellmobilsoft.coffee.module.config.watcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.microprofile.config.spi.ConfigSource;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * A configuration source implementation that loads properties from a specified URL and supports automatic reloading when the underlying properties
 * file changes. This class is designed for use cases where configuration needs to be dynamically updated during runtime without application restart.
 *
 * @author martin.nagy
 * @since 2.10.1
 */
public class ReloadingPropertiesConfigSource implements ConfigSource, AutoCloseable {
    private static final String NAME_PREFIX = "ReloadingPropertiesConfigSource[source=";
    private static final Logger log = Logger.getLogger(ReloadingPropertiesConfigSource.class);

    private final int ordinal;
    private final String name;
    private final URL url;
    private final ConfigFileWatcher configFileWatcher;
    private Map<String, String> properties;

    /**
     * Constructs a {@code ReloadingPropertiesConfigSource} with a specified URL as the source of the configuration properties. This constructor uses
     * a default ordinal value of 120 and initializes the necessary resources for monitoring changes in the underlying properties file, allowing
     * automatic reloading of the configuration.
     *
     * @param url
     *            the URL pointing to the properties file used as the configuration source
     * @throws IOException
     *             if an error occurs while reading the properties file from the specified URL
     */
    public ReloadingPropertiesConfigSource(URL url) throws IOException {
        this(url, 120);
    }

    /**
     * Constructs a {@code ReloadingPropertiesConfigSource} with a specified URL as the source of the configuration properties and a given ordinal
     * value that determines the source's priority. Initializes the necessary resources for monitoring changes in the underlying properties file,
     * allowing automatic reloading of the configuration.
     *
     * @param url
     *            the URL pointing to the properties file used as the configuration source
     * @param ordinal
     *            the ordinal value used to determine the priority of this configuration source
     * @throws IOException
     *             if an error occurs while reading the properties file from the specified URL
     */
    public ReloadingPropertiesConfigSource(URL url, int ordinal) throws IOException {
        this.name = NAME_PREFIX + url.toString() + "]";
        this.ordinal = ordinal;
        this.url = url;
        properties = read(url);
        try {
            configFileWatcher = new ConfigFileWatcher(Path.of(url.toURI()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid config file URI: " + url, e);
        }
        configFileWatcher.addListener(path -> reloadConfig());
    }

    /**
     * Reloads the configuration properties from the specified URL
     */
    public void reloadConfig() {
        try {
            properties = read(url);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read from properties file: " + url, e);
        }
        log.info("Reloaded config from: [{0}]", url);
    }

    private static Map<String, String> read(URL url) throws IOException {
        Properties x = new Properties();
        try (InputStream inputStream = url.openStream(); BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            x.load(bufferedInputStream);
        }
        Map<String, String> map = new HashMap<>(x.size());
        x.forEach((k, v) -> map.put(String.valueOf(k), String.valueOf(v)));
        return map;
    }

    @Override
    public void close() {
        configFileWatcher.close();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
