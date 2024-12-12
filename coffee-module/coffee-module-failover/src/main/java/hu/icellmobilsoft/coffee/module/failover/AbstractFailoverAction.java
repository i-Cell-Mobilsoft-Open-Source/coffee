package hu.icellmobilsoft.coffee.module.failover;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.module.redisstream.annotation.RedisStreamProducer;
import hu.icellmobilsoft.coffee.module.redisstream.config.StreamMessageParameter;
import hu.icellmobilsoft.coffee.module.redisstream.publisher.RedisStreamPublisher;
import hu.icellmobilsoft.coffee.rest.action.AbstractEntityProcessorAction;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

/**
 * Abstract action for failover endpoints.
 *
 * @param <T>
 *            the type of the collected messages
 * @author krisztian.hathazi
 * @author attila-kiss-it
 * @since 2.10.0
 */
public abstract class AbstractFailoverAction<T> extends AbstractEntityProcessorAction {

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Failover implementation:
     * <ul>
     * <li>collects the records/messages that must be resend: {@link #getMessagesToResend(OffsetDateTime, OffsetDateTime, int)}</li>
     * <li>converts the records/messages to {@link String} messages: {@link #convertMessagesToString(List)}</li>
     * <li>publishes the {@link String} messages to a Redis stream: {@link #publish(List)}</li>
     * <li>increments the relevant metrics: {@link #incrementMetrics(List)}</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    protected int doProcess(OffsetDateTime fromDateTime, OffsetDateTime toDateTime, int limit) throws BaseException {

        List<T> messagesToResend = getMessagesToResend(fromDateTime, toDateTime, limit);
        List<String> messages = convertMessagesToString(messagesToResend);

        publish(messages);
        incrementMetrics(messages);

        int size = messages.size();
        String redisStreamGroup = getRedisStreamProducer().group();

        log.info(
                "Failover action published [{0}] records to [{1}] stream. FromDateTime: [{2}], toDateTime: [{3}], limit: [{4}]",
                size,
                redisStreamGroup,
                fromDateTime,
                toDateTime,
                limit);

        return size;
    }

    /**
     * Collects the records/messages that must be resend.
     *
     * @param fromDateTime
     *            start time
     * @param toDateTime
     *            end time
     * @param limit
     *            limit
     * @return the records/messages that must be resend
     * @throws BaseException
     *             in case of failure
     */
    protected abstract List<T> getMessagesToResend(OffsetDateTime fromDateTime, OffsetDateTime toDateTime, int limit) throws BaseException;

    /**
     * Converts the collected records/messages to {@link String} to be able to publish them.
     *
     * @param messagesToResend
     *            the collected messages
     * @return the {@link List} of the converted messages in {@link String} format
     * @throws BaseException
     *             in case of failure
     */
    protected abstract List<String> convertMessagesToString(List<T> messagesToResend) throws BaseException;

    /**
     * Publishes the messages to the configured {@link RedisStreamProducer} with the message parameters defined by
     * {@link #getStreamMessageParameters()}.
     *
     * @param messages
     *            the messages to send
     * @throws BaseException
     *             in case of error
     */
    protected void publish(List<String> messages) throws BaseException {
        RedisStreamPublisher redisStreamPublisher = CDI.current()
                .select(RedisStreamPublisher.class, getRedisStreamProducer())
                .get();
        redisStreamPublisher.publish(messages, getStreamMessageParameters());
    }

    /**
     * Returns the configuration of the {@link RedisStreamProducer}.
     *
     * @return {@link RedisStreamProducer}
     * @throws BaseException
     *             in case of error
     */
    protected abstract RedisStreamProducer getRedisStreamProducer() throws BaseException;

    /**
     * Returns the stream message parameters that must be used during publish.
     *
     * @return the {@link Map} of stream message parameters
     * @throws BaseException
     *             in case of error
     *
     * @see StreamMessageParameter
     */
    protected Map<String, String> getStreamMessageParameters() throws BaseException {
        return Map.ofEntries(RedisStreamPublisher.parameterOf(StreamMessageParameter.TTL, Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()));
    }

    /**
     * Increments the metrics according to the published messages.
     *
     * @param messages
     *            the published messages
     * @throws BaseException
     *             in case of error
     */
    protected abstract void incrementMetrics(List<String> messages) throws BaseException;

    // TODO redis stream es metrika keresztezese

}
