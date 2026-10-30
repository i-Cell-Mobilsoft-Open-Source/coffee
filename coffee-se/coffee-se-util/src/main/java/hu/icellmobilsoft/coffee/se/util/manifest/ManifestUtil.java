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
package hu.icellmobilsoft.coffee.se.util.manifest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Reads manifest contents from an input stream.
 *
 * @author balazs.bakos
 * @since 4.0.0
 */
public final class ManifestUtil {

    /**
     * Standard resource path of the manifest inside an archive
     */
    public static final String MANIFEST_RESOURCE_PATH = "META-INF/MANIFEST.MF";

    private static final String NEW_LINE = "\n";

    private ManifestUtil() {
    }

    /**
     * Reads the complete manifest as UTF-8 text. Preserves line order, continuation lines and named sections, normalizing line endings to LF.
     * Closes the supplied stream, including when reading fails.
     *
     * @param inputStream
     *            the manifest input stream, or {@code null} if no manifest is available
     * @return manifest contents with an LF after each line, or an empty string if the stream is {@code null}
     * @throws IOException
     *             if the manifest cannot be read
     */
    public static String getManifestContent(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(NEW_LINE);
            }
            return content.toString();
        }
    }
}
