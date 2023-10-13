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
package hu.icellmobilsoft.coffee.module.etcd.config;

import java.time.temporal.ChronoUnit;

/**
 * ETCD configuration values
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public interface EtcdConfig {

    /**
     * Configuration key for ETCD URL
     */
    String URL_KEY = "coffee.etcd.default.url";

    /**
     * Configuration key for connection timeout
     */
    String CONNECTION_TIMEOUT_KEY = "coffee.etcd.default.connection.timeout.millis";

    /**
     * Configuration key for retry delay
     */
    String RETRY_DELAY = "coffee.etcd.default.retry.delay";

    /**
     * Configuration key for retry max delay
     */
    String RETRY_MAX_DELAY = "coffee.etcd.default.retry.max.delay";

    /**
     * Configuration key for keepalive time
     */
    String KEEPALIVE_TIME = "coffee.etcd.default.keepalive.time.seconds";

    /**
     * Configuration key for keepalive timeout
     */
    String KEEPALIVE_TIMEOUT = "coffee.etcd.default.keepalive.timeout.seconds";

    /**
     * Configuration key for keepalive without calls
     */
    String KEEPALIVE_WITHOUT_CALLS = "coffee.etcd.default.keepalive.without.calls";

    /**
     * Configuration key for retry chrono unit
     */
    String RETRY_CHRONO_UNIT = "coffee.etcd.default.retry.chrono.unit";

    /**
     * Configuration key for retry max duration
     */
    String RETRY_MAX_DURATION = "coffee.etcd.default.retry.max.duration.seconds";

    /**
     * Configuration key for enable gRPC's wait for ready
     */
    String WAIT_FOR_READY = "coffee.etcd.default.wait.for.ready";

    /**
     * ETCD configuration url, example: "http://localhost1:2379,http://localhost2:2379"
     *
     * @return ETCD url
     */
    String[] getUrl();

    /**
     * Timeout param to establish connection with the etcd server, this timeout needs to be the base timeout param when getting value from the server
     *
     * @return connection timeout in millis
     */
    long getConnectionTimeout();

    /**
     * Returns The delay between retries
     *
     * @return the retry delay
     */
    long getRetryDelay();

    /**
     * Returns the max backing off delay between retries
     *
     * @return max retry delay
     */
    long getRetryMaxDelay();

    /**
     * Returns the keep alive time in seconds
     *
     * @return keep alive time
     */
    long getKeepaliveTime();

    /**
     * Returns the keep alive time out in seconds
     *
     * @return keep alive time out
     */
    long getKeepaliveTimeout();

    /**
     * Keepalive option for gRPC
     *
     * @return the gRPC keep alive without calls
     */
    boolean isKeepaliveWithoutCalls();

    /**
     * Returns he retries period unit
     *
     * @return the chrono unit
     */
    ChronoUnit getRetryChronoUnit();

    /**
     * Returns the retries max duration in seconds
     *
     * @return retries max duration
     */
    long getRetryMaxDuration();

    /**
     * Enable gRPC's wait for ready semantics
     *
     * @return if grpc client uses gRPC's wait for ready semantics
     */
    boolean isWaitForReady();
}
