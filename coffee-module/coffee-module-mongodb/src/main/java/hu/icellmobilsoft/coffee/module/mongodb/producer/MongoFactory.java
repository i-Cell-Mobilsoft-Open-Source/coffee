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

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import com.mongodb.MongoClient;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.mongodb.annotation.MongoConfiguration;
import hu.icellmobilsoft.coffee.module.mongodb.config.MongoDbConfig;
import hu.icellmobilsoft.coffee.module.mongodb.handler.MongoDbHandler;
import hu.icellmobilsoft.coffee.module.mongodb.handler.MongoDbUtil;

/**
 * MongoDbHandler producer class
 *
 * @author imre.scheffer
 * @since 1.0.0
 * 
 * @deprecated Use {@link MongoDbClientFactory} instead, forRemoval = true, since = "1.1.0"
 */
@ApplicationScoped
@Deprecated(forRemoval = true, since = "1.1.0")
public class MongoFactory {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(MongoFactory.class);

    private Map<String, MongoClient> mongoClientMap = new HashMap<>();

    /**
     * <p>
     * produceMongoDbHandler.
     * </p>
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
     * <p>
     * dispose.
     * </p>
     */
    public void dispose(@Disposes @MongoConfiguration(urlKey = "", databaseKey = "") MongoDbHandler mongoDbHandler) {
        try {
            CDI.current().destroy(mongoDbHandler);
        } catch (Exception e) {
            LOGGER.error("Error in MongoDbHandler destroy: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * <p>
     * getMongoClient.
     * </p>
     */
    public MongoClient getMongoClient(String uri) throws BaseException {
        if (StringUtils.isBlank(uri)) {
            throw new BaseException("uri is null!");
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
                LOGGER.debugv("closed: [{0}]", clientString);
            } catch (BaseException e) {
                LOGGER.error(e.getLocalizedMessage());
            }
        }
    }
}
