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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * History táblák primáris kulcsa
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Embeddable
public class HistoryPk implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Elő rekord id
     */
    @Column(name = "X__ID", nullable = false, length = 30)
    @NotNull
    @Size(max = 30)
    private String id;

    /**
     * History rekord létrehozásának ideje
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS", nullable = false, length = 7)
    @NotNull
    private Date ts;

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

    /**
     * <p>Getter for the field <code>ts</code>.</p>
     */
    public Date getTs() {
        return ts;
    }

    /**
     * <p>Setter for the field <code>ts</code>.</p>
     */
    public void setTs(Date ts) {
        this.ts = ts;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((ts == null) ? 0 : ts.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        HistoryPk other = (HistoryPk) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (ts == null) {
            if (other.ts != null)
                return false;
        } else if (!ts.equals(other.ts))
            return false;
        return true;
    }
}
