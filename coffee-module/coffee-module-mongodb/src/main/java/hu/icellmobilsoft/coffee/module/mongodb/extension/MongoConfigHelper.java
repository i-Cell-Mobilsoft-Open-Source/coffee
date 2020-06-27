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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

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
 *    mongo:
 *      xmlapi:
 *        database: icon_xmlapi
 *        uri: mongodb://icon_xmlapi:icon_xmlapi@hubphq-icon-sandbox-d001.icellmobilsoft.hu:27017/icon_xmlapi?ssl=false
 *        connectionsPerHost: 150
 *        minConnectionsPerHost: 1
 *        connectTimeout: 10000
 *        serverSelectionTimeout: 5000
 *        socketTimeout: 0
 *        maxConnectionIdleTime: 20000
 *        maxConnectionLifeTime: 20000
 *        heartbeatConnectTimeout: 20000
 *        heartbeatFrequency: 20000
 *        heartbeatSocketTimeout: 20000
 *        minHeartbeatFrequency: 500
 * </pre>
 * 
 * @author czenczl
 *
 */
@Dependent
public class MongoConfigHelper {

    public static final String CONFIG_PREFIX = "coffee.mongodb";

    public static final String DATA_BASE_KEY = "database";
    public static final String URI_KEY = "uri";
    public static final String SOCKET_TIMEOUT_KEY = "socketTimeout";
    public static final String MAX_CONNECTION_IDLE_TIME = "maxConnectionIdleTime";
    public static final String MAX_CONNECTION_LIFE_TIME = "maxConnectionLifeTime";
    public static final String CONNECTIONS_PER_HOST = "connectionsPerHost";
    public static final String CONNECT_TIMEOUT = "connectTimeout";
    public static final String HEARTBEAT_CONNECT_TIMEOUT = "heartbeatConnectTimeout";
    public static final String HEARTBEAT_FREQUENCY = "heartbeatFrequency";
    public static final String HEARTBEAT_SOCKETT_IMEOUT = "heartbeatSocketTimeout";
    public static final String MIN_CONNECTIONS_PER_HOST = "minConnectionsPerHost";
    public static final String MIN_HEART_BEAT_FREQUENCY = "minHeartbeatFrequency";
    public static final String SERVER_SELECTION_TIMEOUT = "serverSelectionTimeout";

    public String configKey;

    @Inject
    private Config config;

    /**
     * MongoDb full URI, example: "mongodb://user:password@localhost:27017/icon_invoice_request?ssl=false"
     *
     * @param configKey
     * @return
     */
    public String getUri() {
        return config.getOptionalValue(concatConfigKey(URI_KEY), String.class)
                .orElse("mongodb://user:password@localhost:27017/icon_invoice_request?ssl=false");
    }

    /**
     * Selected database in MongoDB, example: "user_request_response"
     * 
     * @param configKey
     * @return
     */
    public String getDatabase() {
        return config.getOptionalValue(concatConfigKey(DATA_BASE_KEY), String.class).orElse("default");
    }

    /**
     * <p>
     * The socket timeout in milliseconds. It is used for I/O socket read and write operations {@link java.net.Socket#setSoTimeout(int)}
     * </p>
     *
     * @param configKey
     * @return
     */
    public Integer getSocketTimeout() {
        return config.getOptionalValue(concatConfigKey(SOCKET_TIMEOUT_KEY), Integer.class).orElse(0);
    }

    /**
     * The maximum idle time of a pooled connection. A zero value indicates no limit to the idle time. A pooled connection that has exceeded its idle
     * time will be closed and replaced when necessary by a new connection.
     * 
     * @param configKey
     * @return
     */
    public Integer getMaxConnectionIdleTime() {
        return config.getOptionalValue(concatConfigKey(MAX_CONNECTION_IDLE_TIME), Integer.class).orElse(20000);
    }

    /**
     * The maximum life time of a pooled connection. A zero value indicates no limit to the life time. A pooled connection that has exceeded its life
     * time will be closed and replaced when necessary by a new connection.
     *
     * @param configKey
     * @return
     */
    public Integer getMaxConnectionLifeTime() {
        return config.getOptionalValue(concatConfigKey(MAX_CONNECTION_LIFE_TIME), Integer.class).orElse(20000);
    }

    /**
     * The maximum number of connections allowed per host for this MongoClient instance. Those connections will be kept in a pool when idle. Once the
     * pool is exhausted, any operation requiring a connection will block waiting for an available connection.
     * </p>
     * 
     * @param configKey
     * @return
     */
    public Integer getConnectionsPerHost() {
        return config.getOptionalValue(concatConfigKey(CONNECTIONS_PER_HOST), Integer.class).orElse(100);
    }

    /**
     * <p>
     * The connection timeout in milliseconds. A value of 0 means no timeout. It is used solely when establishing a new connection
     * {@link java.net.Socket#connect(java.net.SocketAddress, int) }
     * </p>
     *
     * @param configKey
     * @return
     */
    public Integer getConnectTimeout() {
        return config.getOptionalValue(concatConfigKey(CONNECT_TIMEOUT), Integer.class).orElse(10000);
    }

    /**
     * <p>
     * Gets the connect timeout for connections used for the cluster heartbeat.
     * </p>
     *
     * @param configKey
     * @return
     */
    public Integer getHeartbeatConnectTimeout() {
        return config.getOptionalValue(concatConfigKey(HEARTBEAT_CONNECT_TIMEOUT), Integer.class).orElse(20000);
    }

    /**
     * Gets the heartbeat frequency. This is the frequency that the driver will attempt to determine the current state of each server in the cluster.
     *
     * @param configKey
     * @return
     */
    public Integer getHeartbeatFrequency() {
        return config.getOptionalValue(concatConfigKey(HEARTBEAT_FREQUENCY), Integer.class).orElse(10000);
    }

    /**
     * Gets the socket timeout for connections used for the cluster heartbeat.
     *
     * @param configKey
     * @return
     */
    public Integer getHeartbeatSocketTimeout() {
        return config.getOptionalValue(concatConfigKey(HEARTBEAT_SOCKETT_IMEOUT), Integer.class).orElse(20000);
    }

    /**
     * <p>
     * The minimum number of connections per host for this MongoClient instance. Those connections will be kept in a pool when idle, and the pool will
     * ensure over time that it contains at least this minimum number.
     * </p>
     *
     * @param configKey
     * @return
     */
    public Integer getMinConnectionsPerHost() {
        return config.getOptionalValue(concatConfigKey(MIN_CONNECTIONS_PER_HOST), Integer.class).orElse(0);
    }

    /**
     * Gets the minimum heartbeat frequency. In the event that the driver has to frequently re-check a server's availability, it will wait at least
     * this long since the previous check to avoid wasted effort.
     *
     * @param configKey
     * @return
     */
    public Integer getMinHeartbeatFrequency() {
        return config.getOptionalValue(concatConfigKey(MIN_HEART_BEAT_FREQUENCY), Integer.class).orElse(500);
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
     */
    public Integer getServerSelectionTimeout() {
        return config.getOptionalValue(concatConfigKey(SERVER_SELECTION_TIMEOUT), Integer.class).orElse(5000);
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    private String concatConfigKey(String key) {
        return new StringJoiner(".").add(CONFIG_PREFIX).add(configKey).add(key).toString();
    }

}
