/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
     * <br>
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
     *            stream adat tartalom, konkrétan a {@value IRedisStreamConstant.Common#DATA_KEY_MESSAGE} kulcs értéke, ami string vagy json lehet
     * @throws BaseException
     *             hiba a feldolgozás során
     */
    public abstract void doWork(String text) throws BaseException;
}
