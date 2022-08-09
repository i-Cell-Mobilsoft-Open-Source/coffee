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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

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

/**
 * Test for Redis config class
 * 
 * @author imre.scheffer
 * @since 1.11.0
 *
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Redis config tests")
class RedisConfigTest {

    static final String CONFIG_KEY = "test";
    static final String CONFIG_KEY_YML = "yamlconfig";
    static final String POOL_CONFIG_KEY_CUSTOM1 = "custom1";

    @Inject
    @RedisConnection(configKey = CONFIG_KEY)
    private ManagedRedisConfig redisConfig;

    @Inject
    @RedisConnection(configKey = CONFIG_KEY_YML)
    private ManagedRedisConfig ymlDefaultPoolRedisConfig;

    @Inject
    @RedisConnection(configKey = CONFIG_KEY_YML, poolConfigKey = POOL_CONFIG_KEY_CUSTOM1)
    private ManagedRedisConfig ymlCustom1PoolRedisConfig;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // beans.xml scan
            .enableDiscovery())
            // start request scope + build
            .activate(RequestScoped.class).build();

    @Test
    @DisplayName("default pool test")
    void defaultValues() {
        Assertions.assertEquals(CONFIG_KEY, redisConfig.getConfigKey());
        Assertions.assertEquals(0, redisConfig.getDatabase());
        Assertions.assertEquals("localhost", redisConfig.getHost());
        Assertions.assertNull(redisConfig.getPassword());
        Assertions.assertEquals("default", redisConfig.getPoolConfigKey());
        Assertions.assertEquals(16, redisConfig.getPoolMaxIdle());
        Assertions.assertEquals(64, redisConfig.getPoolMaxTotal());
        Assertions.assertEquals(6380, redisConfig.getPort());
        Assertions.assertEquals(5000, redisConfig.getTimeout());
    }

    @Test
    @DisplayName("yml default pool test")
    void yamlDefaultPoolValues() {
        Assertions.assertEquals(CONFIG_KEY_YML, ymlDefaultPoolRedisConfig.getConfigKey());
        Assertions.assertEquals(1, ymlDefaultPoolRedisConfig.getDatabase());
        Assertions.assertEquals("sample.icellmobilsoft.hu", ymlDefaultPoolRedisConfig.getHost());
        Assertions.assertEquals("secret", ymlDefaultPoolRedisConfig.getPassword());
        Assertions.assertEquals("default", ymlDefaultPoolRedisConfig.getPoolConfigKey());
        Assertions.assertEquals(2, ymlDefaultPoolRedisConfig.getPoolMaxIdle());
        Assertions.assertEquals(1, ymlDefaultPoolRedisConfig.getPoolMaxTotal());
        Assertions.assertEquals(6381, ymlDefaultPoolRedisConfig.getPort());
        Assertions.assertEquals(6000, ymlDefaultPoolRedisConfig.getTimeout());
    }

    @Test
    @DisplayName("yml custom1 pool test")
    void yamlCustom1PoolValues() {
        Assertions.assertEquals(CONFIG_KEY_YML, ymlCustom1PoolRedisConfig.getConfigKey());
        Assertions.assertEquals(1, ymlCustom1PoolRedisConfig.getDatabase());
        Assertions.assertEquals("sample.icellmobilsoft.hu", ymlCustom1PoolRedisConfig.getHost());
        Assertions.assertEquals("secret", ymlCustom1PoolRedisConfig.getPassword());
        Assertions.assertEquals(POOL_CONFIG_KEY_CUSTOM1, ymlCustom1PoolRedisConfig.getPoolConfigKey());
        Assertions.assertEquals(4, ymlCustom1PoolRedisConfig.getPoolMaxIdle());
        Assertions.assertEquals(3, ymlCustom1PoolRedisConfig.getPoolMaxTotal());
        Assertions.assertEquals(6381, ymlCustom1PoolRedisConfig.getPort());
        Assertions.assertEquals(6000, ymlCustom1PoolRedisConfig.getTimeout());
    }
}
