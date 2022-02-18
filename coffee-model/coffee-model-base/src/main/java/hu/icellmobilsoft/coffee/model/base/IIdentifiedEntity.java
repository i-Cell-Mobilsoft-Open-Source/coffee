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
 * @param <USER>
 *            The type of the creator and modifier user
 * @author arnold.bucher
 * @since 1.8.1
 */
public interface IIdentifiedEntity<ID extends Serializable, USER> extends IVersionable {

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

    /**
     * Returns the creator user of the entity
     *
     * @return the creator user of the entity
     */
    USER getCreatorUser();

    /**
     * Sets the creator user of the entity
     *
     * @param creatorUser
     *            the new creator user
     */
    void setCreatorUser(USER creatorUser);

    /**
     * Returns the modification user of the entity
     *
     * @return the modification user of the entity
     */
    USER getModifierUser();

    /**
     * Sets the modification user of the entity
     *
     * @param modifierUser
     *            the new modification user
     */
    void setModifierUser(USER modifierUser);
}
