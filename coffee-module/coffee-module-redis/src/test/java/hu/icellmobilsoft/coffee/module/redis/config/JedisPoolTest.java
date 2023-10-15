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
package hu.icellmobilsoft.coffee.module.redis.config;

import static org.mockito.Mockito.mock;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import redis.clients.jedis.JedisPool;

/**
 * Test for Redis pool config producer
 * 
 * @author imre.scheffer
 * @since 1.11.0
 *
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Redis pool config tests")
class JedisPoolTest {

    static final String CONFIG_KEY = "test";
    static final String CONFIG_KEY_YML = "yamlconfig";
    static final String POOL_CONFIG_KEY_CUSTOM1 = "custom1";

    @Inject
    @RedisConnection(configKey = CONFIG_KEY)
    private JedisPool jedisPool;

    @Inject
    @RedisConnection(configKey = CONFIG_KEY_YML)
    private JedisPool ymlDefaultJedisPool;

    @Inject
    @RedisConnection(configKey = CONFIG_KEY_YML, poolConfigKey = POOL_CONFIG_KEY_CUSTOM1)
    private JedisPool ymlCustom1PoolJedisPool;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(
            WeldInitiator.createWeld()
                    // beans.xml scan
                    .enableDiscovery())
            .addBeans(MockBean.of(mock(MetricRegistry.class), MetricRegistry.class))
            // start request scope + build
            .activate(RequestScoped.class)
            .build();

    @Test
    @DisplayName("default pool test")
    void defaultValues() {
        // tobbet sajnos nem tudunk kiszedni belole
        // de azert a producer loggol infokat
        Assertions.assertEquals(16, jedisPool.getMaxIdle());
        Assertions.assertEquals(64, jedisPool.getMaxTotal());
    }

    @Test
    @DisplayName("yml default pool test")
    void yamlDefaultPoolValues() {
        Assertions.assertEquals(2, ymlDefaultJedisPool.getMaxIdle());
        Assertions.assertEquals(1, ymlDefaultJedisPool.getMaxTotal());
    }

    @Test
    @DisplayName("yml custom1 pool test")
    void yamlCustom1PoolValues() {
        Assertions.assertEquals(4, ymlCustom1PoolJedisPool.getMaxIdle());
        Assertions.assertEquals(3, ymlCustom1PoolJedisPool.getMaxTotal());
    }
}
