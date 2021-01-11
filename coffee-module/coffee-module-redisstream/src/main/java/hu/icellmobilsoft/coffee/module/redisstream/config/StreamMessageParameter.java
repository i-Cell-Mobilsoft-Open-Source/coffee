package hu.icellmobilsoft.coffee.module.redisstream.config;

/**
 * Redis Stream message parameters
 * 
 * @author imre.scheffer 1.3.0
 */
public enum StreamMessageParameter {
    /**
     * Stream message time to live. Value is epoch time millis. If value is < now, then message is expired
     */
    TTL(IRedisStreamConstant.Common.DATA_KEY_TTL);

    String messageKey;

    private StreamMessageParameter(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
