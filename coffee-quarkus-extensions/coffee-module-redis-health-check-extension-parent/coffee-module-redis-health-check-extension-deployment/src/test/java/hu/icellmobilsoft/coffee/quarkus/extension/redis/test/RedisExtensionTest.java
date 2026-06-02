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
