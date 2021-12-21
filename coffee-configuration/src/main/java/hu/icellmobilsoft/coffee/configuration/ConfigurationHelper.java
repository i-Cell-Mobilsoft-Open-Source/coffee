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
package hu.icellmobilsoft.coffee.configuration;

import java.util.Optional;

import javax.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Configuration value helper
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class ConfigurationHelper {

    /**
     * Get String type, @RequestScope cached value from config sources
     *
     * @param key
     *            config key
     * @return String value or null
     */
    public String getString(String key) {
        return getConfigValue(key, String.class);
    }

    /**
     * Get Integer type, @RequestScope cached value from config sources
     *
     * @param key
     *            config key
     * @return Integer value or null
     */
    public Integer getInteger(String key) {
        return getConfigValue(key, Integer.class);
    }

    /**
     * Get Boolean type, @RequestScope cached value from config sources
     *
     * @param key
     *            config key
     * @return Boolean value or null
     */
    public Boolean getBoolean(String key) {
        return getConfigValue(key, Boolean.class);
    }

    /**
     * Get Long type, @RequestScope cached value from config sources
     *
     * @param key
     *            config key
     * @return Long value or null
     */
    public Long getLong(String key) {
        return getConfigValue(key, Long.class);
    }

    /**
     * Get Float type, @RequestScope cached value from config sources
     *
     * @param key
     *            config key
     * @return Float value or null
     */
    public Float getFloat(String key) {
        return getConfigValue(key, Float.class);
    }

    /**
     * Get Double type, @RequestScope cached value from config sources
     *
     * @param key
     *            config key
     * @return Double value or null
     */
    public Double getDouble(String key) {
        return getConfigValue(key, Double.class);
    }

    /**
     * Get String type, @RequestScope cached optional value from config sources
     *
     * @param key
     *            config key
     * @return Optional String value or null
     */
    public Optional<String> getOptionalString(String key) {
        return getConfigOptionalValue(key, String.class);
    }

    /**
     * Get Integer type, @RequestScope cached optional value from config sources
     *
     * @param key
     *            config key
     * @return Optional Integer value or null
     */
    public Optional<Integer> getOptionalInteger(String key) {
        return getConfigOptionalValue(key, Integer.class);
    }

    /**
     * Get Boolean type, @RequestScope cached optional value from config sources
     *
     * @param key
     *            config key
     * @return Optional Boolean value or null
     */
    public Optional<Boolean> getOptionalBoolean(String key) {
        return getConfigOptionalValue(key, Boolean.class);
    }

    /**
     * Get Long type, @RequestScope cached optional value from config sources
     *
     * @param key
     *            config key
     * @return Optional Long value or null
     */
    public Optional<Long> getOptionalLong(String key) {
        return getConfigOptionalValue(key, Long.class);
    }

    /**
     * Get Float type, @RequestScope cached optional value from config sources
     *
     * @param key
     *            config key
     * @return Optional Float value or null
     */
    public Optional<Float> getOptionalFloat(String key) {
        return getConfigOptionalValue(key, Float.class);
    }

    /**
     * Get Double type, @RequestScope cached optional value from config sources
     *
     * @param key
     *            config key
     * @return Optional Double value or null
     */
    public Optional<Double> getOptionalDouble(String key) {
        return getConfigOptionalValue(key, Double.class);
    }

    /**
     * Getting value by key from config sources
     *
     * @param <T>
     *            return object type
     * @param key
     *            config key
     * @param clazz
     *            return class type
     * @return value or null
     */
    public <T> T getConfigValue(String key, Class<T> clazz) {
        Optional<T> microprofileConfigValue = getConfigOptionalValue(key, clazz);
        return microprofileConfigValue.orElse(null);
    }

    /**
     * Getting optional value by key from config sources
     * 
     * @param <T>
     *            return object type
     * @param key
     *            key
     * @param clazz
     *            return class type
     * @return {@code Optional<T>} value
     */
    public <T> Optional<T> getConfigOptionalValue(String key, Class<T> clazz) {
        if (StringUtils.isBlank(key)) {
            return Optional.empty();
        }
        return ConfigProvider.getConfig().getOptionalValue(key, clazz);
    }

}
