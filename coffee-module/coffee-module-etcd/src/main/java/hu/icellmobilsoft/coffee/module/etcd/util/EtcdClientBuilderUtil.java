/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.etcd.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import jakarta.enterprise.inject.Vetoed;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;

/**
 * {@link ClientBuilder} creater util class
 * 
 * @author speter555
 * @since 1.13.0
 */
@Vetoed
public class EtcdClientBuilderUtil {

    /**
     * Private constructor because of it is an util class
     */
    private EtcdClientBuilderUtil() {
    }

    private static final Logger logger = Logger.getLogger(EtcdClientBuilderUtil.class);

    /**
     * Timeout param to establish connection with the etcd server, this timeout needs to be the base timeout param when getting value from the server.
     */
    public static final long CONNECT_TIMEOUT_MILLIS = 500;

    /**
     *
     * Create {@link ClientBuilder} with urls with came from parameter
     *
     * @param urls
     *            etcd service urls
     * @return {@link ClientBuilder} instance
     * @throws BaseException
     *             if exception occurs at create {@link ClientBuilder}
     */
    public static ClientBuilder getClientBuilder(String[] urls) throws BaseException {
        ClientBuilder etcdClientBuilder = null;
        try {
            etcdClientBuilder = Client.builder()
                    // endpoints
                    .endpoints(urls)
                    // Connect timeout
                    .connectTimeout(Duration.ofMillis(CONNECT_TIMEOUT_MILLIS)) // default null
                    // retryDelay
                    .retryDelay(500) // default 500
                    // retryMaxDelay
                    .retryMaxDelay(2500) // default 2500
                    // keepaliveTime
                    .keepaliveTime(Duration.ofSeconds(30L)) // default 30 sec
                    // keepaliveTimeout
                    .keepaliveTimeout(Duration.ofSeconds(10L)) // default 10 sec
                    // keepaliveWithoutCalls
                    .keepaliveWithoutCalls(true) // default true
                    // retryChronoUnit
                    .retryChronoUnit(ChronoUnit.MILLIS) // default ChronoUnit.MILLIS
                    // retryMaxDuration
                    .retryMaxDuration(Duration.ofSeconds(10)) // default null
                    // waitForReady
                    .waitForReady(true) // default true
            ;
        } catch (Exception e) {
            logger.error("Problems trying to get the Etcd client builder.", e);
            throw new BaseException("Problems trying to get the Etcd client builder.", e);
        }
        return etcdClientBuilder;
    }
}
