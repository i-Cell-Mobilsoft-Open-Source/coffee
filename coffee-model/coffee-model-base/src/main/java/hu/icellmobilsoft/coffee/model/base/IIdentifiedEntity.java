/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.model.base;

import java.io.Serializable;

/**
 * Interface for the identified audit entities. Contains setters, getters for id, version, and for the basic audit fields.
 *
 * @param <ID>
 *            The type of the entity id
 * @author arnold.bucher
 * @since 1.8.1
 */
public interface IIdentifiedEntity<ID extends Serializable> extends IVersionable {

    /**
     * Returns the id of the entity
     *
     * @return the id of the entity
     */
    ID getId();

    /**
     * Sets the id of the entity
     *
     * @param id
     *            the new id
     */
    void setId(ID id);

}
