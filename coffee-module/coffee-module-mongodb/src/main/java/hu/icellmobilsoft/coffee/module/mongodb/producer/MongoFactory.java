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
package hu.icellmobilsoft.coffee.module.mongodb.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;

import com.mongodb.client.MongoClient;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.mongodb.annotation.MongoConfiguration;
import hu.icellmobilsoft.coffee.module.mongodb.config.MongoDbConfig;
import hu.icellmobilsoft.coffee.module.mongodb.handler.MongoDbHandler;
import hu.icellmobilsoft.coffee.module.mongodb.handler.MongoDbUtil;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * MongoDbHandler producer class
 *
 * @author imre.scheffer
 * @since 1.0.0
 * 
 * @deprecated Use {@link hu.icellmobilsoft.coffee.module.mongodb.extension.MongoDbClientFactory} instead, forRemoval = true, since = "1.1.0"
 */
@ApplicationScoped
@Deprecated(forRemoval = true, since = "1.1.0")
public class MongoFactory {

    private static Logger LOGGER = Logger.getLogger(MongoFactory.class);

    private Map<String, MongoClient> mongoClientMap = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public MongoFactory() {
        super();
    }

    /**
     * Producer method for {@link MongoDbHandler}.
     *
     * @param injectionPoint
     *            injection point
     * @return created Mongo DB handler
     */
    @Produces
    @MongoConfiguration(urlKey = "", databaseKey = "")
    @Dependent
    public MongoDbHandler produceMongoDbHandler(InjectionPoint injectionPoint) {
        MongoConfiguration annotation = injectionPoint.getAnnotated().getAnnotation(MongoConfiguration.class);
        String urlKey = annotation.urlKey();
        String databaseKey = annotation.databaseKey();

        String urlValue = ConfigProvider.getConfig().getValue(urlKey, String.class);

        String databaseValue = ConfigProvider.getConfig().getValue(databaseKey, String.class);

        Instance<MongoDbHandler> mongoDbHandlerInstance = CDI.current().select(MongoDbHandler.class);
        MongoDbHandler mongoDbHandler = mongoDbHandlerInstance.get();
        mongoDbHandler.setMongoDbConfig(new MongoDbConfig() {

            @Override
            public String getUri() {
                return urlValue;
            }

            @Override
            public String getDatabase() {
                return databaseValue;
            }
        });
        return mongoDbHandler;
    }

    /**
     * Disposes given {@link MongoDbHandler}.
     * 
     * @param mongoDbHandler
     *            Mongo DB handler to dispose.
     */
    public void dispose(@Disposes @MongoConfiguration(urlKey = "", databaseKey = "") MongoDbHandler mongoDbHandler) {
        try {
            CDI.current().destroy(mongoDbHandler);
        } catch (Exception e) {
            LOGGER.error("Error in MongoDbHandler destroy: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Returns Mongo DB client.
     *
     * @param uri
     *            Mongo DB uri
     * @return {@link MongoClient}
     * @throws BaseException
     *             if {@code uri} param is empty or mongo client is not available
     */
    public MongoClient getMongoClient(String uri) throws BaseException {
        if (StringUtils.isBlank(uri)) {
            throw new InvalidParameterException("uri is null!");
        }
        if (!mongoClientMap.containsKey(uri)) {
            MongoClient mongoClient = MongoDbUtil.getMongoClient(uri);
            mongoClientMap.put(uri, mongoClient);
        }
        return mongoClientMap.get(uri);
    }

    /**
     * <p>
     * disconnectMongo.
     * </p>
     */
    @PreDestroy
    public void disconnectMongo() {
        if (mongoClientMap == null || mongoClientMap.isEmpty()) {
            return;
        }
        // disconnects from mongo
        for (Entry<String, MongoClient> entry : mongoClientMap.entrySet()) {
            try {
                String clientString = entry.getValue().toString();
                MongoDbUtil.close(entry.getValue());
                LOGGER.debug("closed: [{0}]", clientString);
            } catch (BaseException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
    }
}
