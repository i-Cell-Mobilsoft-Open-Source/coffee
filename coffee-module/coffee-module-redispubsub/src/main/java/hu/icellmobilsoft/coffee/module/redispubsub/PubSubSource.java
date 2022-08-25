/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 i-Cell Mobilsoft Zrt.
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

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import redis.clients.jedis.Jedis;

/**
 * Redis pubsub implementation for {@link Publisher}. Consumes messages from redis pub/sub channel, and publishes them to mp reactive channel.
 * Messages can be consumed via {@link org.eclipse.microprofile.reactive.messaging.Incoming} annotation.
 *
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class PubSubSource implements Publisher<Message<?>>, AutoCloseable {

    private final PubSubConnectorIncomingConfiguration config;
    private final ExecutorService subscriberService;
    private final Logger log = Logger.getLogger(PubSubSource.class);

    private Instance<Jedis> jedisInstance;
    private Jedis jedis;
    private ReactiveJedisPubSub pubsub;

    /**
     * Instantiates a new Pub sub source.
     *
     * @param config
     *            the incoming mp stream configuration
     * @param subscriberService
     *            the executorService on wich redis subscription shall occur
     */
    public PubSubSource(PubSubConnectorIncomingConfiguration config, ExecutorService subscriberService) {
        this.config = config;
        this.subscriberService = subscriberService;
    }

    @Override
    public void subscribe(Subscriber<? super Message<?>> subscriber) {
        // külön szálon indítjuk a hallgatózást, különben a deployment szálat beragasztanánk
        subscriberService.execute(() -> jedisSubscribe(subscriber));
    }

    private void jedisSubscribe(Subscriber<? super Message<?>> subscriber) {
        String channel = config.getPubSubChannel().orElseGet(config::getChannel);
        String connectionKey = config.getConnectionKey();
        String poolKey = config.getPoolKey();
        log.info("Subscribing to redis channel:[{0}]...", channel);

        jedisInstance = CDI.current().select(Jedis.class, new RedisConnection.Literal(connectionKey, poolKey));
        jedis = null;
        try {
            pubsub = new ReactiveJedisPubSub(subscriber, getSubscription());
            jedis = jedisInstance.get();
            jedis.subscribe(pubsub, channel);
        } catch (Exception e) {
            if (pubsub != null) {
                // ha a pubsub nem null, akkor elszállt (pl. connection hiba), különben épp lezáródik
                log.error(MessageFormat.format("Redis subscription failed on redis channel:[{0}], retrying...", channel), e);
                closeJedis();
                sleep();
                jedisSubscribe(subscriber);
            }
        }
    }

    private Subscription getSubscription() {
        // mp-reactive-streams nagyrészt lekezeli ami nekünk kell,
        // úgyhogy egyelőre csak logolunk és resource lazárásra használjuk,
        // később ha kell plusz logika ki lehet szervezni külön osztálynak
        return new Subscription() {
            @Override
            public void request(long n) {
                log.trace("Consumer is ready for [{0}] new messages.", n);
            }

            @Override
            public void cancel() {
                log.info("Consumer is cancelling subscription");
                closeJedis();
            }
        };
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

    @Override
    public void close() {
        closeJedis();
    }

    private void closeJedis() {
        if (pubsub != null && pubsub.isSubscribed()) {
            ReactiveJedisPubSub toUnsunscribe = pubsub;
            pubsub = null;
            toUnsunscribe.unsubscribe();
        }
        if (jedis != null) {
            jedisInstance.destroy(jedis);
            jedis = null;
            jedisInstance = null;
        }
    }
}
