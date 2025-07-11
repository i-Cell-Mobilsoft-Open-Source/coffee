/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.docgen.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.module.docgen.config.config.ConfigDocConfig;

/**
 * {@link ConfigDoc} file generation test
 * 
 * @author martin.nagy
 * @since 1.9.0
 */
class ConfigDocTest {

    @Test
    @DisplayName("generated file should contain config keys")
    void generatedFileShouldContainConfigKeys() throws URISyntaxException, IOException {
        URL generatedFileUrl = getClass().getResource("/" + ConfigDocConfig.DEFAULT_OUTPUT_PATH + ConfigDocConfig.DEFAULT_OUTPUT_FILE_NAME);
        assertNotNull(generatedFileUrl);

        String generatedFile = Files.readString(Paths.get(generatedFileUrl.toURI()));

        // key column
        assertTrue(generatedFile.contains("test.foo"));
        assertTrue(generatedFile.contains("test.bar"));
        assertTrue(generatedFile.contains("test.baz"));
        assertTrue(generatedFile.contains("test2.xxx"));

        // description column
        assertTrue(generatedFile.contains("Override..."));

        // since column
        assertTrue(generatedFile.contains("3.14159"));

        // features column
        assertTrue(generatedFile.contains("⏳"));
        assertTrue(generatedFile.contains("🚀"));

        // Title overwritten and heading level is 1
        assertTrue(generatedFile.contains("= Title Test"));
    }
}
