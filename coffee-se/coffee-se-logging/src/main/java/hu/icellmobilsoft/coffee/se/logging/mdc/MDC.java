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

import java.util.Map;

/**
 * Mapped Diagnostic Context. <br>
 * 
 * Internally uses an adapter in order to delegate calls to an existing MDC implementation on the classpath. <br>
 * 
 * Default adapters are (in order of priority): <br>
 * 
 * <ul>
 * <li>{@link JbossMDCAdapter} - for using {@code org.jboss.logging.MDC}</li>
 * <li>{@link Slf4jMDCAdapter} - for using {@code org.slf4j.MDC}</li>
 * <li>{@link CoffeeMDCAdapter} - fallback MDC, uses a {@link ThreadLocal}</li>
 * </ul>
 *
 *
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public final class MDC {

    private static final MDCAdapter adapter = MDCAdapters.findAdapter();

    /**
     * Default constructor, constructs a new object.
     */
    public MDC() {
        super();
    }

    /**
     * Puts value into underlying MDC implementation.
     *
     * @param key
     *            MDC lookup key fro the value
     * @param val
     *            the value
     */
    public static void put(String key, String val) {
        adapter.put(key, val);
    }

    /**
     * Gets value for key or {@code null} from underlying MDC implementation.
     *
     * @param key
     *            MDC lookup key
     *
     * @return the value or {@code null}
     */
    public static String get(String key) {
        return adapter.get(key);
    }

    /**
     * Removes the value for key from the underlying MDC implementation.
     *
     * @param key
     *            MDC lookup key
     */
    public static void remove(String key) {
        adapter.remove(key);
    }

    /**
     * Returns the diagnostic context map from the underlying MDC implementation.
     *
     * @return the MDC map
     */
    public static Map<String, String> getMap() {
        return adapter.getMap();
    }

    /**
     * Clears the underlying MDC implementation.
     */
    public static void clear() {
        adapter.clear();
    }

}
