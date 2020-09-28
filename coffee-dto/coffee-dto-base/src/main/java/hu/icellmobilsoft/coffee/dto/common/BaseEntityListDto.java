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
import java.util.ArrayList;
import java.util.List;

/**
 * Main dto for service to service communication
 *
 * @author imre.scheffer
 * @param <E>
 *            entity type
 * @param <CONTEXT>
 *            contextType like hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType
 * @since 1.0.0
 */
public class BaseEntityListDto<E extends Serializable, CONTEXT extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private CONTEXT context;

    private List<E> entityList;

    /**
     * Getter for the field <code>context</code>.
     * 
     * @return context
     */
    public CONTEXT getContext() {
        return context;
    }

    /**
     * Setter for the field <code>context</code>.
     * 
     * @param context
     *            context
     */
    public void setContext(CONTEXT context) {
        this.context = context;
    }

    /**
     * Getter for the field <code>entityList</code>.
     * 
     * @return entityList
     */
    public List<E> getEntityList() {
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        return entityList;
    }

    /**
     * Setter for the field <code>entityList</code>.
     * 
     * @param entityList
     *            entityList
     */
    public void setEntityList(List<E> entityList) {
        this.entityList = entityList;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseEntityListDto [context=");
        builder.append(context);
        builder.append(", entityList=");
        builder.append(entityList);
        builder.append("]");
        return builder.toString();
    }
}
