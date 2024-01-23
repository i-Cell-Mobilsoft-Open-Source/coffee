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
package hu.icellmobilsoft.coffee.module.mp.metrics.test;

import jakarta.inject.Inject;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import hu.icellmobilsoft.coffee.cdi.metric.spi.IJedisMetricsHandler;

/**
 * Testing Custom Microprofile Metrics producer CDI resolver
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("CustomJedisMpMetricsHandler producer tests")
class CustomJedisMpMetricsHandlerTests {

    @Inject
    private IJedisMetricsHandler jedisMetricsHandler;

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.from(WeldInitiator.createWeld()
            // added cdi test classes
            .addBeanClass(MockMetricRegistryProducer.class).addBeanClass(CustomJedisMpMetricsHandler.class)
            // alternative
            .addAlternative(CustomJedisMpMetricsHandler.class)
            // beans.xml scan
            .enableDiscovery())
            // start request scope + build
            .build();

    @Test
    @DisplayName("MP Metrics handler test")
    void mpMetricsHandler() {
        Assertions.assertNotNull(jedisMetricsHandler);

        jedisMetricsHandler.addMetric("key1", "key2", () -> 1L, () -> 2);

        Assertions.assertInstanceOf(WeldClientProxy.class, jedisMetricsHandler);
        Object instance = ((WeldClientProxy) jedisMetricsHandler).getMetadata().getContextualInstance();
        // must be JedisMpMetricsHandler
        Assertions.assertInstanceOf(CustomJedisMpMetricsHandler.class, instance);
    }

}
