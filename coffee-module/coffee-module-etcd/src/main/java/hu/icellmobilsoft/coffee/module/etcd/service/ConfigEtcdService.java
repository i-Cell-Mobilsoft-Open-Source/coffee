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
package hu.icellmobilsoft.coffee.module.etcd.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Service class for MP-Config and other visualized data handling
 *
 * @since 1.0.0
 * @author imre.scheffer
 */
public class ConfigEtcdService {

    private Logger log;

    private EtcdService etcdService;

    /**
     * Default constructor, constructs a new object.
     */
    public ConfigEtcdService() {
        super();
    }

    /**
     * ETCD service initialization
     * 
     * @param etcdService
     *            ETCD service
     */
    public void init(EtcdService etcdService) {
        this.etcdService = etcdService;
    }

    /**
     * Check initialization
     * 
     * @throws BaseException
     *             Exception if etcdService is null
     */
    protected void checkInit() throws BaseException {
        if (etcdService == null) {
            throw new TechnicalException("EtcdRepository is not initialized!");
        }
    }

    /**
     * Get value from ETCD
     * 
     * @param key
     *            key in ETCD
     * @return value of key, no value is {@value EtcdService#EMPTY_VALUE} (empty String)
     * @throws BaseException
     *             technical error
     * @throws BONotFoundException
     *             if key not found
     */
    public String getRawValue(String key) throws BaseException {
        checkInit();
        return etcdService.get(key).orElse(EtcdService.EMPTY_VALUE);
    }

    /**
     * Get value from ETCD. If value is inside "{}" then continue finding to this value as key
     * 
     * @param key
     *            key in ETCD
     * @return value of key, no value is {@value EtcdService#EMPTY_VALUE} (empty String)
     * @throws BaseException
     *             technical error
     * @throws TechnicalException
     *             key-value loop found
     */
    public String getValue(String key) throws BaseException {
        checkInit();
        Set<String> previousKeys = new HashSet<String>();
        return getValueWithCircleCheck(key, previousKeys);
    }

    /**
     * Put value to ETCD
     * 
     * @param key
     *            key in ETCD
     * @param value
     *            value save to ETCD. Null value means {@value EtcdService#EMPTY_VALUE} (empty String)
     * @throws BaseException
     *             technical error
     */
    public void putValue(String key, Object value) throws BaseException {
        checkInit();
        etcdService.set(key, value == null ? null : value.toString());
    }

    /**
     * Get all key-values pair from ETCD
     * 
     * @return all key/values
     * @throws BaseException
     *             technical error
     */
    public Map<String, String> getAll() throws BaseException {
        checkInit();
        Map<String, Optional<String>> valueMap = etcdService.get(EtcdService.STARTKEY, EtcdService.ENDKEY);
        return reMapOptional(valueMap);
    }

    /**
     * Remapping {@code Map<String, Optional<String>>} to {@code Map<String, String>}
     * 
     * @param map
     *            input
     * @return output
     */
    protected Map<String, String> reMapOptional(Map<String, Optional<String>> map) {
        Map<String, String> result = new HashMap<>();
        for (Entry<String, Optional<String>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().orElse(EtcdService.EMPTY_VALUE));
        }
        return result;
    }

    /**
     * Search/Select all key-values pairs by input starter key. ETCD does not natively support key filtering, only search for the first match. This is
     * limited implementation when the input key is supplemented with the input key endkey
     * <ul>
     * <li>input: "abc" - search keys: "abc"-"abd"</li>
     * <li>input: "c" - search keys: "c"-"d"</li>
     * </ul>
     * 
     * @param startKey
     *            start key for filtration
     * @return key/values
     * @throws BaseException
     *             technical error
     */
    public Map<String, String> searchList(String startKey) throws BaseException {
        if (startKey == null) {
            return Collections.emptyMap();
        }
        checkInit();
        String endKey = EtcdService.ENDKEY;
        try {
            int strLastIndex = startKey.length() - 1;
            char lastChar = startKey.charAt(strLastIndex);
            endKey = startKey.substring(0, strLastIndex) + ++lastChar;
        } catch (Exception e) {
            log.debug("etcd: cannot increase last character of startKey [{0}]", startKey);
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Convert exception: " + e.getLocalizedMessage(), e);
        }
        Map<String, Optional<String>> valueMap = etcdService.get(startKey, endKey);
        return reMapOptional(valueMap);
    }

    /**
     * Extended implementation of {@link #searchList(String)} when input is aggregated by input keys
     * 
     * @param startKeyArray
     *            start keys array for filtration
     * @return key/values
     * @throws BaseException
     *             technical error
     */
    public Map<String, String> searchList(String[] startKeyArray) throws BaseException {
        Map<String, String> aggregatedMap = new HashMap<>();
        for (String key : startKeyArray) {
            Map<String, String> configMAp = searchList(key);
            aggregatedMap.putAll(configMAp);
        }
        return aggregatedMap;
    }

    /**
     * Delete key from ETCD
     * 
     * @param key
     *            key for deletion
     * @throws BaseException
     *             technical error
     * @throws BONotFoundException
     *             if not found key
     */
    public void delete(String key) throws BaseException {
        checkInit();
        etcdService.delete(key);
    }

    /**
     * Inifinity loop cheking for values
     * 
     * @param key
     *            initial key
     * @param previousKeys
     *            set of previous keys to check
     * @return real value
     * @throws BaseException
     *             technical error
     * @throws TechnicalException
     *             key-value loop found
     */
    protected String getValueWithCircleCheck(String key, Set<String> previousKeys) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new InvalidParameterException("key is blank!");
        }
        String value = getRawValue(key);
        if (StringUtils.startsWith(value, "{") && StringUtils.endsWith(value, "}")) {
            String newKey = value.substring(1, value.length() - 1);
            if (previousKeys.contains(newKey)) {
                throw new TechnicalException("Circle found in the chain for key [" + key + "]!");
            } else {
                previousKeys.add(newKey);
            }
            return getValueWithCircleCheck(newKey, previousKeys);
        } else {
            return value;
        }
    }

    /**
     * Returns the ETCD service
     * 
     * @return the ETCD service
     */
    public EtcdService getEtcdService() {
        return etcdService;
    }
}
