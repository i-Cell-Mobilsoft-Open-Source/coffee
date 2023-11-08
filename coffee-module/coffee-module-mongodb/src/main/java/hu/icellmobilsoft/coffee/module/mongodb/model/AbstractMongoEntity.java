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
package hu.icellmobilsoft.coffee.module.mongodb.model;

import java.io.Serializable;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

/**
 * Mongo ObjectId
 *
 * @author balazs.joo
 * @since 1.0.0
 */
public abstract class AbstractMongoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor, constructs a new object.
     */
    public AbstractMongoEntity() {
        super();
    }

    /**
     * Identifier of the mongo object
     */
    @BsonId
    private ObjectId id;

    /**
     * Getter for the field {@code id}.
     *
     * @return {@code id}
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Setter for the field {@code id}.
     *
     * @param id
     *            id to set
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

}
