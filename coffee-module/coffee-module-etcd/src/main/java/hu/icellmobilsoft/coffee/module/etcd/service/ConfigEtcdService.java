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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * <p>ConfigEtcdService class.</p>
 *
 * @since 1.0.0
 */
@Dependent
public class ConfigEtcdService extends BaseEtcdService<String> {

    private static final long serialVersionUID = 1L;

    private static final String STARTKEY = " ";
    private static final String ENDKEY = "\0";

    @Inject
    private Logger log;

    /**
     * <p>getValue.</p>
     */
    public String getValue(String key) throws BaseException {
        return getEtcdData(key, String.class);
    }

    /**
     * <p>putValue.</p>
     */
    public void putValue(String key, Object value) throws BaseException {
        setEtcdData(key, value == null ? null : value.toString());
    }

    /**
     * <p>getList.</p>
     */
    public Map<String, String> getList() throws BaseException {
        return getEtcdDataList(STARTKEY, ENDKEY);
    }

    /**
     * <p>searchList.</p>
     */
    public Map<String, String> searchList(String startKey) throws BaseException {
        String endKey = ENDKEY;
        try {
            int strLastIndex = startKey.length() - 1;
            char lastChar = startKey.charAt(strLastIndex);
            endKey = startKey.substring(0, strLastIndex) + ++lastChar;
        } catch (Exception e) {
            log.debugv("etcd: cannot increase last character of startKey [{0}]", startKey);
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, "Convert exception: " + e.getLocalizedMessage(), e);
        }
        return getEtcdDataList(startKey, endKey);
    }

    /**
     * <p>searchList.</p>
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
     * <p>delete.</p>
     */
    public void delete(String key) throws BaseException {
        deleteEtcdData(key, String.class);
    }
}
