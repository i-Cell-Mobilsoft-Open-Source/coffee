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

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.string.StringHelper;

/**
 * Alkamazás szintű konfiguráció gyűjtő, mely időnkent ({@value #CACHE_TIME_MINUTES} percenként) űjra kérdezi a beállításokat az konfiguráció
 * forrásokból, vagy ad lehetőséget arra, hogy rögtön újraolvassa őket
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@ApplicationScoped
public class ApplicationConfiguration {

    /** Constant <code>CACHE_TIME_MINUTES=30</code> */
    public static final int CACHE_TIME_MINUTES = 30;

    /** Constant <code>ERROR_IN_GETTING_KEY=Error in getting configuration for key [{0}]: [{1}]</code> */
    public static final String ERROR_IN_GETTING_KEY = "Error in getting configuration for key [{0}]: [{1}]";

    /** Constant <code>MSG_ETCD_VALUE_NOT_FOUND=Etcd value not found, key: [{0}] valueClass: [{1}]!</code> */
    public static final String MSG_ETCD_VALUE_NOT_FOUND = "Etcd value not found, key: [{0}] valueClass: [{1}]!";

    /** Constant <code>MSG_ETCD_VALUE_FOR_KEY=Key [{0}] value [{1}]</code> */
    public static final String MSG_ETCD_VALUE_FOR_KEY = "Key [{0}] value [{1}]";

    @Inject
    private Logger log;

    @Inject
    private ConfigurationHelper configurationHelper;

    /**
     * Default constructor, constructs a new object.
     */
    public ApplicationConfiguration() {
        super();
    }

    private LoadingCache<CompositeCacheLoaderKey, Optional<?>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(CACHE_TIME_MINUTES, TimeUnit.MINUTES).build(new CacheLoader<CompositeCacheLoaderKey, Optional<?>>() {
                @SuppressWarnings("unchecked")
                @Override
                public Optional<?> load(CompositeCacheLoaderKey compositeCacheLoaderKey) throws Exception {
                    Optional<?> optValue = configurationHelper.getConfigOptionalValue(compositeCacheLoaderKey.getKey(),
                            compositeCacheLoaderKey.getValueClass());
                    // if the value is missing or ETCD cluster failure happened
                    if (optValue.isEmpty()) {
                        String msg = MessageFormat.format(MSG_ETCD_VALUE_NOT_FOUND, compositeCacheLoaderKey.getKey(),
                                compositeCacheLoaderKey.getValueClass());
                        throw new BONotFoundException(msg);
                    }
                    return optValue;
                }
            });

    /**
     * Getting string value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public String getString(String key) {
        return getValue(key, String.class);
    }

    /**
     * Getting integer value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Integer getInteger(String key) {
        return getValue(key, Integer.class);
    }

    /**
     * Getting boolean value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Boolean getBoolean(String key) {
        return getValue(key, Boolean.class);
    }

    /**
     * Getting long value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Long getLong(String key) {
        return getValue(key, Long.class);
    }

    /**
     * Getting string value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Float getFloat(String key) {
        return getValue(key, Float.class);
    }

    /**
     * Getting double value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Double getDouble(String key) {
        return getValue(key, Double.class);
    }

    /**
     * Getting optional string value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Optional<String> getOptionalString(String key) {
        return getOptionalValue(key, String.class);
    }

    /**
     * Getting optional integer value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Optional<Integer> getOptionalInteger(String key) {
        return getOptionalValue(key, Integer.class);
    }

    /**
     * Getting optional boolean value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Optional<Boolean> getOptionalBoolean(String key) {
        return getOptionalValue(key, Boolean.class);
    }

    /**
     * Getting optional long value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Optional<Long> getOptionalLong(String key) {
        return getOptionalValue(key, Long.class);
    }

    /**
     * Getting optional string value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Optional<Float> getOptionalFloat(String key) {
        return getOptionalValue(key, Float.class);
    }

    /**
     * Getting optional double value by key from application cache
     *
     * @param key
     *            config key
     * @return value or null
     */
    public Optional<Double> getOptionalDouble(String key) {
        return getOptionalValue(key, Double.class);
    }

    /**
     * Getting value by key from application cache
     *
     * @param <T>
     *            return object type
     * @param key
     *            configuration key
     * @param clazz
     *            return class type
     * @return value or null
     */
    public <T> T getValue(String key, Class<T> clazz) {
        Optional<T> value = getOptionalValue(key, clazz);
        return value.orElse(null);
    }

    /**
     * Getting optional value by key from application cache
     *
     * @param <T>
     *            return object type
     * @param key
     *            configuration key
     * @param clazz
     *            return class type
     * @return {@code Optional<T>} value
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOptionalValue(String key, Class<T> clazz) {
        if (StringUtils.isBlank(key)) {
            return Optional.empty();
        }
        try {
            Optional<T> value = (Optional<T>) cache.get(new CompositeCacheLoaderKey(key, clazz));
            if (log.isTraceEnabled()) {
                log.trace(MSG_ETCD_VALUE_FOR_KEY, key, StringHelper.maskPropertyValue(key, value));
            }
            return value;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof BONotFoundException) {
                log.warn(e.getCause().getMessage());
            } else {
                log.error(MessageFormat.format(ERROR_IN_GETTING_KEY, key, e.getLocalizedMessage()), e);
            }
        }
        return Optional.empty();
    }

    /**
     * Clear cache values
     */
    public void clear() {
        cache.invalidateAll();
    }
}
