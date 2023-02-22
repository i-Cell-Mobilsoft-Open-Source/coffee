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

import hu.icellmobilsoft.coffee.model.base.annotation.ModifiedBy;
import jakarta.enterprise.inject.Vetoed;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Implementation of AbstractIdentifiedAuditEntity with ModifiedBy onCreate = true
 *
 * @author zsolt.vasi
 * @since 2.0.0
 */
@Vetoed
@Entity
@Table(name = "dummy")
public class ModifiedByOnCreateImplAbstractIdentifiedAuditEntity extends AbstractIdentifiedAuditEntity {

    /**
     * The last modifier user of the entity
     */
    @ModifiedBy(onCreate = true)
    @Column(name = "X__MODUSER", length = 30)
    private String modifierUser;

    @Override
    public String getModifierUser() {
        return modifierUser;
    }

    @Override
    public void setModifierUser(String modifierUser) {
        this.modifierUser = modifierUser;
    }

}
