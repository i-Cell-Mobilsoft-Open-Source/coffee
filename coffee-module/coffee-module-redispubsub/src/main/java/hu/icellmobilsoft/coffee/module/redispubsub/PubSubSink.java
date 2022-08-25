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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.eclipse.microprofile.reactive.messaging.Message;

import hu.icellmobilsoft.coffee.dto.common.LogConstants;
import hu.icellmobilsoft.coffee.module.redis.annotation.RedisConnection;
import hu.icellmobilsoft.coffee.module.redis.manager.RedisManager;
import hu.icellmobilsoft.coffee.module.redispubsub.bundle.PubSubMessage;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.se.logging.mdc.MDC;
import hu.icellmobilsoft.coffee.tool.gson.JsonUtil;
import redis.clients.jedis.Jedis;

/**
 * Sink for redis pubsub. Used by connector to consume emitted messages and publish them to redis. Messages can be emitted via
 * {@link org.eclipse.microprofile.reactive.messaging.Outgoing} annotation or {@link org.eclipse.microprofile.reactive.messaging.Emitter#send} method
 *
 * @author mark.petrenyi
 * @since 1.1.0
 */
public class PubSubSink {
    private final PubSubConnectorOutgoingConfiguration outConfig;
    private final Logger log = Logger.getLogger(PubSubSink.class);

    /**
     * Instantiates a new PubSubSink.
     *
     * @param outConfig
     *            the outgoing configuration
     */
    public PubSubSink(PubSubConnectorOutgoingConfiguration outConfig) {
        this.outConfig = outConfig;
    }

    /**
     * Publish message to redis pub sub. Always sends {@link PubSubMessage} as json to redis channel
     *
     * @param msg
     *            the mp reactive streams message to be sent
     */
    public void publishMessage(Message<?> msg) {
        log.trace(">> publishMessage:[{0}]", msg);
        String messagePayload = getPubSubMessage(msg);
        String channel = outConfig.getPubSubChannel().orElseGet(outConfig::getChannel);
        Instance<RedisManager> redisManagerInstance = CDI.current().select(RedisManager.class,
                new RedisConnection.Literal(outConfig.getConnectionKey(), outConfig.getPoolKey()));
        RedisManager redisManager = null;
        try {
            redisManager = redisManagerInstance.get();
            Optional<Long> published = redisManager.runWithConnection(Jedis::publish, "publish", channel, messagePayload);
            log.trace("Message published to [{0}] clients!", published.orElse(0L));
            // redis pub/sub nem kezel ack/nack-ot, de az ide érkező message jöhet olyan forrásból,
            // ahol kell, egyelőre ha sikeres a kiküldés ackolunk, ha nem akkor nack, később ezen lehet finomítani.
            msg.ack();
        } catch (Exception e) {
            String errorMsg = MessageFormat.format("Could not publish message:[{0}] to redis pub/sub channel:[{1}]", msg, channel);
            log.error(errorMsg, e);
            msg.nack(e);
        } finally {
            if (redisManager != null) {
                redisManagerInstance.destroy(redisManager);
            }
            log.trace("<< publishMessage:[{0}]", msg);
        }
    }

    private String getPubSubMessage(Message<?> msg) {
        PubSubMessage bundle;
        if (msg instanceof PubSubMessage) {
            bundle = (PubSubMessage) msg;
        } else if (msg.getPayload() instanceof PubSubMessage) {
            bundle = (PubSubMessage) msg.getPayload();
        } else {
            Object payload = msg.getPayload();
            String payloadString = (payload instanceof String) ? (String) payload : JsonUtil.toJson(payload);
            Map<String, String> context = new HashMap<>();
            context.put(LogConstants.LOG_SESSION_ID, MDC.get(LogConstants.LOG_SESSION_ID));
            bundle = PubSubMessage.of(payloadString, context);
        }
        return JsonUtil.toJson(bundle);
    }
}
