/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.model.base.audit;

import hu.icellmobilsoft.coffee.model.base.annotation.CurrentUser;
import hu.icellmobilsoft.coffee.model.base.javatime.AbstractAuditEntity;
import jakarta.enterprise.inject.Produces;

/**
 * Teszt entitások létrehozásához szükséges {@link AbstractAuditEntity#getCreatorUser()} töltéséhez adatot szolgáltató osztály
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
public class UserProvider {

    public static final String DEFAULT_SYSTEM_USER = "0";

    @Produces
    @CurrentUser
    public String currentUser() {
        return DEFAULT_SYSTEM_USER;
    }

}
