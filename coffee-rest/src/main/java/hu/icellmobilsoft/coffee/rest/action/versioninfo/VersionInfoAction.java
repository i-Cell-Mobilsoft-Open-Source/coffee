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
package hu.icellmobilsoft.coffee.rest.action.versioninfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.api.exception.TechnicalException;
import hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType;

/**
 * Action for reading manifest file
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 */
@Model
public class VersionInfoAction {

    private static final String NEW_LINE = "\n";
    private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
    private static final String IMPLEMENTATION_TITLE = "Implementation-Title";
    private static final String CLASS_PATH = "Class-Path";

    @Inject
    @ConfigProperty(name = "quarkus.application.name")
    Optional<String> quarkusAppName;

    @Inject
    @ConfigProperty(name = "coffee.app.name")
    Optional<String> coffeeAppName;

    @Inject
    private ServletContext servletContext;

    /**
     * Default constructor
     */
    public VersionInfoAction() {
        // Default constructor for java 21
    }

    /**
     * read manifest from jar file (quarkus and wildfly)
     * 
     * @return the manifest content
     * @throws BaseException
     *             if any error occurs
     */
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
                        return warVersionInfo();
                    }
                    return sb.toString();
                }
            }
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "MANIFEST.MF not found for " + appName);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }

    /**
     * read manifest from war file (wildfly)
     * 
     * @param servletRequest
     *            the servlet request object
     * @return the manifest content
     * @throws BaseException
     *             if any error occurs
     */
    private String warVersionInfo() throws BaseException {
        if (servletContext == null) {
            return null;
        }
        try {
            InputStream inputStream = servletContext.getResourceAsStream(META_INF_MANIFEST_MF);
            StringBuilder version = new StringBuilder();
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    version.append(line);
                    version.append(NEW_LINE);
                }
            }
            return version.toString();
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
