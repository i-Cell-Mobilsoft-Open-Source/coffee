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
package hu.icellmobilsoft.coffee.cdi.metric;

/**
 * Pojo for neutral metric tag definition
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 */
public class MetricTag {

    private String key;

    private String value;

    /**
     * Default constructor, constructs a new object.
     * 
     * @param key
     *            metric tag key
     * @param value
     *            metric tag value
     */
    public MetricTag(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Get metric tag key
     * 
     * @return metric key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get metric tag value
     * 
     * @return metric value
     */
    public String getValue() {
        return value;
    }
}
