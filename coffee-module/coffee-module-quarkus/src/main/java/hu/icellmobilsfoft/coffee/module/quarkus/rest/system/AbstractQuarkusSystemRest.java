/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsfoft.coffee.module.quarkus.rest.system;

import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import hu.icellmobilsoft.coffee.cdi.config.IConfigKey;
import hu.icellmobilsoft.coffee.cdi.config.IQuarkusConfigKey;
import hu.icellmobilsoft.coffee.rest.system.AbstractSystemRest;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType;

/**
 * SystemRest endpoint for quarkus
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 * @see AbstractSystemRest
 */
public abstract class AbstractQuarkusSystemRest extends AbstractSystemRest {

    @Inject
    @ConfigProperty(name = IQuarkusConfigKey.QUARKUS_APPLICATION_NAME)
    Optional<String> quarkusAppName;

    @Inject
    @ConfigProperty(name = IConfigKey.COFFEE_APP_NAME)
    Optional<String> coffeeAppName;

    /**
     * Default constructor
     */
    public AbstractQuarkusSystemRest() {
        // Default constructor for java 21
    }

    @Override
    public String versionInfo() throws BaseException {
        String appName = getAppName();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(META_INF_MANIFEST_MF);

            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream is = url.openStream()) {
                    Manifest mf = new Manifest(is);
                    Attributes mainAttributes = mf.getMainAttributes();
                    StringBuilder sb = new StringBuilder();
                    String implementationTitle = mf.getMainAttributes().getValue(IMPLEMENTATION_TITLE);
                    mainAttributes.entrySet()
                            .stream()
                            .filter(entry -> StringUtils.equals(appName, implementationTitle))
                            .filter(entry -> !CLASS_PATH.equalsIgnoreCase(entry.getKey().toString()))
                            .forEach(entry -> sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(NEW_LINE));

                    if (sb.isEmpty()) {
                        return MessageFormat.format("cannot find MANIFEST.MF for [{0}]", appName);
                    }
                    return sb.toString();
                }
            }
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "MANIFEST.MF not found for " + appName);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }

    private String getAppName() {
        if (quarkusAppName.isEmpty()) {
            return coffeeAppName.isEmpty() ? "" : coffeeAppName.get();
        }
        return quarkusAppName.get();
    }

}
