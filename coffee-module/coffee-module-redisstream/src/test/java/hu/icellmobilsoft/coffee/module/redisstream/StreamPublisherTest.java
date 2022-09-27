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
package hu.icellmobilsoft.coffee.module.redisstream;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamProducer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamGroupConfig;
import hu.icellmobilsoft.coffee.module.redisstream.publisher.RedisStreamPublisher;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import hu.icellmobilsoft.coffee.module.redisstream.service.RedisStreamService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import redis.clients.jedis.resps.StreamEntry;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

/**
 * Test for RedisStream Publisher class
 *
 * @author peter.kovacs
 * @since 1.11.0
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Redis Publisher tests")
@Testcontainers
class StreamPublisherTest {

    public static final String STREAMCONFIG_1 = "streamconfig1";
    @Inject
    @RedisStreamProducer(group = STREAMCONFIG_1)
    private RedisStreamPublisher publisher1;
    @Inject
    @RedisStreamProducer(group = "streamconfig2")
    private RedisStreamPublisher publisher2;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // beans.xml scan
            .enableDiscovery())
            // start request scope + build
            .activate(RequestScoped.class).build();

    @Inject
    private RedisStreamService redisStreamService;

    @Inject
    @RedisConnection(configKey = "redis1")
    private RedisManager redisManager;

    @Container
    public GenericContainer redis = new GenericContainer<>(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

    @Test
    @DisplayName("redis1 redisstream test")
    void redis1ConfigTest() throws NoSuchFieldException, IllegalAccessException, BaseException {

        Field fieldStreamGroupConfig = publisher1.getClass().getDeclaredField("config");
        fieldStreamGroupConfig.setAccessible(true);
        StreamGroupConfig streamGroupConfig = (StreamGroupConfig) fieldStreamGroupConfig.get(publisher1);

        Assertions.assertEquals(8000, streamGroupConfig.getStreamReadTimeoutMillis());
        Assertions.assertEquals("redis1", streamGroupConfig.getConnectionKey());
        Assertions.assertEquals("default", streamGroupConfig.getProducerPool());
        Assertions.assertEquals(3600000, streamGroupConfig.getProducerTTL().get());
        Assertions.assertEquals(10000, streamGroupConfig.getProducerMaxLen().get());
        Assertions.assertEquals("custom1", streamGroupConfig.getConsumerPool());
        Assertions.assertEquals(2, streamGroupConfig.getRetryCount().get());
        Assertions.assertEquals(2, streamGroupConfig.getConsumerThreadsCount().get());

    }

    @Test
    @DisplayName("redis2 redisstream test: yaml configuration overrides code")
    void redis2Configtest() throws NoSuchFieldException, IllegalAccessException {

        Field fieldStreamGroupConfig = publisher2.getClass().getDeclaredField("config");
        fieldStreamGroupConfig.setAccessible(true);
        StreamGroupConfig streamGroupConfig = (StreamGroupConfig) fieldStreamGroupConfig.get(publisher2);

        Assertions.assertEquals(7000, streamGroupConfig.getStreamReadTimeoutMillis());
        // yaml configuration overrides code
        Assertions.assertEquals("redis3", streamGroupConfig.getConnectionKey());
        Assertions.assertEquals("invalid", streamGroupConfig.getProducerPool());
        Assertions.assertEquals(3200000, streamGroupConfig.getProducerTTL().get());
        Assertions.assertEquals(12000, streamGroupConfig.getProducerMaxLen().get());
        Assertions.assertEquals("custom2", streamGroupConfig.getConsumerPool());
        Assertions.assertEquals(2, streamGroupConfig.getRetryCount().get());
        Assertions.assertEquals(2, streamGroupConfig.getConsumerThreadsCount().get());

    }

    @Test
    @DisplayName("redis1 redisstream test")
    void redisStreamPublishTest() throws BaseException {
        redisStreamService.setGroup(STREAMCONFIG_1);
        redisStreamService.setRedisManager(redisManager);
        String streamMessage = "alma";
        publisher1.publish(STREAMCONFIG_1, streamMessage, Map.ofEntries(Map.entry("extSessionId", "id1")));
        Optional<StreamEntry> streamEntry = Optional.empty();
        redisManager.initConnection();
        redisStreamService.handleGroup();
        streamEntry = redisStreamService.consumeOne("ads");
        Assertions.assertEquals(streamMessage, streamEntry.get().getFields().get("message"));

    }

}
