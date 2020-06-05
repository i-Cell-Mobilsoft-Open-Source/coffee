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
 */
@Dependent
public class MongoDbHandler {

    private MongoDbConfig mongoDbConfig;

    @Inject
    private MongoFactory mongoFactory;

    private MongoDatabase mongoDatabase;

    private MongoServiceImpl mongoService;

    /**
     * <p>Setter for the field <code>mongoDbConfig</code>.</p>
     */
    public void setMongoDbConfig(MongoDbConfig mongoDbConfig) {
        this.mongoDbConfig = mongoDbConfig;
    }

    /**
     * Getting mongoDb client. Optimally this is called only in internal by this class
     *
     * @param uriString
     *            pl.:
     *            "mongodb://login:pass@dev01.icellmobilsoft.hu:27017,dev02.icellmobilsoft.hu:27017/db?replicaSet=icellmobilsoft.dev.mongocluster.db"
     * @return mongo client
     * @throws BaseException
     */
    public MongoClient getMongoClient() throws BaseException {
        if (mongoDbConfig == null) {
            throw new TechnicalException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, "mongoDbConfig is not set!");
        }
        return mongoFactory.getMongoClient(mongoDbConfig.getUri());
    }

    /**
     * <p>getDatabase.</p>
     */
    public MongoDatabase getDatabase() throws BaseException {
        if (mongoDatabase == null) {
            mongoDatabase = MongoDbUtil.getDatabase(getMongoClient(), mongoDbConfig.getDatabase());
        }
        return mongoDatabase;
    }

    /**
     * <p>setDatabase.</p>
     */
    public void setDatabase(String databaseName) throws BaseException {
        if (StringUtils.isBlank(databaseName)) {
            throw new TechnicalException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, "databaseName is blank!");
        }
        mongoDatabase = MongoDbUtil.getDatabase(getMongoClient(), databaseName);
    }

    /**
     * <p>Getter for the field <code>mongoService</code>.</p>
     */
    public MongoServiceImpl getMongoService() throws BaseException {
        if (mongoService == null) {
            mongoService = CDI.current().select(MongoServiceImpl.class).get();
        }
        return mongoService;
    }

    /**
     * <p>setCollection.</p>
     */
    public void setCollection(String collection) throws BaseException {
        getMongoService().setMongoCollection(getDatabase().getCollection(collection, BasicDBObject.class));
    }

    /**
     * <p>findFirst.</p>
     */
    public BasicDBObject findFirst(Bson filter) throws BaseException {
        return getMongoService().findFirst(filter);
    }

    /**
     * <p>findById.</p>
     */
    public BasicDBObject findById(String mongoId) throws BaseException {
        return getMongoService().findById(mongoId);
    }

    /**
     * <p>insertOne.</p>
     */
    public void insertOne(BasicDBObject document) throws BaseException {
        getMongoService().insertOne(document);
    }

}
