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

/**
 * Interface for the identified audit entities. Contains setters, getters for id, version, and for the basic audit fields.
 *
 * @param <DATE> The type of the date. E.g.: {@link java.util.Date}, {@link java.time.OffsetDateTime}, {@link java.time.Instant}
 * @author martin.nagy
 * @since 1.8.0
 */
public interface IIdentifiedAuditEntity<DATE> {

    /**
     * Returns the id of the entity
     *
     * @return the id of the entity
     */
    String getId();

    /**
     * Sets the id of the entity
     *
     * @param id the new id
     */
    void setId(String id);

    /**
     * Returns the version of the entity
     *
     * @return the version of the entity
     */
    long getVersion();

    /**
     * Sets the version of the entity
     *
     * @param version the new version
     */
    void setVersion(long version);

    /**
     * Returns the creation date of the entity
     *
     * @return the creation date of the entity
     */
    DATE getCreationDate();

    /**
     * Sets the creation date of the entity
     *
     * @param creationDate the new creation date
     */
    void setCreationDate(DATE creationDate);

    /**
     * Returns the modification date of the entity
     *
     * @return the modification date of the entity
     */
    DATE getModificationDate();

    /**
     * Sets the modification date of the entity
     *
     * @param modificationDate the new modification date
     */
    void setModificationDate(DATE modificationDate);

    /**
     * Returns the creator user of the entity
     *
     * @return the creator user of the entity
     */
    String getCreatorUser();

    /**
     * Sets the creator user of the entity
     *
     * @param creatorUser the new creator user
     */
    void setCreatorUser(String creatorUser);

    /**
     * Returns the modification user of the entity
     *
     * @return the modification user of the entity
     */
    String getModifierUser();

    /**
     * Sets the modification user of the entity
     *
     * @param modifierUser the new modification user
     */
    void setModifierUser(String modifierUser);
}
