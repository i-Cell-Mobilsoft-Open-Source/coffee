/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.cache;

import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;

/**
 * General-purpose {@link LoadingCache} support class.
 *
 * @param <KEY>
 *            the type of the cache key used for lookup
 * @param <VALUE>
 *            the type of the elements stored in the cache
 *
 * @author tamas.cserhati
 * @author martin.nagy
 * @since 2.11.0
 */
public abstract class AbstractLoadingCache<KEY, VALUE> extends AbstractCache<KEY, VALUE> {

    private final LoadingCache<KEY, VALUE> cache = createCacheBuilder().build(new CacheLoader<>() {
        @Override
        public VALUE load(KEY key) throws Exception {
            return AbstractLoadingCache.this.load(key);
        }
    });

    /**
     * constructor
     */
    protected AbstractLoadingCache() {
    }

    /**
     * The operation to be cached
     *
     * @param key
     *            the cache key
     * @return the value to store
     * @throws BaseException
     *             if any error occurs
     */
    protected abstract VALUE load(KEY key) throws BaseException;

    @Override
    public LoadingCache<KEY, VALUE> getCache() {
        return cache;
    }

    /**
     * Returns the value from the cache
     * 
     * @param key
     *            the cache key we are looking for
     * @return the value found
     * @throws BaseException
     *             if any error occurs
     */
    public VALUE get(KEY key) throws BaseException {
        if (key == null) {
            throw new InvalidParameterException(CoffeeFaultType.INVALID_INPUT, "key is missing");
        }

        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof BaseException baseException) {
                throw baseException;
            } else {
                throw new TechnicalException(CoffeeFaultType.GENERIC_EXCEPTION, MessageFormat.format("Error reading from cache: [{0}]", key), e);
            }
        } finally {
            updateMetrics();
        }
    }

}
