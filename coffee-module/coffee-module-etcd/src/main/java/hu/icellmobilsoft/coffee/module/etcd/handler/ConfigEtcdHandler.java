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
package hu.icellmobilsoft.coffee.module.etcd.handler;

import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.etcd.service.ConfigEtcdService;

/**
 * Az ETCD paramétereket kezelését segíti. A lekérdezésnél a beágyazottan másik kulcsokra hivatkozó értékeket kiértékeli körellenőrzéssel a láncban.
 *
 * @author robert.kaplar
 * @since 1.0.0
 */
@Dependent
public class ConfigEtcdHandler {

    @Inject
    private ConfigEtcdService configEtcdService;

    /**
     * Default constructor, constructs a new object.
     */
    public ConfigEtcdHandler() {
        super();
    }

    /**
     * Returns value of given key in ETCD.
     *
     * @param key
     *            key in ETCD
     * @return value of key or empty {@link String} if key does not exist
     * @throws BaseException
     *             if technical error
     * @see ConfigEtcdService#getValue(String)
     */
    @Traced(component = SpanAttribute.Etcd.Jetcd.COMPONENT, kind = SpanAttribute.Etcd.Jetcd.KIND, dbType = SpanAttribute.Etcd.DB_TYPE)
    public String getValue(String key) throws BaseException {
        Set<String> previousKeys = new HashSet<String>();
        return getValueWithCircleCheck(key, previousKeys);
    }

    /**
     * Assigns given value to given key in ETCD.
     *
     * @param key
     *            key in ETCD
     * @param value
     *            value to set, if value null, then empty {@link String} is set
     * @throws BaseException
     *             if technical error
     * @see ConfigEtcdService#putValue(String, Object)
     */
    @Traced(component = SpanAttribute.Etcd.Jetcd.COMPONENT, kind = SpanAttribute.Etcd.Jetcd.KIND, dbType = SpanAttribute.Etcd.DB_TYPE)
    public void putValue(String key, Object value) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new InvalidParameterException("key is blank!");
        }
        configEtcdService.putValue(key, value);
    }

    private String getValueWithCircleCheck(String key, Set<String> previousKeys) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new InvalidParameterException("key is blank!");
        }
        String value = configEtcdService.getValue(key);
        if (StringUtils.startsWith(value, "{") && StringUtils.endsWith(value, "}")) {
            String newKey = value.substring(1, value.length() - 1);
            if (previousKeys.contains(newKey)) {
                throw new BaseException("Circle found in the chain for key [" + key + "]!");
            } else {
                previousKeys.add(newKey);
            }
            return getValueWithCircleCheck(newKey, previousKeys);
        } else {
            return value;
        }
    }

}
