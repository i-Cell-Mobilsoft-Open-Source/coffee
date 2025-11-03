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
package hu.icellmobilsoft.coffee.tool.utils.cache;

/**
 * Cache metrics data for async CDI event
 * 
 * @author tamas.cserhati
 * @since 2.11.0
 */
public class CacheMetricsEvent {

    private static final String METADATA_HIT_COUNT_NAME = "cache_hit_count";
    private static final String METADATA_MISS_COUNT_NAME = "cache_miss_count";
    private static final String METADATA_SIZE_NAME = "cache_size";
    private String cacheName;
    private Long hitCount;
    private Long missCount;
    private Long size;

    /**
     * constructor
     */
    public CacheMetricsEvent() {
    }

    /**
     * returns the cache name
     * 
     * @return the cacheName
     */
    public String getCacheName() {
        return cacheName;
    }

    /**
     * set the cache name
     * 
     * @param cacheName
     *            the cacheName to set
     */
    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * return the hit count
     * 
     * @return the hitCount
     */
    public Long getHitCount() {
        return hitCount;
    }

    /**
     * set the hit count
     * 
     * @param hitCount
     *            the hitCount to set
     */
    public void setHitCount(Long hitCount) {
        this.hitCount = hitCount;
    }

    /**
     * return the miss count
     * 
     * @return the missCount
     */
    public Long getMissCount() {
        return missCount;
    }

    /**
     * set the miss count
     * 
     * @param missCount
     *            the missCount to set
     */
    public void setMissCount(Long missCount) {
        this.missCount = missCount;
    }

    /**
     * return the cache size
     * 
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * set the cache size
     * 
     * @param size
     *            the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * return the string {@value #METADATA_HIT_COUNT_NAME}
     * 
     * @return the metadataHitCountName
     */
    public static String getMetadataHitCountName() {
        return METADATA_HIT_COUNT_NAME;
    }

    /**
     * return the string {@value #METADATA_MISS_COUNT_NAME}
     * 
     * @return the metadataMissCountName
     */
    public static String getMetadataMissCountName() {
        return METADATA_MISS_COUNT_NAME;
    }

    /**
     * return the string {@value #METADATA_SIZE_NAME}
     * 
     * @return the metadataSizeName
     */
    public static String getMetadataSizeName() {
        return METADATA_SIZE_NAME;
    }
}
