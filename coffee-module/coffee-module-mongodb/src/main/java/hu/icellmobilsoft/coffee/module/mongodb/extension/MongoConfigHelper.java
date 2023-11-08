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

import java.util.StringJoiner;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;

/**
 * 
 * Helper class for mongodb config parameters
 * 
 * General pattern is "{@code coffee.mongodb.${configKey}.${setting}}
 * 
 * ie.:
 *
 * <pre>
 *  coffee:
 *    mongodb:
 *      xmlapi:
 *        database: xmlapi
 *        uri: mongodb://xmlapi:xmlapi@sample-sandbox-d001.icellmobilsoft.hu:27017/xmlapi?ssl=false
 *        connectionsPerHost: 150
 *        minConnectionsPerHost: 1
 *        connectTimeout: 10000
 *        serverSelectionTimeout: 5000
 *        socketTimeout: 0
 *        maxConnectionIdleTime: 20000
 *        maxConnectionLifeTime: 20000
 *        heartbeatFrequency: 20000
 *        minHeartbeatFrequency: 500
 * </pre>
 * 
 * @author czenczl
 * @since 1.1.0
 * 
 */
@Dependent
public class MongoConfigHelper {

    /**
     * Mongo db konfig key prefix
     */
    public static final String CONFIG_PREFIX = "coffee.mongodb";

    /**
     * Database name
     */
    public static final String DATA_BASE_KEY = "database";
    /**
     * Server uri
     */
    public static final String URI_KEY = "uri";
    /**
     * MongoDB client {@value} config
     */
    public static final String SOCKET_TIMEOUT_KEY = "socketTimeout";
    /**
     * MongoDB client {@value} config
     */
    public static final String MAX_CONNECTION_IDLE_TIME_KEY = "maxConnectionIdleTime";
    /**
     * MongoDB client {@value} config
     */
    public static final String MAX_CONNECTION_LIFE_TIME_KEY = "maxConnectionLifeTime";
    /**
     * MongoDB client {@value} config
     */
    public static final String CONNECTIONS_PER_HOST_KEY = "connectionsPerHost";
    /**
     * MongoDB client {@value} config
     */
    public static final String CONNECT_TIMEOUT_KEY = "connectTimeout";
    /**
     * MongoDB client {@value} config
     */
    public static final String HEARTBEAT_FREQUENCY_KEY = "heartbeatFrequency";
    /**
     * MongoDB client {@value} config
     */
    public static final String MIN_CONNECTIONS_PER_HOST_KEY = "minConnectionsPerHost";
    /**
     * MongoDB client {@value} config
     */
    public static final String MIN_HEART_BEAT_FREQUENCY_KEY = "minHeartbeatFrequency";
    /**
     * MongoDB client {@value} config
     */
    public static final String SERVER_SELECTION_TIMEOUT_KEY = "serverSelectionTimeout";

    private static final Integer DEFAULT_SOCKET_TIMEOUT = 0;
    private static final Integer DEFAULT_MAX_CONNECTION_IDLE_TIME = 20000;
    private static final Integer DEFAULT_MAX_CONNECTION_LIFE_TIME = 20000;
    private static final Integer DEFAULT_CONNECTIONS_PER_HOST = 100;
    private static final Integer DEFAULT_CONNECT_TIMEOUT = 10000;
    private static final Integer DEFAULT_HEARTBEAT_FREQUENCY = 10000;
    private static final Integer DEFAULT_MIN_CONNECTIONS_PER_HOST = 0;
    private static final Integer DEFAULT_MIN_HEART_BEAT_FREQUENCY = 500;
    private static final Integer DEFAULT_SERVER_SELECTION_TIMEOUT = 5000;

    private String configKey;

    @Inject
    private Config config;

    /**
     * Default constructor, constructs a new object.
     */
    public MongoConfigHelper() {
        super();
    }

    /**
     * MongoDb full URI, example: "mongodb://user:password@localhost:27017/samplet?ssl=false"
     *
     * @return uri MongoDatabase uri
     */
    public String getUri() {
        return config.getOptionalValue(concatConfigKey(URI_KEY), String.class).orElse("mongodb://user:password@localhost:27017/samplet?ssl=false");
    }

    /**
     * Selected database in MongoDB, example: "user_request_response"
     * 
     * @return database Selected MongoDatabase
     */
    public String getDatabase() {
        return config.getOptionalValue(concatConfigKey(DATA_BASE_KEY), String.class).orElse("default");
    }

    /**
     * <p>
     * The socket timeout in milliseconds. It is used for I/O socket read and write operations {@link java.net.Socket#setSoTimeout(int)}
     * </p>
     *
     * @return socketTimeout
     */
    public Integer getSocketTimeout() {
        return config.getOptionalValue(concatConfigKey(SOCKET_TIMEOUT_KEY), Integer.class).orElse(DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * The maximum idle time of a pooled connection. A zero value indicates no limit to the idle time. A pooled connection that has exceeded its idle
     * time will be closed and replaced when necessary by a new connection.
     * 
     * @return maxConnectionIdleTime
     */
    public Integer getMaxConnectionIdleTime() {
        return config.getOptionalValue(concatConfigKey(MAX_CONNECTION_IDLE_TIME_KEY), Integer.class).orElse(DEFAULT_MAX_CONNECTION_IDLE_TIME);
    }

    /**
     * The maximum life time of a pooled connection. A zero value indicates no limit to the life time. A pooled connection that has exceeded its life
     * time will be closed and replaced when necessary by a new connection.
     *
     * @return maxConnectionLifeTime
     */
    public Integer getMaxConnectionLifeTime() {
        return config.getOptionalValue(concatConfigKey(MAX_CONNECTION_LIFE_TIME_KEY), Integer.class).orElse(DEFAULT_MAX_CONNECTION_LIFE_TIME);
    }

    /**
     * <p>
     * The maximum number of connections allowed per host for this MongoClient instance. Those connections will be kept in a pool when idle. Once the
     * pool is exhausted, any operation requiring a connection will block waiting for an available connection.
     * </p>
     * 
     * @return connectionsPerHost
     */
    public Integer getConnectionsPerHost() {
        return config.getOptionalValue(concatConfigKey(CONNECTIONS_PER_HOST_KEY), Integer.class).orElse(DEFAULT_CONNECTIONS_PER_HOST);
    }

    /**
     * <p>
     * The connection timeout in milliseconds. A value of 0 means no timeout. It is used solely when establishing a new connection
     * {@link java.net.Socket#connect(java.net.SocketAddress, int) }
     * </p>
     *
     * @return connectTimeout
     */
    public Integer getConnectTimeout() {
        return config.getOptionalValue(concatConfigKey(CONNECT_TIMEOUT_KEY), Integer.class).orElse(DEFAULT_CONNECT_TIMEOUT);
    }

    /**
     * Gets the heartbeat frequency. This is the frequency that the driver will attempt to determine the current state of each server in the cluster.
     *
     * @return heartbeatFrequency
     */
    public Integer getHeartbeatFrequency() {
        return config.getOptionalValue(concatConfigKey(HEARTBEAT_FREQUENCY_KEY), Integer.class).orElse(DEFAULT_HEARTBEAT_FREQUENCY);
    }


    /**
     * <p>
     * The minimum number of connections per host for this MongoClient instance. Those connections will be kept in a pool when idle, and the pool will
     * ensure over time that it contains at least this minimum number.
     * </p>
     *
     * @return minConnectionsPerHost
     */
    public Integer getMinConnectionsPerHost() {
        return config.getOptionalValue(concatConfigKey(MIN_CONNECTIONS_PER_HOST_KEY), Integer.class).orElse(DEFAULT_MIN_CONNECTIONS_PER_HOST);
    }

    /**
     * Gets the minimum heartbeat frequency. In the event that the driver has to frequently re-check a server's availability, it will wait at least
     * this long since the previous check to avoid wasted effort.
     *
     * @return minHeartbeatFrequency
     */
    public Integer getMinHeartbeatFrequency() {
        return config.getOptionalValue(concatConfigKey(MIN_HEART_BEAT_FREQUENCY_KEY), Integer.class).orElse(DEFAULT_MIN_HEART_BEAT_FREQUENCY);
    }

    /**
     * <p>
     * Gets the server selection timeout in milliseconds, which defines how long the driver will wait for server selection to succeed before throwing
     * an exception.
     * </p>
     *
     * <p>
     * A value of 0 means that it will timeout immediately if no server is available. A negative value means to wait indefinitely.
     * </p>
     * 
     * @return serverSelectionTimeout
     */
    public Integer getServerSelectionTimeout() {
        return config.getOptionalValue(concatConfigKey(SERVER_SELECTION_TIMEOUT_KEY), Integer.class).orElse(DEFAULT_SERVER_SELECTION_TIMEOUT);
    }

    /**
     * Returns the current configKey value
     * @return the current configKey value
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Sets the new configKey value
     * @param configKey new configKey value
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private String concatConfigKey(String key) {
        return new StringJoiner(".").add(CONFIG_PREFIX).add(configKey).add(key).toString();
    }

}
