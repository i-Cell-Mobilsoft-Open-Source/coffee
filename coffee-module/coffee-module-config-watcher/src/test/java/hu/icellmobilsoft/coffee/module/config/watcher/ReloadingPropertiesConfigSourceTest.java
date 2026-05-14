/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.config.watcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link ReloadingPropertiesConfigSource} unit test
 *
 * @author martin.nagy
 * @since 2.13.0
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReloadingPropertiesConfigSourceTest {

    @Test
    void config_source_should_automatically_change_on_file_change(@TempDir Path tempDir) throws IOException {
        // GIVEN
        Path tempFile = Files.createTempFile(tempDir, getClass().getCanonicalName() + "-", ".properties");
        Files.write(tempFile, "dummy=foo".getBytes());

        try (ReloadingPropertiesConfigSource configSource = new ReloadingPropertiesConfigSource(tempFile.toUri().toURL())) {
            assertEquals("foo", configSource.getValue("dummy"));

            // WHEN
            Files.write(tempFile, "dummy=bar".getBytes());

            // THEN
            Awaitility.await().untilAsserted(() -> {
                assertEquals("bar", configSource.getValue("dummy"), "configSource.getValue(\"dummy\")");
            });
        }
    }
}
