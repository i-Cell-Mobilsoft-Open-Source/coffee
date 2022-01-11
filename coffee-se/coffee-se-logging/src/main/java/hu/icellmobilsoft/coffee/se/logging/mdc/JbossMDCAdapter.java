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

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * MDC adapter for {@code org.jboss.logging.MDC}
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class JbossMDCAdapter implements MDCAdapter {

    private Logger log = DefaultLogger.getLogger(JbossMDCAdapter.class);

    private Method put;
    private Method get;
    private Method getMap;
    private Method clear;
    private Method remove;

    /**
     * JbossMDCAdapter default constructor
     * 
     * @throws Exception
     *             error
     */
    public JbossMDCAdapter() throws Exception {
        Class<?> jbossMDC = Class.forName("org.jboss.logging.MDC");
        put = jbossMDC.getMethod("put", String.class, Object.class);
        get = jbossMDC.getMethod("get", String.class);
        getMap = jbossMDC.getMethod("getMap");
        clear = jbossMDC.getMethod("clear");
        remove = jbossMDC.getMethod("remove", String.class);
    }

    /** {@inheritDoc} */
    @Override
    public void put(String key, String val) {
        try {
            put.invoke(null, key, val);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not put key:[{0}] with val:[{1}] into jboss MDC:[{2]}", key, val, e.getLocalizedMessage()), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String get(String key) {
        try {
            Object getResult = get.invoke(null, key);
            return toString(getResult);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not get key:[{0}] from jboss MDC:[{1]}", key, e.getLocalizedMessage()), e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void remove(String key) {
        try {
            remove.invoke(null, key);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not remove key:[{0}] from jboss MDC:[{1]}", key, e.getLocalizedMessage()), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getMap() {
        try {
            Map<String, Object> getMapResult = (Map<String, Object>) getMap.invoke(null);
            return getMapResult.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> toString(e.getValue())));
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not getMap from jboss MDC:[{0]}", e.getLocalizedMessage()), e);
        }
        return Collections.emptyMap();
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        try {
            clear.invoke(null);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not clear jboss MDC:[{0]}", e.getLocalizedMessage()), e);
        }
    }

    private String toString(Object getResult) {
        return (getResult instanceof String || getResult == null) ? (String) getResult : String.valueOf(getResult);
    }

}
