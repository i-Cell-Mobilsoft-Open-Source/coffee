package hu.icellmobilsoft.coffee.tool.utils.cache;

import java.text.MessageFormat;

import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.evict.Evictable;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Common class for helping to cache objects
 *
 * @param <KEY>
 *            the type of the cache key
 * @param <VALUE>
 *            the type of the cache values
 *
 * @author tamas.cserhati
 * @author martin.nagy
 * @since 2.11.0
 */
public abstract class AbstractCache<KEY, VALUE> implements Evictable {
    private static final String CONFIG_PATTERN = "coffee.cache.guava.{0}.{1}";
    private static final String DISABLE_METRICS = "disableMetrics";
    private static final String SPECS = "specs";

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private Event<CacheMetricsEvent> metricsEvent;

    /**
     * constructor
     */
    private AbstractCache() {
    }

    private final Config config = ConfigProvider.getConfig();

    /**
     * Returns with the guava cache object
     *
     * @return the {@link Cache} object
     */
    protected abstract Cache<KEY, VALUE> getCache();

    /**
     * Creates a preconfigured cache builder
     *
     * @return the {@link CacheBuilder}
     */
    protected CacheBuilder<Object, Object> createCacheBuilder() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.from(getSpecs());

        if (isMetricsEnabled()) {
            cacheBuilder = cacheBuilder.recordStats();
        }

        RemovalListener<Object, Object> removalListener = (RemovalListener<Object, Object>) getRemovalListener();
        if (removalListener != null) {
            cacheBuilder = cacheBuilder.removalListener(removalListener);
        }

        return cacheBuilder;
    }

    /**
     * return the listener for deleting cache key
     * 
     * @return the listener which runs on the deletion of the key
     */
    protected RemovalListener<KEY, VALUE> getRemovalListener() {
        if (isMetricsEnabled()) {
            return notification -> updateMetrics();
        }

        return null;
    }

    /**
     * Returns with the cache specification
     * 
     * @see com.google.common.cache.CacheBuilderSpec
     *
     * @return the specification
     */
    protected String getDefaultSpecs() {
        return "expireAfterWrite=12h";
    }

    /**
     * Returns with the configured cache name
     *
     * @return the cache name used in configuration
     */
    protected String getCacheName() {
        return getClass().getSimpleName();
    }

    @Override
    public void evict() {
        getCache().invalidateAll();
    }

    /**
     * Removes the value(s) associated with the key
     * 
     * @param key
     *            the key
     * @throws BaseException
     *             if any error occurs
     */
    protected void evict(KEY key) throws BaseException {
        if (key == null) {
            throw new InvalidParameterException(CoffeeFaultType.INVALID_INPUT, "key is missing");
        }
        getCache().invalidate(key);
    }

    /**
     * Merics update
     */
    protected void updateMetrics() {
        if (isMetricsEnabled()) {
            try {
                doUpdateMetrics();
            } catch (BaseException e) {
                log.error("Error during cache metric collection", e);
            }
        }
    }

    /**
     * Do the metrics update
     * 
     * @throws BaseException
     *             if any error occurs
     */
    protected void doUpdateMetrics() throws BaseException {
        updateMetrics(getCache(), getCacheName());
    }

    private String getSpecs() {
        return config.getOptionalValue(formatKey(SPECS), String.class).orElse(getDefaultSpecs());
    }

    private boolean isMetricsEnabled() {
        return !config.getOptionalValue(formatKey(DISABLE_METRICS), Boolean.class).orElse(false);
    }

    private String formatKey(String key) {
        return MessageFormat.format(CONFIG_PATTERN, getCacheName(), key);
    }

    private void updateMetrics(Cache<?, ?> cache, String cacheName) throws BaseException {
        if (cache == null || StringUtils.isBlank(cacheName)) {
            throw new InvalidParameterException(CoffeeFaultType.INVALID_INPUT, "cache or cacheName is missing!");
        }
        metricsEvent.fireAsync(createEvent(cache, cacheName));
    }

    private CacheMetricsEvent createEvent(Cache<?, ?> cache, String cacheName) {
        CacheMetricsEvent event = new CacheMetricsEvent();
        CacheStats stats = cache.stats();
        event.setCacheName(cacheName);
        event.setHitCount(stats.hitCount());
        event.setMissCount(stats.missCount());
        event.setSize(cache.size());
        return event;
    }
}
