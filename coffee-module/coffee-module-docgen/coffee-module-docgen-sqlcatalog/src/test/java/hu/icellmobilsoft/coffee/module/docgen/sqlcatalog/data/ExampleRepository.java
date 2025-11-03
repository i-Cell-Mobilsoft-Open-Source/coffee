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
package hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.data;

import jakarta.persistence.QueryHint;

import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;
import org.hibernate.jpa.HibernateHints;

@Repository
public interface ExampleRepository {

    @Query("SELECT o From Object o WHERE o.id = :id")
    Object findById(String id);

    @Query(value = "SELECT o From Object o WHERE o.name = :name", hints = { @QueryHint(name = HibernateHints.HINT_COMMENT, value = "EXAMPLE-ID"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = true + "") })
    Object findByName(String name);
}
