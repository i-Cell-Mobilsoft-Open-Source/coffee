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
package hu.icellmobilsoft.coffee.model.base.javatime;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * Base class for id + audited (X__ID, X__INSDATE, X__MODDATE, X__INSUSER, X__MODUSER) entities.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@MappedSuperclass
public abstract class AbstractIdentifiedAuditEntity extends AbstractAuditEntity<String> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "X__ID", length = 30)
    @GenericGenerator(name = "javatime-entity-id-generator", strategy = "hu.icellmobilsoft.coffee.model.base.generator.EntityIdGenerator")
    @GeneratedValue(generator = "javatime-entity-id-generator", strategy = GenerationType.IDENTITY)
    private String id;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     */
    public void setId(String id) {
        this.id = id;
    }
}
