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

import org.apache.commons.lang3.StringUtils;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.mongodb.codec.time.duration.DurationAsDocumentCodec;
import hu.icellmobilsoft.coffee.module.mongodb.codec.time.xmlgregoriancalendar.XMLGregorianCalendarCodec;

/**
 * MongoDb util class
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class MongoDbUtil {

    /**
     * Default constructor, constructs a new object.
     */
    public MongoDbUtil() {
        super();
    }

    /**
     * Returns mongoDb client. Optimally this should be called internally by this class only.
     *
     * @param uriString
     *            pl.:
     *            "mongodb://login:pass@dev01.icellmobilsoft.hu:27017,dev02.icellmobilsoft.hu:27017/db?replicaSet=icellmobilsoft.dev.mongocluster.db"
     * @return mongo client
     * @throws BaseException
     *             if {@code uriString} param is empty or mongo client is not available
     */
    public static MongoClient getMongoClient(String uriString) throws BaseException {
        if (StringUtils.isBlank(uriString)) {
            throw new InvalidParameterException("uriString is blank!");
        }
        try {
            // https://www.mongodb.com/docs/drivers/java/sync/v4.5/fundamentals/connection/connect/
            return MongoClients.create(uriString);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Failed to create mongo client: " + e.getLocalizedMessage(), e);
        }

    }

    /**
     * Returns mongoDb database with handling exceptions.
     *
     * @param mongoClient
     *            mongo DB client
     * @param databaseName
     *            name of DB to return
     * @return mongo database
     * @throws BaseException
     *             if params are empty or Mongo DB unavailable
     * @see MongoClient#getDatabase(String)
     */
    public static MongoDatabase getDatabase(MongoClient mongoClient, String databaseName) throws BaseException {
        try {
            if (mongoClient == null || StringUtils.isBlank(databaseName)) {
                throw new InvalidParameterException("mongoClient or databaseName is null!");
            }
            return setCodecs(mongoClient.getDatabase(databaseName));
        } catch (IllegalArgumentException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Failed to get mongo database: " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Closes mongo client connection.
     *
     * @param mongoClient
     *            mongo DB client
     * @throws BaseException
     *             if any exception occurs during close
     * @see MongoClient#close()
     */
    public static void close(MongoClient mongoClient) throws BaseException {
        if (mongoClient != null) {
            try {
                mongoClient.close();
            } catch (Exception e) {
                throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Failed to close mongoDb client: " + e.getLocalizedMessage(), e);
            }
        }
    }

    /**
     * Sets codecs for mongoDB, pay attention to registration order (first default, then custom, last base pojo).
     *
     * @param database
     *            mongo DB to set codecs for
     * @return mongo BD
     */
    private static MongoDatabase setCodecs(MongoDatabase database) {
        return database.withCodecRegistry(
                CodecRegistries.fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(), // default codecs
                        CodecRegistries.fromCodecs(new DurationAsDocumentCodec(), new XMLGregorianCalendarCodec()), // custom codecs
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))); // base pojo codecs
    }
}
