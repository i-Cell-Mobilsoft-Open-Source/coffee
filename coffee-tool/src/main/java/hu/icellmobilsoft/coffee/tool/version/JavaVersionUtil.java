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
package hu.icellmobilsoft.coffee.tool.version;

import java.security.InvalidParameterException;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

/**
 * Java version helper.
 *
 * @author arnold.bucher
 * @since 1.0.0
 */
@Vetoed
public class JavaVersionUtil {

    /**
     * Returns the {@link JavaVersion} of current system.
     * 
     * @return {@link JavaVersion} of current system
     */
    public static JavaVersion getCurrentSystemJavaVersion() {
        return getJavaVersion(System.getProperty("java.version"));
    }

    /**
     * Convert version property {@link String} to {@link JavaVersion}.
     *
     * @param versionProperty
     *            version property {@code String}
     * @return converted {@code JavaVersion}
     * @throws InvalidParameterException
     *             if {@code versionProperty} cannot be converted
     */
    public static JavaVersion getJavaVersion(String versionProperty) {
        if (StringUtils.isBlank(versionProperty)) {
            throw new InvalidParameterException("versionProperty is required!");
        }

        // build new version info now
        JavaVersion javaVersion = new JavaVersion();

        try {
            String[] versionPart = versionProperty.split("\\.");
            if (versionPart.length > 0) {
                javaVersion.setMajor(Integer.parseInt(versionPart[0]));
            }
            if (versionPart.length > 1) {
                javaVersion.setFeature(Integer.parseInt(versionPart[1]));
            }
            if (versionPart.length > 2) {
                javaVersion.setPatch(versionPart[2]);
                String[] s = javaVersion.getPatch().split("_");
                if (s.length > 1) {
                    javaVersion.setPatchUpdate(Integer.parseInt(s[1]));
                }
            }
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Invalid java version! [" + versionProperty + "]");
        }

        return javaVersion;
    }

}
