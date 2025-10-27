/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.etcd.producer;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import hu.icellmobilsoft.coffee.module.etcd.handler.ConfigEtcdHandler;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * RuntimeFilteredEtcdConfigSource tests
 *
 * @author gyengus
 */
@DisplayName("Testing RuntimeFilteredEtcdConfigSource")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RuntimeFilteredEtcdConfigSourceTest extends BaseEtcdTest {

    static final String TEST_KEY = "public.TEST_KEY_" + RandomUtil.generateId();
    static final String TEST_KEY2 = "public.TEST_KEY2_" + RandomUtil.generateId();
    static final String TEST_VALUE = "public.TEST_VALUE_" + RandomUtil.generateId();

    @Inject
    private ConfigEtcdHandler configEtcdHandler;

    @BeforeAll
    static void beforeAll() {
        defaultBeforeAll();
        System.setProperty("RuntimeFilteredEtcdConfigSource.enabled", "true");
    }

    @AfterAll
    static void afterAll() {
        defaultAfterAll();
        System.setProperty("RuntimeFilteredEtcdConfigSource.enabled", "false");
    }

    @Test
    @DisplayName("non-existent key")
    @Order(100)
    void testNonExistentKey() {
        // GIVEN
        assertAllEtcdConfigSource();

        // WHEN
        String actual = ConfigProvider.getConfig().getOptionalValue(TEST_KEY + "_none", String.class).orElse(NO_VALUE);

        // THEN
        Assertions.assertEquals(NO_VALUE, actual);
    }

    @Test
    @DisplayName("existing key not active")
    @Order(200)
    void testExistingKeyNotActive() throws BaseException {
        // GIVEN
        assertAllEtcdConfigSource();
        configEtcdHandler.putValue(TEST_KEY, TEST_VALUE);

        // WHEN
        String actual = ConfigProvider.getConfig().getOptionalValue(TEST_KEY, String.class).orElse(NO_VALUE);

        // THEN
        Assertions.assertEquals(NO_VALUE, actual);
    }

    @Test
    @DisplayName("existing key active")
    @Order(300)
    void testExistingKeyActive() throws BaseException {
        // GIVEN
        assertAllEtcdConfigSource();
        RuntimeFilteredEtcdConfigSource.setActive(true);
        configEtcdHandler.putValue(TEST_KEY2, TEST_VALUE);

        // WHEN
        String actual = ConfigProvider.getConfig().getOptionalValue(TEST_KEY2, String.class).orElse(NO_VALUE);

        // THEN
        Assertions.assertEquals(TEST_VALUE, actual);
    }
}
