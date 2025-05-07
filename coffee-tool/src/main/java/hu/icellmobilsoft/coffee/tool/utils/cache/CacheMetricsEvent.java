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
