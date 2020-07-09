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
 * MDC Adapter interface for wrapping different MDC implementations
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public interface MDCAdapter {

    /**
     * Puts value into underlying MDC implementation.
     *
     * @param key
     *            MDC lookup key fro the value
     * @param val
     *            the value
     */
    public void put(String key, String val);

    /**
     * Gets value for key or {@code null} from underlying MDC implementation.
     *
     * @param key
     *            MDC lookup key
     *
     * @return the value or {@code null}
     */
    public String get(String key);

    /**
     * Removes the value for key from the underlying MDC implementation.
     *
     * @param key
     *            MDC lookup key
     */
    public void remove(String key);

    /**
     * Returns the diagnostic context map from the underlying MDC implementation.
     *
     * @return the MDC map
     */
    public Map<String, String> getMap();

    /**
     * Clears the underlying MDC implementation.
     */
    public void clear();

}
