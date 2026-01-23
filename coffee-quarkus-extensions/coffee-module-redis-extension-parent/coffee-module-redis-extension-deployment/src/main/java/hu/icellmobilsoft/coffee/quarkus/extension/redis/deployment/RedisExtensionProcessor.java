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
package hu.icellmobilsoft.coffee.quarkus.extension.redis.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import hu.icellmobilsoft.coffee.quarkus.extension.redis.health.StartupRedisHealthCheck;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;

/**
 * Configures the redis health check extension
 *
 * @author gabor.balazs
 * @since 2.13.0
 */
public class RedisExtensionProcessor {

    private static final String FEATURE = "coffee-module-redis-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem createStartupRedisHealthCheck() {
        return AdditionalBeanBuildItem.unremovableOf(StartupRedisHealthCheck.class);
    }
}
