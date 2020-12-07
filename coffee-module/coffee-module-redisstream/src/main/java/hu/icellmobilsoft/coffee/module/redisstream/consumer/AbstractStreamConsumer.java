package hu.icellmobilsoft.coffee.module.redisstream.consumer;

import java.util.Map;

import javax.annotation.Resource;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redisstream.config.IRedisStreamConstant;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import redis.clients.jedis.StreamEntry;

/**
 * Default Redis stream consumer
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public abstract class AbstractStreamConsumer implements IRedisStreamConsumer {

    @Resource(lookup = "java:app/AppName")
    private String applicationName;

    /**
     * {@inheritDoc}
     * 
     * <br/>
     * <br/>
     * Az egész stream tartalmat kategorizálva várja, melyek üzleti metadata értéket hordoznak. Például a folyamat azonosító
     */
    @Override
    public void onStream(StreamEntry streamEntry) throws BaseException {
        try {
            MDC.put(LogConstants.LOG_SERVICE_NAME, applicationName);

            Map<String, String> fieldMap = streamEntry.getFields();
            String mainData = fieldMap.get(IRedisStreamConstant.Common.DATA_KEY_MESSAGE);
            String flowId = fieldMap.getOrDefault(IRedisStreamConstant.Common.DATA_KEY_FLOW_ID, mainData);
            MDC.put(LogConstants.LOG_SESSION_ID, flowId);

            doWork(mainData);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Alap implementáló metódus, üzleti logikát tartalmaz
     * 
     * @param text
     *            stream adat tartalom, konkrétan a {@value RedisStreamConstant.Common#DATA_KEY_MAIN} kulcs értéke, ami string vagy json lehet
     * @throws BaseException
     *             hiba a feldolgozás során
     */
    public abstract void doWork(String text) throws BaseException;
}
