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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link ConfigFileWatcher} unit test
 *
 * @author martin.nagy
 * @since 2.13.0
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ConfigFileWatcherTest {

    @Test
    void should_notify_for_file_changes(@TempDir Path tempDir) throws IOException {
        // GIVEN
        int writeCount = 10;
        Path tempFile = Files.createTempFile(tempDir, getClass().getCanonicalName() + "-", "");
        AtomicInteger notificationCounter = new AtomicInteger(0);

        // WHEN
        try (ConfigFileWatcher configFileWatcher = new ConfigFileWatcher(tempFile)) {
            configFileWatcher.addListener(path -> {
                notificationCounter.incrementAndGet();
            });

            for (int i = 0; i < writeCount; i++) {
                Files.write(tempFile, (i + "").getBytes());
            }

            // THEN
            Awaitility.await().untilAsserted(() -> {
                assertTrue(
                        notificationCounter.get() >= writeCount,
                        MessageFormat.format("notification count [{0}] >= write count [{1}]", notificationCounter.get(), writeCount));
            });
        }
    }
}
