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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
     * init MongoRepository collection
     * 
     * @param mongoCollection
     */
    public void initRepositoryCollection(String collection) {
        mongoService.initRepositoryCollection(mongoDatabase.getCollection(collection, BasicDBObject.class));
    }

    /**
     * get selected mongodatabase
     * 
     * @return
     */
    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * set mongo database
     * 
     * @param mongoDatabase
     */
    public void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    /**
     * <p>
     * insertOne.
     * </p>
     */
    public void insertOne(BasicDBObject document) throws BaseException {
        mongoService.insertOne(document);
    }

    /**
     * <p>
     * findFirst.
     * </p>
     */
    public BasicDBObject findFirst(Bson filter) throws BaseException {
        return mongoService.findFirst(filter);
    }

    /**
     * <p>
     * findById.
     * </p>
     */
    public BasicDBObject findById(String mongoId) throws BaseException {
        return mongoService.findById(mongoId);
    }

}
