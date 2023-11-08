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
package hu.icellmobilsoft.coffee.module.mongodb.extension;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.mongodb.annotation.MongoServiceBaseQualifier;
import hu.icellmobilsoft.coffee.module.mongodb.service.MongoService;

/**
 * Class for handle @MongoDatabase and default MongoService functionality
 * 
 * @author czenczl
 * @since 1.1.0
 *
 */
@Dependent
public class MongoDbClient {

    private MongoDatabase mongoDatabase;

    @Inject
    @MongoServiceBaseQualifier
    private MongoService<BasicDBObject> mongoService;

    /**
     * Default constructor, constructs a new object.
     */
    public MongoDbClient() {
        super();
    }

    /**
     * init MongoRepository collection
     * 
     * @param collection
     *            The selected mongo collection
     */
    public void initRepositoryCollection(String collection) {
        mongoService.initRepositoryCollection(mongoDatabase.getCollection(collection, BasicDBObject.class));
    }

    /**
     * get selected mongodatabase
     * 
     * @return mongoDatabase
     */
    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * set mongo database
     * 
     * @param mongoDatabase
     *            set the selected MongoDatabase
     */
    public void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    /**
     * <p>
     * insertOne.
     * </p>
     * 
     * @param document
     *            BasicDBObject to insert
     * @throws BaseException
     *             If insert fail.
     */
    public void insertOne(BasicDBObject document) throws BaseException {
        mongoService.insertOne(document);
    }

    /**
     * <p>
     * findFirst.
     * </p>
     * 
     * @param filter
     *            mongo select filter
     * @return BasicDBObject
     * @throws BaseException
     *             Missing filter, or mongo select failed.
     */
    public BasicDBObject findFirst(Bson filter) throws BaseException {
        return mongoService.findFirst(filter);
    }

    /**
     * <p>
     * findById.
     * </p>
     * 
     * @param mongoId
     *            Mongo document object id
     * @return BasicDBObject
     * @throws BaseException
     *             Mongo select failed.
     */
    public BasicDBObject findById(String mongoId) throws BaseException {
        return mongoService.findById(mongoId);
    }

}
