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
     * @return the stream group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the thread count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

}
