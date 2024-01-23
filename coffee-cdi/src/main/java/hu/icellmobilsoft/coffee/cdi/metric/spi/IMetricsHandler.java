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

import org.apache.commons.lang3.ClassUtils;

import hu.icellmobilsoft.coffee.cdi.metric.MetricTag;

/**
 * Metric handler interface
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 *
 */
public interface IMetricsHandler {

    /**
     * Search metric information in metric registry. If not foun then
     * 
     * @param name
     *            metric name for search
     * @param tags
     *            metric tags for search
     * @return value of gauge metric. If not found then null.
     */
    Double searchGauge(String name, MetricTag... tags);

    /**
     * Metric implementation enum
     */
    enum Implementation {
        /**
         * Microprofile metrics
         */
        MP_METRIC("org.eclipse.microprofile.metrics.MetricRegistry"),
        /**
         * Micrometer
         */
        MICROMETER("io.micrometer.core.instrument.MeterRegistry"),
        /**
         * Unknown
         */
        UNKNOWN("");

        String className;

        /**
         * Default constructor, constructs a new object.
         * 
         * @param className
         *            implementation class name
         */
        Implementation(String className) {
            this.className = className;
        }

        /**
         * Getter
         * 
         * @return class name of implementation
         */
        String getClassName() {
            return className;
        }
    }

    /**
     * Try to resolve system metric implementation
     * 
     * @return Founded implementation enum or {@link Implementation#UNKNOWN} if not found defined class
     */
    static Implementation getImplementation() {
        try {
            ClassUtils.getClass(Implementation.MICROMETER.getClassName(), false);
            return Implementation.MICROMETER;
        } catch (ClassNotFoundException e1) {
            try {
                ClassUtils.getClass(Implementation.MP_METRIC.getClassName(), false);
                return Implementation.MP_METRIC;
            } catch (ClassNotFoundException e2) {
                return Implementation.UNKNOWN;
            }
        }
    }
}
