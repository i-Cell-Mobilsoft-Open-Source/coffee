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
package hu.icellmobilsoft.coffee.model.base.history;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import hu.icellmobilsoft.coffee.model.base.AbstractAuditEntity;
import hu.icellmobilsoft.coffee.model.base.history.enums.State;

/**
 * Abstract entity class for history tables
 *
 * @author czenczl
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
@MappedSuperclass
public abstract class AbstractHistoryEntity extends AbstractAuditEntity<String> {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "X__STATE")
    private State state;

    @Id
    @Embedded
    private HistoryPk historyPk;

    /**
     * <p>Getter for the field <code>historyPk</code>.</p>
     */
    public HistoryPk getHistoryPk() {
        return historyPk;
    }

    /**
     * <p>Setter for the field <code>historyPk</code>.</p>
     */
    public void setHistoryPk(HistoryPk historyPk) {
        this.historyPk = historyPk;
    }

    /**
     * <p>Getter for the field <code>state</code>.</p>
     */
    public State getState() {
        return state;
    }

    /**
     * <p>Setter for the field <code>state</code>.</p>
     */
    public void setState(State state) {
        this.state = state;
    }
}
