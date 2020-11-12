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

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.jboss.weld.context.bound.BoundRequestContext;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redisstream.service.RedisStreamService;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntry;

/**
 * Abstract stream consumer class
 * 
 * @author imre.scheffer
 * @since 1.3.0
 */
public abstract class AbstractStreamConsumer implements IRedisStreamConsumer {

    @Inject
    private Logger log;

    @Inject
    private RedisStreamService redisStreamService;

    private String consumerIdentifier;

    private String redisConfigKey;

    private boolean endLoop;

    @Inject
    private BoundRequestContext boundRequestContext;

    @Override
    public void init(String redisConfigKey, String group) {
        this.redisConfigKey = redisConfigKey;
        redisStreamService.setGroup(group);
    }

    /**
     * Vegtelen ciklus inditasa, ami a streamet olvassa
     */
    public void startLoop() {
        consumerIdentifier = RandomUtil.generateId();
        endLoop = false;
        boolean firstRun = true;
        while (!endLoop) {
            Optional<StreamEntry> streamEntry = Optional.empty();
            Instance<Jedis> jedisInstance = CDI.current().select(Jedis.class, new RedisConnection.Literal(redisConfigKey));
            Jedis jedis = null;
            try {
                jedis = jedisInstance.get();
                redisStreamService.setJedis(jedis);

                if (firstRun) {
                    // lehethogy a csoport nem letezik
                    redisStreamService.handleGroup();
                    firstRun = false;
                }

                streamEntry = redisStreamService.consumeOne(consumerIdentifier);

                if (streamEntry.isPresent()) {
                    executeProcess(streamEntry.get());

                    // ack
                    redisStreamService.ack(streamEntry.get().getID());
                }
            } catch (BaseException e) {
                log.error(MessageFormat.format("Exception on consume streamEntry [{0}]: [{1}]", streamEntry, e.getLocalizedMessage()), e);
            } catch (Exception e) {
                log.error("Exception on consume stream: [" + e.getLocalizedMessage() + "]", e);
                sleep();
            } finally {
                if (jedis != null) {
                    // el kell engedni a connectiont
                    jedisInstance.destroy(jedis);
                }
            }
        }
    }

    /**
     * Process execution wrapper. Running process in self started request scope
     * 
     * @param streamEntry
     *            Redis stream entry
     * @throws BaseException
     *             exceptin is error
     */
    protected void executeProcess(StreamEntry streamEntry) throws BaseException {
        Map<String, Object> requestScopeStore = null;
        try {
            requestScopeStore = new ConcurrentHashMap<>();
            startRequestScope(requestScopeStore);
            process(streamEntry);
        } finally {
            endRequestScope(requestScopeStore);
        }
    }

    private void startRequestScope(Map<String, Object> requestScopeDataStore) {
        boundRequestContext.associate(requestScopeDataStore);
        boundRequestContext.activate();
    }

    private void endRequestScope(Map<String, Object> requestScopeDataStore) {
        try {
            boundRequestContext.invalidate();
            boundRequestContext.deactivate();
        } finally {
            if (requestScopeDataStore != null) {
                boundRequestContext.dissociate(requestScopeDataStore);
            }
        }
    }

    private void sleep() {
        try {
            // fontos a szuneteltetes hogy peldaul a connection szakadasa ne floodolja a logot
            // es ne menjen felesleges korlatlan vegtelen probalkosba
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException ex) {
            log.warn("Interrupted sleep.", ex);
            // sonar: "InterruptedException" should not be ignored (java:S2142)
            try {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.warn("Exception during interrupt.", ex);
            }
        }
    }

    /**
     * Egyedi stream consumer azonosito
     * 
     * @return azonosito
     */
    public String getConsumerIdentifier() {
        return consumerIdentifier;
    }

    /**
     * Végtelen stream olvasása leállítása
     */
    public void stopLoop() {
        endLoop = true;
    }

    @Override
    public void run() {
        startLoop();
    }
}
