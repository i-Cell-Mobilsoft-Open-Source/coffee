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
package hu.icellmobilsoft.coffee.module.etcd.producer;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.TechnicalException;

/**
 * ETCD Config source Szandekosan semmi CDI, MP Config init kozben a CDI meg nem elerheto mindig. Thread-safe singleton
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
@ThreadSafe
public class EtcdConfigSourceCache {

    /**
     * Cache write expiration time in minutes
     */
    public static final int CACHE_TIME_MINUTES = 30;

    private EtcdConfigSourceCache() {
    }

    private final LoadingCache<String, Optional<String>> cache = CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME_MINUTES, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Optional<String>>() {
                @Override
                public Optional<String> load(String key) throws Exception {
                    return DefaultEtcdConfigSource.readEtcdValue(key);
                }
            });

    /**
     * Returns value of given property.
     * 
     * @param propertyName
     *            property to return value of
     * @return {@link Optional} value of property
     * @throws BaseException
     *             if cannot return property
     */
    public Optional<String> getValue(String propertyName) throws BaseException {
        try {
            return cache.get(propertyName);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof BaseException) {
                throw (BaseException) e.getCause();
            } else {
                throw new TechnicalException(CoffeeFaultType.SERVICE_CALL_FAILED,
                        MessageFormat.format("Error in getting key [{0}] from ETCD", propertyName), e);
            }
        }
    }

    private static class LazyHolder {
        public static final EtcdConfigSourceCache INSTANCE = new EtcdConfigSourceCache();
    }

    /**
     * Returns an instance of this class
     * 
     * @return an instance of this class
     */
    public static EtcdConfigSourceCache instance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Clear cache values
     */
    public void clear() {
        cache.invalidateAll();
    }
}
