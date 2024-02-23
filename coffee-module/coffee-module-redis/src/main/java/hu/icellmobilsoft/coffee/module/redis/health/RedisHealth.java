/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redis.health;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import hu.icellmobilsoft.coffee.cdi.health.constants.HealthConstant;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.module.redis.config.ManagedRedisConfig;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.health.HealthUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * To support microprofile-health mechanics, this class can check whether the redis is reachable within a given timeout.
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
@ApplicationScoped
public class RedisHealth {

    /**
     * Default constructor, constructs a new object.
     */
    public RedisHealth() {
        super();
    }

    /**
     * Checking whether the redis is reachable
     * 
     * @param managedRedisConfig
     *            redis configuration
     * @param builderName
     *            the name of the health check response
     * @return The created {@link HealthCheckResponse} contains information about whether the redis server is reachable.
     * @throws BaseException
     *             if wrong config provided
     */
    public HealthCheckResponse checkConnection(ManagedRedisConfig managedRedisConfig, String builderName) throws BaseException {
        return checkConnection(managedRedisConfig, builderName, null);
    }

    /**
     * Checking whether the redis is reachable
     * 
     * @param managedRedisConfig
     *            redis configuration
     * @param builderName
     *            the name of the health check response
     * @param nodeId
     *            unique identifier for health check
     * @return The created {@link HealthCheckResponse} contains information about whether the redis server is reachable.
     * @throws BaseException
     *             if wrong config provided
     */
    public HealthCheckResponse checkConnection(ManagedRedisConfig managedRedisConfig, String builderName, String nodeId) throws BaseException {
        if (managedRedisConfig == null) {
            throw new InvalidParameterException("ManagedRedisConfig is null!");
        }
        if (StringUtils.isBlank(builderName)) {
            throw new InvalidParameterException("builderName is mandatory!");
        }
        HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name(builderName);
        builder.withData(HealthConstant.Common.NODE_NAME, HealthUtil.getNodeId(nodeId));
        builder.withData(HealthConstant.Common.URL, createUrl(managedRedisConfig));

        String host = managedRedisConfig.getHost();
        int port = managedRedisConfig.getPort();
        int database = managedRedisConfig.getDatabase();

        // we shouldnt wait more than 1 sec, because the probe will get timout before the check finishes
        int timeout = (int) TimeUnit.MILLISECONDS.convert(HealthConstant.Common.DEFAULT_CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        try (JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, managedRedisConfig.getPassword(), database);
                Jedis jedis = jedisPool.getResource()) {
            jedis.ping();
            builder.up();
            return builder.build();
        } catch (Exception e) {
            Logger.getLogger(RedisHealth.class)
                    .error(
                            MessageFormat.format(
                                    "Error occurred while establishing connection! configKey [{0}], message: [{1}]",
                                    managedRedisConfig.getConfigKey(),
                                    e.getLocalizedMessage()),
                            e);
            builder.down();
            return builder.build();
        }
    }

    /**
     * concat url from {@link ManagedRedisConfig}
     *
     * @param managedRedisConfig
     *            redis configuration
     * @return url constructed from redis config
     */
    public String createUrl(ManagedRedisConfig managedRedisConfig) {
        if (managedRedisConfig == null) {
            throw new IllegalArgumentException("ManagedRedisConfig is null!");
        }
        return managedRedisConfig.getHost() + ":" + managedRedisConfig.getPort() + "/" + managedRedisConfig.getDatabase();
    }

}
