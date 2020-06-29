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

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * Factory class to produce @MongoDbClient
 * 
 * @author czenczl
 * @since 1.1.0
 *
 */
@ApplicationScoped
public class MongoDbClientFactory {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private MongoClientContainer mongoClientContainer;

    @Inject
    private CodecRegistry mongoCodecRegistry;

    /**
     * Producer method for creating MongoDbClient instance
     * 
     * @param injectionPoint
     * @return
     * @throws BaseException
     */
    @Produces
    @MongoClientConfiguration(configKey = "")
    @Dependent
    public MongoDbClient produceMongoDbClient(InjectionPoint injectionPoint) throws BaseException {
        Optional<MongoClientConfiguration> annotation = AnnotationUtil.getAnnotation(injectionPoint, MongoClientConfiguration.class);
        String configKey = annotation.map(MongoClientConfiguration::configKey).orElse(null);

        // create config helper
        MongoConfigHelper mongoConfigHelper = CDI.current().select(MongoConfigHelper.class).get();
        mongoConfigHelper.setConfigKey(configKey);

        // check if client already exist, synchronized will prevent connection leak
        Map<String, MongoClient> clientMap = mongoClientContainer.getClientMap();
        if (!clientMap.containsKey(configKey)) {
            createMongoClientIfNotExist(mongoConfigHelper);
        }

        Instance<MongoDbClient> mongoDbClientInstance = CDI.current().select(MongoDbClient.class);
        MongoDbClient mongoDbClient = mongoDbClientInstance.get();

        // set database and codecs
        MongoClient mongoClient = clientMap.get(configKey);
        MongoDatabase database = mongoClient.getDatabase(mongoConfigHelper.getDatabase());
        mongoDbClient.setMongoDatabase(database.withCodecRegistry(mongoCodecRegistry));
        return mongoDbClient;
    }

    /**
     * Create mongoClient and put in the container if not exist with the given configKey, thread safe
     * 
     * @param configKey
     * @throws BaseException
     */
    protected synchronized void createMongoClientIfNotExist(MongoConfigHelper mongoConfigHelper) throws BaseException {
        if (!mongoClientContainer.getClientMap().containsKey(mongoConfigHelper.getConfigKey())) {
            // create client
            mongoClientContainer.put(mongoConfigHelper.getConfigKey(), createMongoClient(mongoConfigHelper));
        }
    }

    /**
     * Create mongoClient
     * 
     * @return
     * @throws BaseException
     */
    private MongoClient createMongoClient(MongoConfigHelper mongoConfigHelper) throws BaseException {
        try {
            log.info("MongoDB uri [{0}]", mongoConfigHelper.getUri());

            MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
            builder.maxConnectionIdleTime(mongoConfigHelper.getMaxConnectionIdleTime());
            builder.maxConnectionLifeTime(mongoConfigHelper.getMaxConnectionLifeTime());
            builder.connectionsPerHost(mongoConfigHelper.getConnectionsPerHost());
            builder.connectTimeout(mongoConfigHelper.getConnectTimeout());
            builder.heartbeatConnectTimeout(mongoConfigHelper.getHeartbeatConnectTimeout());
            builder.heartbeatFrequency(mongoConfigHelper.getHeartbeatFrequency());
            builder.heartbeatSocketTimeout(mongoConfigHelper.getHeartbeatSocketTimeout());
            builder.minConnectionsPerHost(mongoConfigHelper.getMinConnectionsPerHost());
            builder.minHeartbeatFrequency(mongoConfigHelper.getMinHeartbeatFrequency());
            builder.socketTimeout(mongoConfigHelper.getSocketTimeout());
            builder.serverSelectionTimeout(mongoConfigHelper.getServerSelectionTimeout());

            MongoClientURI uri = new MongoClientURI(mongoConfigHelper.getUri(), builder);
            return new MongoClient(uri);
        } catch (Exception e) {
            throw new MongoException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format("Failed to create mongo client: [{0}]", e.getLocalizedMessage()), e);
        }
    }

    @PreDestroy
    public void disconnectMongo() {
        if (mongoClientContainer.getClientMap().isEmpty()) {
            return;
        }
        // disconnects from mongo
        for (Entry<String, MongoClient> entry : mongoClientContainer.getClientMap().entrySet()) {
            entry.getValue().close();
            log.debug("closed mongoClient: [{0}]", entry.getKey());
        }
    }

}
