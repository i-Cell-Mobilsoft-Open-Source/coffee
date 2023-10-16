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
package hu.icellmobilsoft.coffee.se.logging.mdc;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback MDC adapter for coffee; if no other adapter is available
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class CoffeeMDCAdapter implements MDCAdapter {

    private final ThreadLocal<Map<String, String>> mdcMap = new ThreadLocal<>();

    /**
     * Default constructor, constructs a new object.
     */
    public CoffeeMDCAdapter() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public void put(String key, String val) {
        mdcMap().put(key, val);
    }

    /** {@inheritDoc} */
    @Override
    public String get(String key) {
        return mdcMap().get(key);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(String key) {
        mdcMap().remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getMap() {
        return Map.copyOf(mdcMap());
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        mdcMap().clear();
    }

    private Map<String, String> mdcMap() {
        Map<String, String> map = mdcMap.get();
        if (map == null) {
            map = new HashMap<>();
            mdcMap.set(map);
        }
        return map;
    }

}
