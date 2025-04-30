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
