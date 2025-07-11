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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * {@link DynamicConfigDocs} file generation test
 * 
 * @author mark.petrenyi
 * @since 1.10.0
 */
class DynamicConfigDocsTest {

    @Test
    @DisplayName("generated file should contain dynamic config keys")
    void generatedFileShouldContainConfigKeys() throws URISyntaxException, IOException {
        URL generatedFileUrl = getClass().getResource("/" + ConfigDocConfig.DEFAULT_OUTPUT_PATH + ConfigDocConfig.DEFAULT_DYNAMIC_OUTPUT_FILE_NAME);
        assertNotNull(generatedFileUrl);

        String generatedFile = Files.readString(Paths.get(generatedFileUrl.toURI()));
        assertTrue(generatedFile.contains(":0: abc"));
        assertTrue(generatedFile.contains("Dynamic config {0} config keys"));
        assertTrue(generatedFile.contains(":0: xyz"));
        assertTrue(generatedFile.contains("Title override for config key {0}"));
        // The keys need to be included twice for the two injections (therefore the split should have 3 parts)
        assertEquals(3, generatedFile.split("test\\.\\{0\\}\\.foo").length);
    }
}
