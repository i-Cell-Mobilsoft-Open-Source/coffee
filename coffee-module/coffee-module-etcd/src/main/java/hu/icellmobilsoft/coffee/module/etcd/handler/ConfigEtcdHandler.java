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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
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
    private Logger log;

    @Inject
    private ConfigEtcdService configEtcdService;

    /**
     * <p>getValue.</p>
     */
    public String getValue(String key) throws BaseException {
        Set<String> previousKeys = new HashSet<String>();
        return getValueWithCircleCheck(key, previousKeys);
    }

    /**
     * <p>putValue.</p>
     */
    public void putValue(String key, Object value) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new BaseException("key is empty!");
        }
        configEtcdService.putValue(key, value);
    }

    private String getValueWithCircleCheck(String key, Set<String> previousKeys) throws BaseException {
        if (StringUtils.isBlank(key)) {
            throw new BaseException("key is empty!");
        }
        String value = configEtcdService.getValue(key);
        if (StringUtils.startsWith(value, "{") && StringUtils.endsWith(value, "}")) {
            String newKey = value.substring(1, value.length() - 1);
            if (previousKeys.contains(newKey)) {
                throw new BaseException("Circle found in the chain!");
            } else {
                previousKeys.add(newKey);
            }
            return getValueWithCircleCheck(newKey, previousKeys);
        } else {
            return value;
        }
    }

}
