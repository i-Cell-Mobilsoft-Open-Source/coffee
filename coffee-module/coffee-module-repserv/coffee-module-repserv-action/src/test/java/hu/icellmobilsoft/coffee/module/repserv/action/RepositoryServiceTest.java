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
package hu.icellmobilsoft.coffee.module.repserv.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.module.repserv.action.config.RepositoryServiceConfig;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ExampleService;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ExampleServiceImpl;
import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;
import hu.icellmobilsoft.coffee.se.api.exception.JsonConversionException;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;

/**
 * {@link RepositoryService} file generation test
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
class RepositoryServiceTest {

    @Test
    @DisplayName("generated implementation should exists")
    void generatedImplementationShouldExists() {
        assertTrue(ExampleService.class.isAssignableFrom(ExampleServiceImpl.class));
    }

    @Test
    @DisplayName("generated json should contain classData properties")
    void generatedJsonShouldContainClassDataProperties() throws URISyntaxException, IOException, JsonConversionException {
        URL generatedFileUrl = getClass().getResource("/" + RepositoryServiceConfig.DEFAULT_GENERATED_JSON_PATH + ExampleService.class.getSimpleName() + ".json");
        assertNotNull(generatedFileUrl);

        String json = Files.readString(Paths.get(generatedFileUrl.toURI()));
        ClassData classData = JsonUtil.toObject(json, ClassData.class);

        assertEquals("hu.icellmobilsoft.coffee.module.repserv.action.data.ExampleService", classData.getClassName());
        assertEquals(4, classData.getMethodDataList().size());

        assertEquals("655e9c0c", classData.getMethodDataList().get(0).getId());
        assertEquals("SELECT t FROM Test t\nWHERE t.param = :param\nAND t.p = :p\n", classData.getMethodDataList().get(0).getJpql());
        assertEquals("test", classData.getMethodDataList().get(0).getMethodName());

        assertEquals("1145f274", classData.getMethodDataList().get(1).getId());
        assertEquals("SELECT count(t) FROM Test t\nWHERE t.param1 = :param1\nAND t.param2 = :param2\n", classData.getMethodDataList().get(1).getJpql());
        assertEquals("getBigDecimal", classData.getMethodDataList().get(1).getMethodName());

        assertEquals("18ee7cb7", classData.getMethodDataList().get(2).getId());
        assertEquals("SELECT t FROM Test t", classData.getMethodDataList().get(2).getJpql());
        assertEquals("findAll", classData.getMethodDataList().get(2).getMethodName());

        assertEquals("d8a01590", classData.getMethodDataList().get(3).getId());
        assertNull(classData.getMethodDataList().get(3).getJpql());
        assertEquals("findCustom", classData.getMethodDataList().get(3).getMethodName());
    }

}
