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
package hu.icellmobilsoft.coffee.model.base.javatime.history;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import hu.icellmobilsoft.coffee.model.base.history.HistoryPk;
import hu.icellmobilsoft.coffee.model.base.history.enums.State;
import hu.icellmobilsoft.coffee.model.base.javatime.AbstractAuditEntity;

/**
 * Abstract entity class for history tables
 *
 * @author czenczl
 * @author imre.scheffer
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class AbstractHistoryEntity extends AbstractAuditEntity<String> {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractHistoryEntity() {
        super();
    }

    /**
     * State of the entity
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "X__STATE")
    private State state;

    /**
     * Primary key of the entity
     */
    @Id
    @Embedded
    private HistoryPk historyPk;

    /**
     * Getter for the field {@code historyPk}.
     * 
     * @return historyPk
     */
    public HistoryPk getHistoryPk() {
        return historyPk;
    }

    /**
     * Setter for the field {@code historyPk}.
     * 
     * @param historyPk
     *            historyPk
     */
    public void setHistoryPk(HistoryPk historyPk) {
        this.historyPk = historyPk;
    }

    /**
     * Getter for the field {@code state}.
     * 
     * @return state
     */
    public State getState() {
        return state;
    }

    /**
     * Setter for the field {@code state}.
     * 
     * @param state
     *            state
     */
    public void setState(State state) {
        this.state = state;
    }
}
