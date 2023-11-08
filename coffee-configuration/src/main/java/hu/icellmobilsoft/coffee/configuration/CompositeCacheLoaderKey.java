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

import java.util.Objects;

/**
 * Összetett kulcs osztály létrehozása az ApplicationConfiguration osztály cache - jéhez.
 *
 * @author csaba.suli
 * @since 1.1.0
 */
public class CompositeCacheLoaderKey {

    private final String key;

    @SuppressWarnings("rawtypes")
    private final Class valueClass;

    /**
     * Creates an instance with configuration key and type
     * 
     * @param key
     *            configuration key
     * @param valueClass
     *            configuration value type
     */
    public CompositeCacheLoaderKey(String key, Class valueClass) {
        this.key = key;
        this.valueClass = valueClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CompositeCacheLoaderKey that = (CompositeCacheLoaderKey) o;
        return Objects.equals(key, that.key) && Objects.equals(valueClass, that.valueClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, valueClass);
    }

    /**
     * Returns configuration key
     * 
     * @return configuration key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns configuration value type
     * 
     * @return configuration value type
     */
    public Class getValueClass() {
        return valueClass;
    }
}
