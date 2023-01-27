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
package hu.icellmobilsoft.coffee.model.base;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.enterprise.inject.Vetoed;

/**
 * Cache for fields and methods of entity classes used in {@link AbstractProvider}
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
@Vetoed
public class ClassFieldsAndMethodsCache {

    private static final int DEFAULT_CACHE_SIZE = 10_000;

    private final LoadingCache<Class<?>, Pair<List<Field>, List<Method>>> cache;

    /**
     * Instantiates a new class fields and methods cache
     */
    public ClassFieldsAndMethodsCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    /**
     * Instantiates a new class fields and methods with the given cache size
     *
     * @param cacheSize
     *            the cache size
     */
    public ClassFieldsAndMethodsCache(int cacheSize) {
        cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build(loader);
    }

    private List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList());
        result.addAll(filteredFields);
        return Collections.unmodifiableList(result);
    }

    private List<Method> getAllMethods(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Method> result = new ArrayList<>(getAllMethods(clazz.getSuperclass()));
        List<Method> filteredMethods = Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toList());
        result.addAll(filteredMethods);
        return Collections.unmodifiableList(result);
    }

    private CacheLoader<Class<?>, Pair<List<Field>, List<Method>>> loader = new CacheLoader<>() {
        @Override
        public Pair<List<Field>, List<Method>> load(Class<?> clazz) {
            return Pair.of(getAllFields(clazz), getAllMethods(clazz));
        }
    };

    /**
     * Gets a pair of fields and methods lists of the given class from cache if found
     *
     * @param clazz
     *            the class
     * @return a pair of fields and methods
     */
    public Pair<List<Field>, List<Method>> getFieldsAndMethods(Class<?> clazz) {
        return cache.getUnchecked(clazz);
    }

    /**
     * Clear cache values
     */
    public void clear() {
        cache.invalidateAll();
    }

}
