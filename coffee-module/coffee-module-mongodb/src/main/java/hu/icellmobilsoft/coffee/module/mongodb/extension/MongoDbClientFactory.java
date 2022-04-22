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
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.StringUtil;

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
    private Logger log;

    @Inject
    private MongoClientContainer mongoClientContainer;

    @Inject
    private CodecRegistry mongoCodecRegistry;

    /**
     * Producer method for creating MongoDbClient instance
     * 
     * @param injectionPoint
     *            Provides access to metadata about MongoDbClient injection.
     * @return MongoDbClient
     * @throws BaseException
     *             If MongoClient cannot be created.
     */
    @Produces
    @MongoClientConfiguration(configKey = "")
    @Dependent
    public MongoDbClient produceMongoDbClient(InjectionPoint injectionPoint) throws BaseException {
        Optional<MongoClientConfiguration> annotation = AnnotationUtil.getAnnotation(injectionPoint, MongoClientConfiguration.class);
        String configKey = annotation.map(MongoClientConfiguration::configKey).orElse(null);

        // create config helper
        Instance<MongoConfigHelper> configHelperInstance = CDI.current().select(MongoConfigHelper.class);
        MongoConfigHelper mongoConfigHelper = configHelperInstance.get();
        configHelperInstance.destroy(mongoConfigHelper);
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
     * @param mongoConfigHelper
     *            The initialized helper class with mongo connection parameters.
     * @throws BaseException
     *             If MongoClient cannot be created.
     */
    protected synchronized void createMongoClientIfNotExist(MongoConfigHelper mongoConfigHelper) throws BaseException {
        if (!mongoClientContainer.getClientMap().containsKey(mongoConfigHelper.getConfigKey())) {
            // create client
            mongoClientContainer.put(mongoConfigHelper.getConfigKey(), createMongoClient(mongoConfigHelper));
        }
    }

    /**
     * Creates mongoClient.
     * 
     * @return mongo client
     * @throws BaseException
     *             if any exception occurs during creation
     */
    private MongoClient createMongoClient(MongoConfigHelper mongoConfigHelper) throws BaseException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("MongoDB uri [{0}]", StringUtil.maskUriAuthenticationCredentials(mongoConfigHelper.getUri()));
            }

            Block<ConnectionPoolSettings.Builder> connectionPoolSettings = builder -> builder.maxSize(mongoConfigHelper.getConnectionsPerHost())
                    .minSize(mongoConfigHelper.getMinConnectionsPerHost())
                    .maxConnectionIdleTime(mongoConfigHelper.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS)
                    .maxConnectionLifeTime(mongoConfigHelper.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS);

            Block<ClusterSettings.Builder> clusterSettings = builder -> builder.serverSelectionTimeout(mongoConfigHelper.getServerSelectionTimeout(),
                    TimeUnit.MILLISECONDS);

            Block<ServerSettings.Builder> serverSettings = builder -> builder
                    .minHeartbeatFrequency(mongoConfigHelper.getMinHeartbeatFrequency(), TimeUnit.MILLISECONDS)
                    .heartbeatFrequency(mongoConfigHelper.getHeartbeatFrequency(), TimeUnit.MILLISECONDS);

            Block<SocketSettings.Builder> socketSettings = builder -> builder
                    .connectTimeout(mongoConfigHelper.getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(mongoConfigHelper.getSocketTimeout(), TimeUnit.MILLISECONDS);

            // heartbeatConnectTimeout and heartbeatSocketTimeout are set internally in the builder with the socket settings values
            MongoClientSettings.Builder clientSettingsBuilder = MongoClientSettings.builder().applyToClusterSettings(clusterSettings)
                    .applyConnectionString(new ConnectionString(mongoConfigHelper.getUri())).applyToConnectionPoolSettings(connectionPoolSettings)
                    .applyToServerSettings(serverSettings).applyToSocketSettings(socketSettings);

            return MongoClients.create(clientSettingsBuilder.build());
        } catch (Exception e) {
            throw new MongoException(CoffeeFaultType.OPERATION_FAILED,
                    MessageFormat.format("Failed to create mongo client: [{0}]", e.getLocalizedMessage()), e);
        }
    }

    /**
     * Closes every mongo connection in the client container
     */
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
