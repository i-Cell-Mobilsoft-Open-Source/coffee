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

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.module.repserv.action.data.ExampleService;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ExampleServiceImpl;
import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;

/**
 * {@link RepositoryService} file generation test
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
class RepositoryServiceTest {

    @Test
    @DisplayName("generated implementation should exists")
    void generatedFileShouldContainConfigKeys() {
        assertTrue(ExampleService.class.isAssignableFrom(ExampleServiceImpl.class));
    }
}
