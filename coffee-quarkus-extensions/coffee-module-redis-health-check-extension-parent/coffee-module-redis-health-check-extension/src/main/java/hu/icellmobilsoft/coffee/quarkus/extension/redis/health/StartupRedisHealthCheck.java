/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.quarkus.extension.redis.health;

import static io.quarkus.redis.runtime.client.VertxRedisClientFactory.DEFAULT_CLIENT;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.Bean;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Startup;

import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import io.quarkus.redis.client.RedisClientName;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.runtime.client.config.RedisConfig;
import io.smallrye.mutiny.TimeoutException;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;
import io.vertx.mutiny.redis.client.Response;

/**
 * Health check for Redis connections at startup.
 *
 * @author gabor.balazs
 * @since 2.13.0
 */
@Startup
@ApplicationScoped
public class StartupRedisHealthCheck implements HealthCheck {
    private final Map<String, Redis> clients = new HashMap<>();

    private final RedisConfig config;

    /**
     * Constructor specifically for injecting redis config
     *
     * @param config
     *            redis config
     */
    public StartupRedisHealthCheck(RedisConfig config) {
        this.config = config;
    }

    /**
     * Initializes the client map
     */
    @PostConstruct
    protected void init() {
        for (InstanceHandle<Redis> handle : Arc.container().select(Redis.class, Any.Literal.INSTANCE).handles()) {
            String clientName = getClientName(handle.getBean());
            clients.putIfAbsent(clientName == null ? DEFAULT_CLIENT : clientName, handle.get());
        }

        for (InstanceHandle<ReactiveRedisDataSource> handle : Arc.container()
                .select(ReactiveRedisDataSource.class, Any.Literal.INSTANCE)
                .handles()) {
            String clientName = getClientName(handle.getBean());
            Redis redis = handle.get().getRedis();
            clients.putIfAbsent(clientName == null ? DEFAULT_CLIENT : clientName, redis);
        }

        for (InstanceHandle<RedisDataSource> handle : Arc.container()
                .select(RedisDataSource.class, Any.Literal.INSTANCE)
                .handles()) {
            String clientName = getClientName(handle.getBean());
            Redis redis = handle.get().getReactive().getRedis();
            clients.putIfAbsent(clientName == null ? DEFAULT_CLIENT : clientName, redis);
        }
    }

    private String getClientName(Bean<?> bean) {
        for (Object qualifier : bean.getQualifiers()) {
            if (qualifier instanceof RedisClientName) {
                return ((RedisClientName) qualifier).value();
            }
        }
        return null;
    }

    private Duration getTimeout(String name) {
        if (RedisConfig.isDefaultClient(name)) {
            return config.defaultRedisClient.timeout;
        } else {
            return config.namedRedisClients.get(name).timeout;
        }
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Redis connection health check").up();
        for (Map.Entry<String, Redis> client : clients.entrySet()) {
            try {
                boolean isDefault = DEFAULT_CLIENT.equals(client.getKey());
                Redis redisClient = client.getValue();
                String redisClientName = isDefault ? "default" : client.getKey();
                Duration timeout = getTimeout(client.getKey());
                Response response = redisClient.send(Request.cmd(Command.PING)).await().atMost(timeout);
                builder.up().withData(redisClientName, response.toString());
            } catch (TimeoutException e) {
                return builder.down().withData("reason", "client [" + client.getKey() + "]: timeout").build();
            } catch (Exception e) {
                if (e.getMessage() == null) {
                    return builder.down().withData("reason", "client [" + client.getKey() + "]: " + e).build();
                }
                return builder.down().withData("reason", "client [" + client.getKey() + "]: " + e.getMessage()).build();
            }
        }
        return builder.build();
    }
}
