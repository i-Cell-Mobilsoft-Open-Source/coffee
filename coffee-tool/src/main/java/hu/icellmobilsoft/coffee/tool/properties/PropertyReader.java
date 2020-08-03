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
package hu.icellmobilsoft.coffee.tool.properties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * <p>PropertyReader class.</p>
 *
 * @since 1.0.0
 */
@Vetoed
public class PropertyReader {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(PropertyReader.class);

    private static PropertyReader instance = null;

    private static final String DELIMITER = ".";

    private static final String CONFIG_FILE_KEY = "application.configurationFile";

    private LoadingCache<String, Properties> cache = CacheBuilder.newBuilder().weakKeys().expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Properties>() {
                @Override
                public Properties load(String key) throws Exception {
                    return getSystemProperties(key);
                }
            });

    /**
     * <p>Constructor for PropertyReader.</p>
     */
    protected PropertyReader() {
    }

    /**
     * <p>Getter for the field <code>instance</code>.</p>
     */
    public static PropertyReader getInstance() {
        if (instance == null) {
            instance = new PropertyReader();
        }
        return instance;
    }

    /**
     * <p>getSystemProperty.</p>
     */
    public String getSystemProperty(Class<?> c, String keyPart) {
        return getSystemProperty(c, keyPart, null);
    }

    /**
     * <p>getSystemProperty.</p>
     */
    public String getSystemProperty(Class<?> c, String keyPart, String defaultValue) {
        String key = c == null ? keyPart : c.getName() + DELIMITER + keyPart;
        LOGGER.debug("get system property: [{0}]", key);
        String value = null;

        try {
            Properties prop = cache.get(CONFIG_FILE_KEY);
            value = prop.getProperty(key);

            if (value == null && defaultValue != null) {
                LOGGER.warn("property not found, returning [{0}]", defaultValue);
                return defaultValue;
            }

            LOGGER.debug("system property value: [{0}]", value);
            return value;
        } catch (ExecutionException e) {
            LOGGER.error(e.getLocalizedMessage());
            return defaultValue;
        }
    }

    private Properties getSystemProperties(String key) {
        Properties prop = null;
        LOGGER.debug("system config property file is not in cache");

        InputStream fin = null;
        String configFileSystemProperty = System.getProperty(key);
        if (StringUtils.isBlank(configFileSystemProperty)) {
            LOGGER.error("Configuration file missing! Please set the [{0}] system property!!!!", CONFIG_FILE_KEY);
        } else {
            try {
                File propertyfile = new File(configFileSystemProperty);
                if (propertyfile.exists()) {
                    LOGGER.debug("Property file found, reading...");
                    fin = new FileInputStream(propertyfile);
                    prop = new Properties();
                    prop.load(fin);
                } else {
                    URL url = new URL(configFileSystemProperty);
                    URLConnection connection = url.openConnection();
                    fin = new BufferedInputStream(connection.getInputStream());
                    prop = new Properties();
                    prop.load(fin);
                }
            } catch (IOException e) {
                LOGGER.warn("Can't read property file", e);
            } finally {
                try {
                    if (fin != null) {
                        fin.close();
                    }
                } catch (IOException e) {
                    LOGGER.error("Error in close inputsream", e);
                }
            }
        }
        return prop;
    }
}
