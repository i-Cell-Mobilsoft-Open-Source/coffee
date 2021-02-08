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
 * MongoRepository class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class MongoRepository<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private MongoCollection<T> mongoCollection;

    /**
     * Finds first document in the collection matching given query filter.
     *
     * @param filter
     *            query filter
     * @return found document
     * @see MongoCollection#find(Bson)
     */
    public T findFirst(Bson filter) {
        return findFirst(filter, null);
    }

    /**
     * Finds first document in the collection matching given query filter sorted by given order.
     *
     * @param filter
     *            query filter
     * @param order
     *            sort order
     * @return found document
     * @see MongoCollection#find(Bson)
     */
    public T findFirst(Bson filter, Bson order) {
        return mongoCollection.find(filter).sort(order).first();
    }

    /**
     * Finds all documents in the collection matching given query filter.
     * 
     * @param filter
     *            query filter
     * @return found document {@link List}
     * @see MongoCollection#find(Bson)
     */
    public List<T> findAll(Bson filter) {
        return mongoCollection.find(filter).into(new ArrayList<>());
    }

    /**
     * Inserts the provided document.
     *
     * @param document
     *            document to insert
     * @see MongoCollection#insertOne(Object)
     */
    public void insertOne(T document) {
        mongoCollection.insertOne(document);
    }

    /**
     * Inserts one or more documents.
     *
     * @param documents
     *            documents to insert
     * @see MongoCollection#insertMany(List)
     */
    public void insertMany(List<T> documents) {
        mongoCollection.insertMany(documents);
    }

    /**
     * Finds all documents in the collection.
     *
     * @param filter
     *            query filter
     * @param order
     *            sorting order
     * @param rows
     *            number of rows per page
     * @param page
     *            page index to return
     * @param clazz
     *            class to decode each document into
     * @return found document {@link List}
     * @see MongoCollection#find(Bson, Class)
     */
    public List<T> find(Bson filter, Bson order, int rows, int page, Class<T> clazz) {
        int skips = rows * (page - 1);
        return mongoCollection.find(filter, clazz).sort(order).skip(skips).limit(rows).into(new ArrayList<>());
    }

    /**
     * Counts the number of documents in the collection according to the given options.
     *
     * @param filter
     *            query filter
     * @return number of documents in the collection
     * @see MongoCollection#countDocuments(Bson)
     */
    public long count(Bson filter) {
        return mongoCollection.countDocuments(filter);
    }

    /**
     * Getter for the field {@code mongoCollection}.
     *
     * @return {@code mongoCollection}
     */
    public MongoCollection<T> getMongoCollection() {
        return mongoCollection;
    }

    /**
     * Setter for the field {@code mongoCollection}.
     *
     * @param mongoCollection
     *            mongoCollection to set
     */
    public void setMongoCollection(MongoCollection<T> mongoCollection) {
        this.mongoCollection = mongoCollection;
    }
}
