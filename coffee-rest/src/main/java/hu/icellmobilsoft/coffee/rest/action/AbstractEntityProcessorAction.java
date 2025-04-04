package hu.icellmobilsoft.coffee.rest.action;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.EntityProcessorResponse;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;
import jakarta.inject.Inject;

/**
 * Common entity processor action with range and limit parameters.
 *
 * @author martin.nagy
 * @author attila-kiss-it
 * @since 2.10.0
 */
public abstract class AbstractEntityProcessorAction extends AbstractBaseAction {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Override
    public ContextType createContext() {
        ContextType context = new ContextType();
        context.setRequestId(RandomUtil.generateId());
        context.setTimestamp(DateUtil.nowUTCTruncatedToMillis());
        return context;
    }

    /**
     * Processes the records by the given parameters. Prepares the input of the {@link #doProcess} call.
     *
     * @param fromDateTimeString
     *            (optional) processes the records after the specified time, defaults to {@link #getDefaultFromDateTime()}, in format parsed by
     *            {@link #parseDateTime(String)}
     * @param toDateTimeString
     *            (optional) processes the records before the specified time, defaults to {@link #getDefaultToDateTime()}, in format parsed by
     *            {@link #parseDateTime(String)}
     * @param limit
     *            (optional) the number of maximum records to process, defaults to {@link #getDefaultLimit()}
     * @return {@link EntityProcessorResponse}
     * @throws BaseException
     *             in case of any error (database failure, invalid time format, etc)
     */
    public EntityProcessorResponse process(String fromDateTimeString, String toDateTimeString, Integer limit) throws BaseException {

        OffsetDateTime fromDateTime = parseDateTime(fromDateTimeString).orElseGet(this::getDefaultFromDateTime);
        OffsetDateTime toDateTime = parseDateTime(toDateTimeString).orElseGet(this::getDefaultToDateTime);
        limit = Optional.ofNullable(limit).orElseGet(this::getDefaultLimit);

        validateInputs(fromDateTime, toDateTime, limit);

        log.debug("Processing Entities from creationDate [{0}], to [{1}], limit [{2}]!", fromDateTime, toDateTime, limit);
        int processedSize = doProcess(fromDateTime, toDateTime, limit);

        return createEntityProcessorResponse(limit, processedSize);
    }

    /**
     * Creates the {@link EntityProcessorResponse} with the requested limit and the actual prossed size.
     *
     * @param processedSizeLimit
     *            limit used for processing
     * @param processedSize
     *            actual processed size
     * @return {@link EntityProcessorResponse}
     */
    protected EntityProcessorResponse createEntityProcessorResponse(Integer processedSizeLimit, int processedSize) {
        EntityProcessorResponse entityProcessorResponse = new EntityProcessorResponse();
        entityProcessorResponse.setProcessedSizeLimit(processedSizeLimit);
        entityProcessorResponse.setProcessedSize(processedSize);
        handleSuccessResultType(entityProcessorResponse);
        return entityProcessorResponse;
    }

    /**
     * Processes the records by the given input parameters. The parameters are pre-validated with
     * {@link #validateInputs(OffsetDateTime, OffsetDateTime, int)}.
     *
     * @param fromDateTime
     *            processes the records after the specified time
     * @param toDateTime
     *            processes the records before the specified time
     * @param limit
     *            the number of maximum records to process
     * @return the number of the processed records
     * @throws BaseException
     *             in case of error
     */
    protected abstract int doProcess(OffsetDateTime fromDateTime, OffsetDateTime toDateTime, int limit) throws BaseException;

    /**
     * Validates the input parameters of the {@link #process(String, String, Integer)} function.
     *
     * @param fromDateTime
     *            start time
     * @param toDateTime
     *            end time
     * @param limit
     *            limit
     * @throws BaseException
     *             in case of validation error
     */
    protected void validateInputs(OffsetDateTime fromDateTime, OffsetDateTime toDateTime, int limit) throws BaseException {
        if (fromDateTime.isAfter(toDateTime)) {
            throw new InvalidParameterException(MessageFormat.format("fromDateTime [{0}] must be before toDateTime [{1}]", fromDateTime, toDateTime));
        }
        if (limit <= 0) {
            throw new InvalidParameterException(CoffeeFaultType.INVALID_INPUT, MessageFormat.format("limit [{0}] must be positive", limit));
        }
    }

    /**
     * Parses the time input parameter to {@link OffsetDateTime} by default in {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} format.
     *
     * @param dateTime
     *            time {@link String}
     * @return {@link Optional} {@link OffsetDateTime}
     * @throws BaseException
     *             in case of parse failure
     */
    protected Optional<OffsetDateTime> parseDateTime(String dateTime) throws BaseException {
        return Optional.ofNullable(DateUtil.tryToParseToOffsetDateTime(dateTime));
    }

    /**
     * Returns the default start time. Defaults to {@code SYSDATE - 2 weeks (at midnight)}.
     *
     * @return the default start time
     */
    protected OffsetDateTime getDefaultFromDateTime() {
        return ZonedDateTime.now().minusWeeks(2).with(LocalTime.MIDNIGHT).toOffsetDateTime();
    }

    /**
     * Returns the default end time. Defaults to {@code SYSDATE - 10 minutes}.
     *
     * @return the default end time
     */
    protected OffsetDateTime getDefaultToDateTime() {
        return ZonedDateTime.now().minusMinutes(10).toOffsetDateTime();
    }

    /**
     * Returns the default limit. Defaults to {@code 5000}.
     *
     * @return the default limit
     */
    protected int getDefaultLimit() {
        return 5000;
    }

}
