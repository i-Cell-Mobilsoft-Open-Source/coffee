/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.cdi;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;

/**
 * Application scoped container class
 *
 * @author karoly.tamas
 * @since 1.6.0
 */
@ApplicationScoped
public class BaseApplicationContainer {

    @Resource(lookup = "java:app/AppName")
    private String appName;

    private Map<String, Object> objectMap;

    /**
     * Object map
     *
     * @return object map
     */
    public synchronized Map<String, Object> getObjectMap() {
        if (objectMap == null) {
            objectMap = new HashMap<>();
        }
        return objectMap;
    }

    /**
     * Coffee app name from config or lookup is not set
     *
     * @return coffee app name
     */
    public synchronized String getCoffeeAppName() {
        final String coffeeAppName;
        if (getObjectMap().containsKey(IConfigKey.COFFEE_APP_NAME) && getObjectMap().get(IConfigKey.COFFEE_APP_NAME) instanceof String) {
            coffeeAppName = (String) getObjectMap().get(IConfigKey.COFFEE_APP_NAME);
        } else {
            Optional<String> coffeeAppNameConfig = ConfigProvider.getConfig().getOptionalValue(IConfigKey.COFFEE_APP_NAME, String.class);
            if (coffeeAppNameConfig.isEmpty()) {
                coffeeAppName = appName;
            } else {
                coffeeAppName = StringUtils.defaultIfBlank(coffeeAppNameConfig.get(), appName);
            }
            getObjectMap().put(IConfigKey.COFFEE_APP_NAME, coffeeAppName);
        }

        return coffeeAppName;
    }
}
