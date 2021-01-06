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
package hu.icellmobilsoft.coffee.tool.utils.string;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.enterprise.inject.Vetoed;
import java.util.regex.Pattern;

/**
 * Cache for precompiled regex patterns used in {@link StringUtil} class
 *
 * @author jozsef.kelemen
 * @since 1.3.0
 */
@Vetoed
public class RegexPatternCache {

    private static final int DEFAULT_CACHE_SIZE = 10_000;

    private CacheLoader<String, Pattern> loader = new CacheLoader<>() {
        @Override
        public Pattern load(String key) {
            return Pattern.compile(key, Pattern.CASE_INSENSITIVE);
        }
    };

    private LoadingCache<String, Pattern> cache;
    
    /**
     * Instantiates a new Regex pattern cache.
     */
    public RegexPatternCache() {
        this(DEFAULT_CACHE_SIZE);
    }    
    
    /**
     * Instantiates a new Regex pattern cache.
     *
     * @param cacheSize
     *            the cache size
     */
    public RegexPatternCache(int cacheSize) {
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(loader);
    }

    
    /**
     * Gets a pattern from cache if found, compiles and stores otherwise.
     *
     * @param patternKey
     *            the pattern key
     * @return the pattern
     */
    public Pattern getPattern(String patternKey) {
        return cache.getUnchecked(patternKey);
    }

    /**
     * Clear cache values
     */
    public void clear() {
        cache.invalidateAll();
    }
}
