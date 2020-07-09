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

import hu.icellmobilsoft.coffee.se.logging.DefaultLogger;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * MDC adapter for {@code org.slf4j.MDC}
 * 
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class Slf4jMDCAdapter implements MDCAdapter {

    private Logger log = DefaultLogger.getLogger(Slf4jMDCAdapter.class);

    private Method put;
    private Method get;
    private Method getMap;
    private Method clear;
    private Method remove;

    public Slf4jMDCAdapter() throws Exception {
        Class<?> slf4jMDC = Class.forName("org.slf4j.MDC");
        put = slf4jMDC.getMethod("put", String.class, String.class);
        get = slf4jMDC.getMethod("get", String.class);
        getMap = slf4jMDC.getMethod("getCopyOfContextMap");
        clear = slf4jMDC.getMethod("clear");
        remove = slf4jMDC.getMethod("remove", String.class);
    }

    /** {@inheritDoc} */
    @Override
    public void put(String key, String val) {
        try {
            put.invoke(null, key, val);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not put key:[{0}] with val:[{1}] into slf4j MDC:[{2]}", key, val, e.getLocalizedMessage()), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String get(String key) {
        try {
            return (String) get.invoke(null, key);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not get key:[{0}] from slf4j MDC:[{1]}", key, e.getLocalizedMessage()), e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void remove(String key) {
        try {
            remove.invoke(null, key);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not remove key:[{0}] from slf4j MDC:[{1]}", key, e.getLocalizedMessage()), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getMap() {
        try {
            return (Map<String, String>) getMap.invoke(null);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not getMap from slf4j MDC:[{0]}", e.getLocalizedMessage()), e);
        }
        return Collections.emptyMap();
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        try {
            clear.invoke(null);
        } catch (Exception e) {
            log.debug(MessageFormat.format("Could not clear slf4j MDC:[{0]}", e.getLocalizedMessage()), e);
        }
    }

}
