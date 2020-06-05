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
 * Main dto for service to service communication
 *
 * @author imre.scheffer
 * @param <E>
 *            entity
 * @param <CONTEXT>
 *            contextType like hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType
 * @since 1.0.0
 */
public class BaseEntityDto<E extends Serializable, CONTEXT extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private CONTEXT context;

    private E entity;

    /**
     * <p>Getter for the field <code>context</code>.</p>
     */
    public CONTEXT getContext() {
        return context;
    }

    /**
     * <p>Setter for the field <code>context</code>.</p>
     */
    public void setContext(CONTEXT context) {
        this.context = context;
    }

    /**
     * <p>Getter for the field <code>entity</code>.</p>
     */
    public E getEntity() {
        return entity;
    }

    /**
     * <p>Setter for the field <code>entity</code>.</p>
     */
    public void setEntity(E entity) {
        this.entity = entity;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseEntityDto [context=");
        builder.append(context);
        builder.append(", entity=");
        builder.append(entity);
        builder.append("]");
        return builder.toString();
    }
}
