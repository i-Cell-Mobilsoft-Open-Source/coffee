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
package hu.icellmobilsoft.coffee.module.docgen.sqlcatalog;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.deltaspike.data.api.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.config.SqlCatalogConfig;

/**
 * Sql catalog generation test by {@link Query} annotation
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
class SqlCatalogTest {

    @Test
    @DisplayName("generated file should contain sql catalog data")
    void generatedFileShouldContainSqlCatalogData() throws URISyntaxException, IOException {
        URL generatedFileUrl = getClass().getResource("/" + SqlCatalogConfig.DEFAULT_OUTPUT_PATH + SqlCatalogConfig.DEFAULT_OUTPUT_FILE_NAME);
        assertNotNull(generatedFileUrl);

        String generatedFile = Files.readString(Paths.get(generatedFileUrl.toURI()));

        // java class column
        assertTrue(generatedFile.contains("ExampleRepository"));

        // method identity column
        assertTrue(generatedFile.contains("findById"));
        assertTrue(generatedFile.contains("findByName"));

        // jpql text column
        assertTrue(generatedFile.contains("SELECT o From Object o WHERE o.id = :id"));
        assertTrue(generatedFile.contains("SELECT o From Object o WHERE o.name = :name"));

        // comment column
        assertTrue(generatedFile.contains("EXAMPLE-ID"));
    }
}
