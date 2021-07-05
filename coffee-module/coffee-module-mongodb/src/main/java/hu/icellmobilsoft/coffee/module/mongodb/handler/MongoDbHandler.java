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
package hu.icellmobilsoft.coffee.module.mongodb.handler;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.mongodb.config.MongoDbConfig;
import hu.icellmobilsoft.coffee.module.mongodb.producer.MongoFactory;
import hu.icellmobilsoft.coffee.module.mongodb.service.MongoServiceImpl;

/**
 * MongoDb handler class
 *
 * @author imre.scheffer
 * @since 1.0.0
 * 
 * @deprecated Use {@link hu.icellmobilsoft.coffee.module.mongodb.extension.MongoDbClient} instead, forRemoval = true, since = "1.1.0"
 */
@Dependent
@Deprecated(forRemoval = true, since = "1.1.0")
public class MongoDbHandler {

    private MongoDbConfig mongoDbConfig;

    @Inject
    private MongoFactory mongoFactory;

    private MongoDatabase mongoDatabase;

    private MongoServiceImpl mongoService;

    /**
     * Setter for the field {@code mongoDbConfig}.
     *
     * @param mongoDbConfig
     *            mongoDbConfig to set
     */
    public void setMongoDbConfig(MongoDbConfig mongoDbConfig) {
        this.mongoDbConfig = mongoDbConfig;
    }

    /**
     * Returns mongoDb client. Optimally this should be called internally by this class only.
     *
     * @return mongo client
     * @throws BaseException
     *             if mongo DB config is empty or mongo client is not available
     */
    public MongoClient getMongoClient() throws BaseException {
        if (mongoDbConfig == null) {
            throw new TechnicalException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, "mongoDbConfig is not set!");
        }
        return mongoFactory.getMongoClient(mongoDbConfig.getUri());
    }

    /**
     * Disposes the managed {@link #mongoService} field
     */
    @PreDestroy
    public void dispose() {
        if (mongoService != null) {
            CDI.current().destroy(mongoService);
        }
    }

    /**
     * Getter for the field {@code mongoDatabase}. If it is null, creates a new one.
     *
     * @return Mongo DB
     * @throws BaseException
     *             if Mongo DB unavailable
     */
    public MongoDatabase getDatabase() throws BaseException {
        if (mongoDatabase == null) {
            mongoDatabase = MongoDbUtil.getDatabase(getMongoClient(), mongoDbConfig.getDatabase());
        }
        return mongoDatabase;
    }

    /**
     * Sets the field {@code mongoDatabase} by given database name.
     *
     * @param databaseName
     *            name of the database
     * @throws BaseException
     *             if {@code databaseName} param is empty or Mongo DB unavailable
     */
    public void setDatabase(String databaseName) throws BaseException {
        if (StringUtils.isBlank(databaseName)) {
            throw new TechnicalException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, "databaseName is blank!");
        }
        mongoDatabase = MongoDbUtil.getDatabase(getMongoClient(), databaseName);
    }

    /**
     * CDI getter for the field {@code mongoService}.
     *
     * @return {@code mongoService}
     * @throws BaseException
     *             exception
     */
    public MongoServiceImpl getMongoService() throws BaseException {
        if (mongoService == null) {
            mongoService = CDI.current().select(MongoServiceImpl.class).get();
        }
        return mongoService;
    }

    /**
     * Sets mongo collection.
     *
     * @param collection
     *            collection to set
     * @throws BaseException
     *             exception
     */
    public void setCollection(String collection) throws BaseException {
        getMongoService().setMongoCollection(getDatabase().getCollection(collection, BasicDBObject.class));
    }

    /**
     * Finds first object in the collection matching given query filter.
     *
     * @param filter
     *            query filter
     * @return found object
     * @throws BaseException
     *             if mongo select fails
     * @see hu.icellmobilsoft.coffee.module.mongodb.service.MongoService#findFirst(Bson)
     */
    public BasicDBObject findFirst(Bson filter) throws BaseException {
        return getMongoService().findFirst(filter);
    }

    /**
     * Finds object by given mongo id.
     *
     * @param mongoId
     *            mongoId to insert
     * @return found object
     * @throws BaseException
     *             if mongo select fails
     * @see hu.icellmobilsoft.coffee.module.mongodb.service.MongoService#findById(String)
     */
    public BasicDBObject findById(String mongoId) throws BaseException {
        return getMongoService().findById(mongoId);
    }

    /**
     * Inserts the provided document.
     *
     * @param document
     *            document to insert
     * @throws BaseException
     *             if mongo insert fails
     * @see hu.icellmobilsoft.coffee.module.mongodb.service.MongoService#insertOne(Object)
     */
    public void insertOne(BasicDBObject document) throws BaseException {
        getMongoService().insertOne(document);
    }

}
