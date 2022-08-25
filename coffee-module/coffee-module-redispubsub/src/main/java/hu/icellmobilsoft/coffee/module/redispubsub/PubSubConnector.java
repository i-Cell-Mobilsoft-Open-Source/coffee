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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.spi.Connector;
import org.eclipse.microprofile.reactive.messaging.spi.IncomingConnectorFactory;
import org.eclipse.microprofile.reactive.messaging.spi.OutgoingConnectorFactory;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.eclipse.microprofile.reactive.streams.operators.SubscriberBuilder;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * MP reactive streams connector for redis pub-sub. <br>
 * <br>
 * Creating consumer<br>
 * Config<br>
 *
 * <pre>
 * {@code
 * mp.messaging.incoming.<channel-key>.connector=coffee-redis-pubsub #1
 * mp.messaging.incoming.<channel-key>.connection-key=sample #2
 * mp.messaging.incoming.<channel-key>.connection-key=sample-consumer #3
 * mp.messaging.incoming.<channel-key>.pub-sub-channel=<redis-pub-sub-channel> #4
 * }*
 * </pre>
 *
 * <ol>
 * <li>use redis pub sub as message source
 * <li>Coffee redis connection key {@code coffee.redis.*}
 * <li>Optional Coffee redis pool key {@code coffee.redis.*.pool.*}, defaults to {@code default}
 * <li>Optional pubsub channel deafults to {@code <channel-key>}
 * </ol>
 *
 * Consumer<br>
 *
 * <pre>
 * &#64;Incoming("&#60;channel-key&#62;")
 * void consume(String msg) {
 *     // logic
 * }
 * </pre>
 *
 * Creating producer<br>
 * Config<br>
 *
 * <pre>
 * {@code
 * mp.messaging.outgoing.<channel-key>.connector=coffee-redis-pubsub
 * mp.messaging.outgoing.<channel-key>.connection-key=sample
 * mp.messaging.outgoing.<channel-key>.connection-key=sample-producer
 * mp.messaging.outgoing.<channel-key>.pub-sub-channel=<redis-pub-sub-channel>
 * }*
 * </pre>
 *
 * Producer<br>
 *
 * <pre>
 * &#64;Inject
 * &#64;Channel("&#60;channel-key&#62;")
 * private Emitter&#60;String&#62; emitter;
 *
 * public void publish(String message) {
 *     emitter.send(message);
 * }
 * </pre>
 *
 * @author mark.petrenyi
 * @since 1.11.0
 */
@ApplicationScoped
@Connector(PubSubConnector.CONNECTOR_NAME)
// Legenerálná a config osztályokat és a config doksit is, de egyelőre csak experimental a WF alatt
// @ConnectorAttribute(name = "connection-key", mandatory = true, type = "string", direction = ConnectorAttribute.Direction.INCOMING_AND_OUTGOING,
// description = "Coffee redis connection key coffee.redis.*")
// @ConnectorAttribute(name = "pool-key", type = "string", direction = ConnectorAttribute.Direction.INCOMING_AND_OUTGOING,
// description = "Coffee redis pool key coffee.redis.*.pool.*", defaultValue = "default")
// @ConnectorAttribute(name = "pub-sub-channel", type = "string", direction = ConnectorAttribute.Direction.INCOMING_AND_OUTGOING,
// description = "Optional name of pub/sub channel, defaults to microprofile stream channel, workaround to have both publisher and subscriber for the
// same channel within a service.")
public class PubSubConnector implements IncomingConnectorFactory, OutgoingConnectorFactory {
    /**
     * The connector name. Activates redis connector with {@code mp.messaging.outgoing.<channel-key>.connector} config key
     */
    public static final String CONNECTOR_NAME = "coffee-redis-pubsub";
    private ExecutorService executorService;
    @Inject
    private Logger log;

    private final List<PubSubSource> publishers = new CopyOnWriteArrayList<>();

    /**
     * Initialize executor service.
     *
     * @throws NamingException
     *             the naming exception
     */
    @PostConstruct
    public void initialize() throws NamingException {
        executorService = InitialContext.doLookup("java:jboss/ee/concurrency/executor/default");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PublisherBuilder<? extends Message<?>> getPublisherBuilder(Config config) {
        log.info("Creating [{0}] PublisherBuilder...", CONNECTOR_NAME);
        PubSubConnectorIncomingConfiguration inConfig = new PubSubConnectorIncomingConfiguration(config);

        PublisherBuilder<Message<?>> messagePublisherBuilder = redisPubSubPublisher(inConfig);
        log.info("Created [{0}] PublisherBuilder for mp channel:[{0}]", CONNECTOR_NAME, inConfig.getChannel());
        return messagePublisherBuilder;
    }

    private PublisherBuilder<Message<?>> redisPubSubPublisher(PubSubConnectorIncomingConfiguration inConfig) {
        PubSubSource publisher = new PubSubSource(inConfig, executorService);
        publishers.add(publisher);
        return ReactiveStreams.fromPublisher(publisher).onErrorResumeWith(e -> {
            log.error(MessageFormat.format("Error occured on channel: [{0}], rebuilding PubSubReactivePublisher...", inConfig.getChannel()), e);
            publisher.close();
            return redisPubSubPublisher(inConfig);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubscriberBuilder<? extends Message<?>, Void> getSubscriberBuilder(Config config) {
        log.info("Creating [{0}] SubscriberBuilder...", CONNECTOR_NAME);
        PubSubConnectorOutgoingConfiguration outConfig = new PubSubConnectorOutgoingConfiguration(config);
        PubSubSink pubSubSink = new PubSubSink(outConfig);
        SubscriberBuilder<Message<?>, Void> subscriberBuilder = ReactiveStreams.<Message<?>> builder().forEach(pubSubSink::publishMessage);
        log.info("Created [{0}] SubscriberBuilder for mp channel:[{0}]", CONNECTOR_NAME, outConfig.getChannel());
        return subscriberBuilder;
    }

    /**
     * Destroys instance, closes opened resources.
     */
    @PreDestroy
    public void destroy() {
        publishers.forEach(PubSubSource::close);
    }
}
