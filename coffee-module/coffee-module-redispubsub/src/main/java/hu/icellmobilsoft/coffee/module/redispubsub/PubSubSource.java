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
        // We start the listening on a separate thread to avoid blocking the deployment thread.
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
            // If the list is empty, it means it was closed while we were in the subscribe operation, so we don't need to do anything.
            if (!redisSubscriptions.isEmpty()) {
                // MP Stream error - this will be restarted by the connector's onError handler.
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
        // First, we clear the list so that if an active subscription fails during closing, we don't handle it.
        redisSubscriptions.clear();
        subscriptionsToClose.forEach(RedisSubscription::close);
    }
}
