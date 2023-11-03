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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.etcd.config.EtcdConfig;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;

/**
 * {@link ClientBuilder} creater util class
 * 
 * @author speter555
 * @since 1.13.0
 */
public class EtcdClientBuilderUtil {

    /**
     * Private constructor because of it is an util class
     */
    private EtcdClientBuilderUtil() {
    }

    private static final Logger logger = Logger.getLogger(EtcdClientBuilderUtil.class);

    /**
     *
     * Create {@link ClientBuilder} with urls with came from parameter
     *
     * @param etcdConfig
     *            {@link EtcdConfig} ETCD configuration values
     * @return {@link ClientBuilder} instance
     * @throws BaseException
     *             if exception occurs at create {@link ClientBuilder}
     */
    public static ClientBuilder getClientBuilder(EtcdConfig etcdConfig) throws BaseException {
        if (etcdConfig == null) {
            throw new InvalidParameterException("etcdConfig is mandatory!");
        }
        ClientBuilder etcdClientBuilder;
        try {
            etcdClientBuilder = Client.builder()
                    // endpoints
                    .endpoints(etcdConfig.getUrl())
                    // Connect timeout, default 500
                    .connectTimeout(Duration.ofMillis(etcdConfig.getConnectionTimeout()))
                    // retryDelay, default 500
                    .retryDelay(etcdConfig.getRetryDelay())
                    // retryMaxDelay, default 2500
                    .retryMaxDelay(etcdConfig.getRetryMaxDelay())
                    // keepaliveTime, default 30 sec
                    .keepaliveTime(Duration.ofSeconds(etcdConfig.getKeepaliveTime()))
                    // keepaliveTimeout, default 10 sec
                    .keepaliveTimeout(Duration.ofSeconds(etcdConfig.getKeepaliveTimeout()))
                    // keepaliveWithoutCalls, default true
                    .keepaliveWithoutCalls(etcdConfig.isKeepaliveWithoutCalls())
                    // retryChronoUnit, default ChronoUnit.MILLIS
                    .retryChronoUnit(etcdConfig.getRetryChronoUnit())
                    // retryMaxDuration, default 10 sec
                    .retryMaxDuration(Duration.ofSeconds(etcdConfig.getRetryMaxDuration()))
                    // waitForReady, default true
                    .waitForReady(etcdConfig.isWaitForReady());
        } catch (Exception e) {
            logger.error("Problems trying to get the Etcd client builder.", e);
            throw new BaseException("Problems trying to get the Etcd client builder.", e);
        }
        return etcdClientBuilder;
    }
}
