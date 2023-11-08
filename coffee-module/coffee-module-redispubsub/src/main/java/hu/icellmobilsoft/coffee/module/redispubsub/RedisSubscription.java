/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redispubsub;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import redis.clients.jedis.Jedis;

/**
 * Microprofile reactive subscription - redis subscribe és mp stream-et köti össze
 * 
 * @since 1.13.0
 * @author mark.petrenyi
 */
public class RedisSubscription implements Subscription, Closeable {

    private final PubSubConnectorIncomingConfiguration config;

    private final Logger log = Logger.getLogger(RedisSubscription.class);

    private Instance<Jedis> jedisInstance;
    private Jedis jedis;
    private ReactiveJedisPubSub pubSub;

    /**
     * Instantiates a new Redis subscription.
     *
     * @param config
     *            redis pub sub incoming configuration
     */
    public RedisSubscription(PubSubConnectorIncomingConfiguration config) {
        this.config = config;
    }

    /**
     * Subscribe to redis.
     *
     * @param subscriber
     *            the mp reactive subscriber to connect with redis SUB
     */
    public void subscribe(Subscriber<? super Message<?>> subscriber) {
        if (subscriber != null) {
            // mp subscription
            pubSub = new ReactiveJedisPubSub(subscriber);
            subscriber.onSubscribe(this);
            // redis subscription
            subscribeInLoop();
        }
    }

    private void subscribeInLoop() {
        try {
            String channel = config.getPubSubChannel().orElseGet(config::getChannel);
            String connectionKey = config.getConnectionKey();
            String poolKey = config.getPoolKey();
            jedisInstance = CDI.current().select(Jedis.class, new RedisConnection.Literal(connectionKey, poolKey));
            jedis = jedisInstance.get();
            jedis.subscribe(pubSub, channel);
        } catch (Exception e) {
            if (pubSub != null) {
                // Jedis hiba, az mp stream még él, elég csak a redisre újra csatlakozni
                log.error("Jedis subscribe failed, closing resources and waiting to retry...", e);
                jedisClose();
                sleep();
                subscribeInLoop();
            }
        }
    }

    private void sleep() {
        try {
            // fontos a szuneteltetes hogy peldaul a connection szakadasa ne floodolja a logot
            // es ne menjen felesleges korlatlan vegtelen probalkosba
            TimeUnit.SECONDS.sleep(config.getRetrySeconds());
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

    @Override
    public void request(long n) {
        log.trace("Consumer is ready for [{0}] new messages.", n);
        // egyelőre csak logolunk, később lehet akár belső queue-ingot kialakítani
    }

    @Override
    public void cancel() {
        log.info("Consumer is cancelling subscription...");
        close();
    }

    @Override
    public void close() {
        if (pubSub != null) {
            pubSub.close();
            pubSub = null;
        }
        jedisClose();
    }

    private void jedisClose() {
        if (jedis != null) {
            jedisInstance.destroy(jedis);
            jedis = null;
            jedisInstance = null;
        }
    }
}
