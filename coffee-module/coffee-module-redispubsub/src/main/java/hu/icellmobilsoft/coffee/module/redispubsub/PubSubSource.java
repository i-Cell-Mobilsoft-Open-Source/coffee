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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import jakarta.enterprise.inject.Vetoed;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Redis pubsub implementation for {@link Publisher}. Consumes messages from redis pub/sub channel, and publishes them to mp reactive channel.
 * Messages can be consumed via {@link org.eclipse.microprofile.reactive.messaging.Incoming} annotation.
 *
 * @author mark.petrenyi
 * @since 1.13.0
 */
@Vetoed
public class PubSubSource implements Publisher<Message<?>>, AutoCloseable {

    private final PubSubConnectorIncomingConfiguration config;
    private final ExecutorService subscriberService;
    private final Logger log = Logger.getLogger(PubSubSource.class);

    private final List<RedisSubscription> redisSubscriptions = new CopyOnWriteArrayList<>();

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
        log.info("Subscribing to redis channel:[{0}]...", channel);

        RedisSubscription redisSubscription = new RedisSubscription(config);
        redisSubscriptions.add(redisSubscription);
        try {
            redisSubscription.subscribe(subscriber);
        } catch (Exception e) {
            // Ha üres a lista, akkor close-olva lett miközben subscribe-ban voltunk, nincs dolgunk
            if (!redisSubscriptions.isEmpty()) {
                // MP stream hiba - ezt majd a connector onErrorja indítja újra
                log.error(MessageFormat.format("Unexpected error occured while subscribed to redis channel:[{0}]...", channel), e);
                redisSubscription.close();
                redisSubscriptions.remove(redisSubscription);
                throw e;
            }
        }
    }

    @Override
    public void close() {
        List<RedisSubscription> subscriptionsToClose = new ArrayList<>(redisSubscriptions);
        // elsőnek cleareljük a listát, hogy ha close alatt száll el aktív felíratkozás, akkor azzal ne foglalkozzunk
        redisSubscriptions.clear();
        subscriptionsToClose.forEach(RedisSubscription::close);
    }
}
