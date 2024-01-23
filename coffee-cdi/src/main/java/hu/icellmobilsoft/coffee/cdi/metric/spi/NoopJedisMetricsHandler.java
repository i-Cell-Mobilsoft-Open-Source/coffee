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
package hu.icellmobilsoft.coffee.cdi.metric.spi;

import java.util.function.Supplier;

/**
 * Empty no operation metrics handler for tests or disabled metric function
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 */
public class NoopJedisMetricsHandler implements IJedisMetricsHandler {

    /**
     * Default constructor, constructs a new object.
     */
    public NoopJedisMetricsHandler() {
        super();
    }

    @Override
    public void addMetric(String configKey, String poolConfigKey, Supplier<Number> activeConnectionSupplier,
            Supplier<Number> idleConnectionSupplier) {
    }

}
