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
package hu.icellmobilsfoft.coffee.module.wildfly.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletContext;

/**
 * Reads version information from the deployed WAR, rather than a dependency's manifest.
 *
 * @author balazs.bakos
 * @since 3.0.0
 */
public final class VersionInfoUtil {

    private static final String META_INF_MANIFEST_MF = "META-INF/MANIFEST.MF";
    private static final String NEW_LINE = "\n";

    private VersionInfoUtil() {
    }

    /**
     * Reads the complete application manifest using the legacy versionInfo response format. Preserves line order, continuation lines and named
     * sections, normalizing line endings to LF.
     *
     * @param servletContext
     *            the deployed web application's servlet context
     * @return manifest contents with an LF after each line, or an empty string if the WAR has no manifest
     * @throws IOException
     *             if the manifest cannot be read
     */
    public static String getVersionInfo(ServletContext servletContext) throws IOException {
        InputStream inputStream = servletContext.getResourceAsStream(META_INF_MANIFEST_MF);
        if (inputStream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder version = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                version.append(line).append(NEW_LINE);
            }
            return version.toString();
        }
    }
}

