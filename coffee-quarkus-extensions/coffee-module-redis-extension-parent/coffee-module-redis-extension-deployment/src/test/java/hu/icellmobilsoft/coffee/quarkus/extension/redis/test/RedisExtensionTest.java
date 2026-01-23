package hu.icellmobilsoft.coffee.quarkus.extension.redis.test;

import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.quarkus.extension.redis.health.StartupRedisHealthCheck;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Test {@link StartupRedisHealthCheck}
 *
 * @since 2.13.0
 * @author gabor.balazs
 */
@QuarkusTest
class RedisExtensionTest {

    @Test
    void testModule() {
        Assertions.assertTrue(true);
    }

    @Inject
    @Startup
    StartupRedisHealthCheck startupRedisHealthCheck;

    @Test
    void testExtension() {
        Assertions.assertNotNull(startupRedisHealthCheck);
        HealthCheckResponse response = startupRedisHealthCheck.call();
        Assertions.assertNotNull(response);
        Assertions.assertEquals("Redis connection health check", response.getName());
        Assertions.assertTrue(response.getStatus() == HealthCheckResponse.Status.UP || response.getStatus() == HealthCheckResponse.Status.DOWN);
    }
}
