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
package hu.icellmobilsoft.coffee.module.repserv.api;

import jakarta.enterprise.inject.Model;

/**
 * Represents a request-scoped context that stores the generated SQL method ID.
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
@Model
public class SqlContext {

    private String id;

    /**
     * Default constructor.
     */
    public SqlContext() {
        super();
    }

    /**
     * Returns the stored method ID.
     *
     * @return the method ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the method ID.
     *
     * @param id
     *            the method ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Clears the stored ID from the context.
     */
    public void clear() {
        this.id = null;
    }
}
