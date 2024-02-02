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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.etcd.handler.ConfigEtcdHandler;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;

/**
 * RuntimeEtcdConfigSource tests
 * 
 * @author Imre Scheffer
 * @since 2.6.0
 */
@DisplayName("Testing FilteredEtcdConfigSource")
public class FilteredEtcdConfigSourceTest extends BaseEtcdTest {

    static final String TEST_KEY_PUBLIC = "public.TEST_KEY_" + RandomUtil.generateId();
    static final String TEST_KEY_PROTECTED = "protected.TEST_KEY_" + RandomUtil.generateId();
    static final String TEST_KEY_PRIVATE = "private.TEST_KEY_" + RandomUtil.generateId();
    static final String TEST_KEY_FALSE = "publicTEST_KEY_" + RandomUtil.generateId();
    static final String TEST_VALUE = "TEST_VALUE_" + RandomUtil.generateId();

    @Inject
    private ConfigEtcdHandler configEtcdHandler;

    @BeforeAll
    public static void beforeAll() {
        defaultBeforeAll();
        System.setProperty("FilteredEtcdConfigSource.enabled", "true");
    }

    @Test
    @DisplayName("non-existent key")
    public void nonExistentKey() throws BaseException {
        assertAllEtcdConfigSource();

        String value = ConfigProvider.getConfig().getOptionalValue(TEST_KEY_PUBLIC + "none", String.class).orElse(NO_VALUE);

        Assertions.assertEquals(NO_VALUE, value);
    }

    @Test
    @DisplayName("existing include pattern key")
    public void existIncludeKey() throws BaseException {
        assertAllEtcdConfigSource();
        configEtcdHandler.putValue(TEST_KEY_PUBLIC, TEST_VALUE);
        configEtcdHandler.putValue(TEST_KEY_PROTECTED, TEST_VALUE);
        configEtcdHandler.putValue(TEST_KEY_FALSE, TEST_VALUE);

        String valuePublic = ConfigProvider.getConfig().getOptionalValue(TEST_KEY_PUBLIC, String.class).orElse(NO_VALUE);
        String valueProtected = ConfigProvider.getConfig().getOptionalValue(TEST_KEY_PROTECTED, String.class).orElse(NO_VALUE);
        String valueFalse = ConfigProvider.getConfig().getOptionalValue(TEST_KEY_FALSE, String.class).orElse(NO_VALUE);

        Assertions.assertEquals(TEST_VALUE, valuePublic);
        Assertions.assertEquals(TEST_VALUE, valueProtected);
        Assertions.assertEquals(NO_VALUE, valueFalse);
    }

    @Test
    @DisplayName("existing exclude pattern key")
    public void existExcludeKey() throws BaseException {
        assertAllEtcdConfigSource();
        configEtcdHandler.putValue(TEST_KEY_PRIVATE, TEST_VALUE);

        String valuePrivate = ConfigProvider.getConfig().getOptionalValue(TEST_KEY_PRIVATE, String.class).orElse(NO_VALUE);

        Assertions.assertEquals(NO_VALUE, valuePrivate);
    }
}
