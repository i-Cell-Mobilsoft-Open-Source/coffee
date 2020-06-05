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
package hu.icellmobilsoft.coffee.module.mongodb.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;

/**
 * <p>MongoRepository class.</p>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class MongoRepository<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private MongoCollection<T> mongoCollection;

    /**
     * <p>findFirst.</p>
     */
    public T findFirst(Bson filter) {
        return findFirst(filter, null);
    }

    /**
     * <p>findFirst.</p>
     */
    public T findFirst(Bson filter, Bson order) {
        return mongoCollection.find(filter).sort(order).first();
    }

    /**
     * <p>findAll.</p>
     */
    public List<T> findAll(Bson filter) {
        return mongoCollection.find(filter).into(new ArrayList<>());
    }

    /**
     * <p>insertOne.</p>
     */
    public void insertOne(T document) {
        mongoCollection.insertOne(document);
    }

    /**
     * <p>insertMany.</p>
     */
    public void insertMany(List<T> documents) {
        mongoCollection.insertMany(documents);
    }

    /**
     * <p>find.</p>
     */
    public List<T> find(Bson filter, Bson order, int rows, int page, Class<T> clazz) {
        int skips = rows * (page - 1);
        return mongoCollection.find(filter, clazz).sort(order).skip(skips).limit(rows).into(new ArrayList<>());
    }

    /**
     * <p>count.</p>
     */
    public long count(Bson filter) {
        return mongoCollection.countDocuments(filter);
    }

    /**
     * <p>Getter for the field <code>mongoCollection</code>.</p>
     */
    public MongoCollection<T> getMongoCollection() {
        return mongoCollection;
    }

    /**
     * <p>Setter for the field <code>mongoCollection</code>.</p>
     */
    public void setMongoCollection(MongoCollection<T> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }
}
