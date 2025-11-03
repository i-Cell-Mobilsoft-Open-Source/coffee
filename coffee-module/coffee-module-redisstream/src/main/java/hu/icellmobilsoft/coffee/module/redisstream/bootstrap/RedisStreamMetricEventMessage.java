/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redisstream.bootstrap;

/**
 * CDI event for redis stream metric data
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 */
public class RedisStreamMetricEventMessage {

    private String group;
    private int count;

    /**
     * Constructor 
     */
    public RedisStreamMetricEventMessage() {
    }
    
    /**
     * Get the stream group name
     * 
     * @return the stream group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set the stream name
     * 
     * @param group
     *            the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Get the thread count
     * 
     * @return the thread count
     */
    public int getCount() {
        return count;
    }

    /**
     * Set the thread count
     * 
     * @param count
     *            the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

}
