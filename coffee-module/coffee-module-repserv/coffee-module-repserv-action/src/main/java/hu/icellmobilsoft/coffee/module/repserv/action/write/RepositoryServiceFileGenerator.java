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
package hu.icellmobilsoft.coffee.module.repserv.action.write;

import java.io.IOException;

import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;

/**
 * Interface defining file generation behavior for classes annotated with {@link RepositoryService}. Implementations of this interface handle creating
 * source or resource files based on collected metadata.
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public interface RepositoryServiceFileGenerator extends AutoCloseable {

    /**
     * Generates all necessary files for a {@link RepositoryService}-annotated class.
     *
     * @throws IOException
     *             if an I/O error occurs during file generation
     */
    void generate() throws IOException;
}
