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

import static org.mockito.Mockito.mock;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

/**
 * Mandatory CDI producer
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 */
@ApplicationScoped
public class MockMetricRegistryProducer {

    @Produces
    @RegistryType(type = Type.VENDOR)
    public MetricRegistry vendorRegistry() {
        return mock(MetricRegistry.class);
    }

    @Produces
    public MetricRegistry defaultRegistry() {
        return mock(MetricRegistry.class);
    }
}
