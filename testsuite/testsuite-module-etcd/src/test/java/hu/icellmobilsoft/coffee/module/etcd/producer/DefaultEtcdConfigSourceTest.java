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
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.module.etcd.handler.ConfigEtcdHandler;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * DefaultEtcdConfigSource tests
 *
 * @author Imre Scheffer
 * @since 2.6.0
 */
@DisplayName("Testing DefaultEtcdConfigSource")
public class DefaultEtcdConfigSourceTest extends BaseEtcdTest {

    static final String TEST_KEY = "TEST_KEY_" + RandomUtil.generateId();
    static final String TEST_VALUE = "TEST_VALUE_" + RandomUtil.generateId();

    @Inject
    private ConfigEtcdHandler configEtcdHandler;

    @BeforeAll
    static void beforeAll() {
        defaultBeforeAll();
        System.setProperty("hu.icellmobilsoft.coffee.module.etcd.producer.DefaultEtcdConfigSource.enabled", "true");
    }

    @AfterAll
    static void afterAll() {
        defaultBeforeAll();
        // The concurrency test must be disabled
        System.setProperty("hu.icellmobilsoft.coffee.module.etcd.producer.DefaultEtcdConfigSource.enabled", "false");
    }

    @Test
    @DisplayName("non-existent key")
    public void nonExistentKey() throws BaseException {
        assertAllEtcdConfigSource();

        String value = ConfigProvider.getConfig().getOptionalValue(TEST_KEY + "none", String.class).orElse(NO_VALUE);

        Assertions.assertEquals(NO_VALUE, value);
    }

    @Test
    @DisplayName("existing key")
    public void existKey() throws BaseException {
        assertAllEtcdConfigSource();
        configEtcdHandler.putValue(TEST_KEY, TEST_VALUE);

        String value = ConfigProvider.getConfig().getOptionalValue(TEST_KEY, String.class).orElse(NO_VALUE);

        Assertions.assertEquals(TEST_VALUE, value);
    }

}
