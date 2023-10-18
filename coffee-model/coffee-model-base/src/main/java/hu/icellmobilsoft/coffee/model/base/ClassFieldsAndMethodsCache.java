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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Cache for fields and methods of entity classes used in {@link AbstractProvider}
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
public class ClassFieldsAndMethodsCache<K extends Class, V extends Pair<List<Field>, List<Method>>> extends LinkedHashMap<K, V> {

    private static final int DEFAULT_CACHE_SIZE = 10_000;

    /**
     * Size of cache
     */
    private final int cacheSize;

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
        super(cacheSize);
        this.cacheSize = cacheSize;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        result.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return Collections.unmodifiableList(result);
    }

    private List<Method> getAllMethods(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Method> result = new ArrayList<>(getAllMethods(clazz.getSuperclass()));
        result.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        return Collections.unmodifiableList(result);
    }

    /**
     * Gets a pair of fields and methods lists of the given class from cache if found
     *
     * @param clazz
     *            the class
     * @return a pair of fields and methods
     */
    public Pair<List<Field>, List<Method>> getFieldsAndMethods(Class<?> clazz) {
        // we should optimize this cache mechanics with CDI, we can collect all information after the AfterDeploymentValidation phase
        if (this.containsKey(clazz)) {
            return this.get(clazz);
        }
        V fieldMethodPair = (V) Pair.of(getAllFields(clazz), getAllMethods(clazz));
        this.put((K) clazz, fieldMethodPair);
        return fieldMethodPair;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > cacheSize;
    }

}
