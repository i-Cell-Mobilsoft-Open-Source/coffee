/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Produces the underlying metrics implementation if exists, otherwise creates a default implementation without metrics handling
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 */
@ApplicationScoped
public class MetricsHandlerProducer {

    /**
     * Default constructor, constructs a new object.
     */
    public MetricsHandlerProducer() {
        super();
    }

    /**
     * Producer method to supply the provided metrics implementation
     * 
     * @return IOpenTraceHandler implementation
     */
    @Produces
    @ApplicationScoped
    public IJedisMetricsHandler produceJedisMetricsHandler() {
        Instance<IJedisMetricsHandler> openTraceHandler = CDI.current().select(IJedisMetricsHandler.class, new MetricsHandlerQualifier.Literal());
        if (openTraceHandler.isResolvable()) {
            return openTraceHandler.get();
        }

        // default implementation
        return new NoopJedisMetricsHandler();
    }

}
