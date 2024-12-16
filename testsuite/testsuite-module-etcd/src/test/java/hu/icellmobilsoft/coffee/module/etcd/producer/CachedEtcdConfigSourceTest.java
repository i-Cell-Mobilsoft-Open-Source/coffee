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
 * CachedEtcdConfigSource tests
 *
 * @author gyengus
 */
@DisplayName("Testing CachedEtcdConfigSource")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CachedEtcdConfigSourceTest extends BaseEtcdTest {

    static final String TEST_KEY = "TEST_KEY_" + RandomUtil.generateId();
    static final String TEST_KEY2 = "TEST_KEY2_" + RandomUtil.generateId();
    static final String TEST_VALUE = "TEST_VALUE_" + RandomUtil.generateId();

    @Inject
    private ConfigEtcdHandler configEtcdHandler;

    @BeforeAll
    static void beforeAll() {
        defaultBeforeAll();
        System.setProperty("CachedEtcdConfigSource.enabled", "true");
    }

    @AfterAll
    static void afterAll() {
        defaultAfterAll();
        System.setProperty("CachedEtcdConfigSource.enabled", "false");
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
        configEtcdHandler.putValue(TEST_KEY2, TEST_VALUE);
        CachedEtcdConfigSource.setActive(true);

        // WHEN
        String actual = ConfigProvider.getConfig().getOptionalValue(TEST_KEY2, String.class).orElse(NO_VALUE);

        // THEN
        Assertions.assertEquals(TEST_VALUE, actual);
    }
}
