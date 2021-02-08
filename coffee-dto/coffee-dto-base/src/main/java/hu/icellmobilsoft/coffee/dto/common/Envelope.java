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
package hu.icellmobilsoft.coffee.dto.common;

import java.io.Serializable;

/**
 * Envelope class for object
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class Envelope<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String json;

    private Class<E> typeOfJson;

    /**
     * Constructor for Envelope.
     */
    public Envelope() {
    }

    /**
     * Constructor for Envelope.
     * 
     * @param json
     *            json
     * @param typeOfJson
     *            typeOfJson
     */
    public Envelope(final String json, final Class<E> typeOfJson) {
        this.json = json;
        this.typeOfJson = typeOfJson;
    }

    /**
     * Getter for the field <code>json</code>.
     * 
     * @return json
     */
    public String getJson() {
        return json;
    }

    /**
     * <p>
     * Setter for the field <code>json</code>.
     * </p>
     * 
     * @param json
     *            json
     */
    public void setJson(String json) {
        this.json = json;
    }

    /**
     * Getter for the field <code>typeOfJson</code>.
     * 
     * @return typeOfJson
     */
    public Class<E> getTypeOfJson() {
        return typeOfJson;
    }

    /**
     * Setter for the field <code>typeOfJson</code>.
     * 
     * @param typeOfJson
     *            typeOfJson
     */
    public void setTypeOfJson(Class<E> typeOfJson) {
        this.typeOfJson = typeOfJson;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((json == null) ? 0 : json.hashCode());
        result = prime * result + ((typeOfJson == null) ? 0 : typeOfJson.hashCode());
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
        @SuppressWarnings("rawtypes")
        Envelope other = (Envelope) obj;
        if (json == null) {
            if (other.json != null)
                return false;
        } else if (!json.equals(other.json))
            return false;
        if (typeOfJson == null) {
            if (other.typeOfJson != null)
                return false;
        } else if (!typeOfJson.equals(other.typeOfJson))
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Envelope [json=" + json + ", typeOfJson=" + typeOfJson + "]";
    }
}
