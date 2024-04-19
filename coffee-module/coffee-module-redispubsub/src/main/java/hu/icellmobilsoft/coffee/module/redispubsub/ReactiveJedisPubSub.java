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

import java.util.Map;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.reactivestreams.Subscriber;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.module.redispubsub.bundle.PubSubMessage;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.string.RandomUtil;
import redis.clients.jedis.JedisPubSub;

/**
 * mp reactive streams implementation of {@link JedisPubSub}. Received messages are converted to {@link PubSubMessage} and sent to the configured
 * subscriber.
 *
 * @author mark.petrenyi
 * @since 1.13.0
 */
public class ReactiveJedisPubSub extends JedisPubSub implements AutoCloseable {

    private static final Logger LOG = Logger.getLogger(ReactiveJedisPubSub.class);

    private final Subscriber<? super Message<?>> subscriber;

    /**
     * Instantiates a new ReactiveJedisPubSub
     *
     * @param subscriber
     *            the base subscriber to use when receiving message
     */
    public ReactiveJedisPubSub(Subscriber<? super Message<?>> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onMessage(String channel, String message) {
        MDC.clear();
        PubSubMessage pubSubMessage = getPubSubMessage(message);
        handleMDC(pubSubMessage);
        LOG.trace("Redis pub/sub message received, channel:[{0}], message:[{1}]", channel, message);
        subscriber.onNext(pubSubMessage);
    }

    private void handleMDC(PubSubMessage pubSubMessage) {
        Map<String, String> context = pubSubMessage.getContext();
        String sid;
        if (context != null && context.containsKey(LogConstants.LOG_SESSION_ID)) {
            sid = context.get(LogConstants.LOG_SESSION_ID);
        } else {
            sid = RandomUtil.generateId();
        }
        MDC.put(LogConstants.LOG_SESSION_ID, sid);
    }

    private PubSubMessage getPubSubMessage(String message) {
        try {
            return JsonUtil.toObjectEx(message, PubSubMessage.class);
        } catch (BaseException e) {
            LOG.warn("Could not parse message as PubSubMessage, assuming message is payload! [{0}]", e.getLocalizedMessage());
            return PubSubMessage.of(message);
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        LOG.info("subscribed to redis pub/sub channel:[{0}], subscribedChannels:[{1}]", channel, subscribedChannels);

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        LOG.info("unsubscribed from redis pub/sub channel:[{0}], subscribedChannels:[{1}]", channel, subscribedChannels);
    }

    @Override
    public void close() {
        if (subscriber != null) {
            subscriber.onComplete();
        }
    }

}
