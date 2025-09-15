/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.rest.action;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.common.commonentity.EntityProcessorResponse;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.date.DateUtil;

/**
 * General class to assist with entity processing
 *
 * @author tamas.cserhati
 * @author martin.nagy
 * @since 2.11.0
 */
public abstract class AbstractEntityProcessorAction extends AbstractBaseAction {

    /**
     * log pattern
     */
    protected static final String LOG_PATTERN = "System call published [{0}] records to [{1}] stream. FromDateTime: [{2}], toDateTime: [{3}], limit: [{4}]";

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * constructor
     */
    public AbstractEntityProcessorAction() {
    }

    /**
     * Processes the required records based on the given parameters. Prepares the inputs for the {@link #doProcess} call.
     *
     * @param fromDateTimeString
     *            (Optional) Searches for packages after the specified time (Default: {@code SYSDATE} - 2 weeks)
     * @param toDateTimeString
     *            (Optional) Searches for packages before the specified time (Default: {@code SYSDATE} - 10 minutes)
     * @param limit
     *            (Optional) Maximum number of items to be processed
     * @return {@link EntityProcessorResponse}
     * @throws BaseException
     *             Database error, invalid date format
     */
    public EntityProcessorResponse process(String fromDateTimeString, String toDateTimeString, Integer limit) throws BaseException {
        OffsetDateTime fromDateTime = parseDate(fromDateTimeString).orElseGet(this::getDefaultFromDate);

        OffsetDateTime toDateTime = parseDate(toDateTimeString).orElseGet(this::getDefaultToDate);

        limit = Optional.ofNullable(limit).orElseGet(this::getDefaultLimit);

        validateInputs(fromDateTime, toDateTime, limit);

        log.debug("Processing Entities from creationDate [{0}], to [{1}], limit [{2}]!", fromDateTime, toDateTime, limit);
        int processedSize = doProcess(fromDateTime, toDateTime, limit);

        return createEntityProcessorResponse(limit, processedSize);
    }

    /**
     * The response will include the actual limit and how many items were processed from it.
     *
     * @param processedSizeLimit
     *            Maximum number of items to be processed
     * @param processedSize
     *            Number of items processed
     * @return EntityProcessorResponse
     * @throws BaseException
     *             if any error occurs
     */
    public EntityProcessorResponse createEntityProcessorResponse(Integer processedSizeLimit, int processedSize) throws BaseException {
        EntityProcessorResponse entityProcessorResponse = new EntityProcessorResponse();
        entityProcessorResponse.setProcessedSizeLimit(processedSizeLimit);
        entityProcessorResponse.setProcessedSize(processedSize);
        handleSuccessResultType(entityProcessorResponse);
        return entityProcessorResponse;
    }

    /**
     * Processes the required records based on the given parameters.
     *
     * @param fromDateTime
     *            Searches for packages after the specified time
     * @param toDateTime
     *            Searches for packages before the specified time
     * @param limit
     *            Maximum number of items to be processed
     * @return the number of processed items
     * @throws BaseException
     *             if any error occurs
     */
    protected abstract int doProcess(OffsetDateTime fromDateTime, OffsetDateTime toDateTime, int limit) throws BaseException;

    /**
     * Returns the default value of the limit. It is only called when the limit is {@code null}.
     *
     * @return the default value of the limit
     */
    protected int getDefaultLimit() {
        return 5000;
    }

    /**
     * Common validation of input data
     * 
     * @param fromDateTime
     *            from date
     * @param toDateTime
     *            to date
     * @param limit
     *            max count of record
     * @throws BaseException
     *             if any error occurs
     */
    protected void validateInputs(OffsetDateTime fromDateTime, OffsetDateTime toDateTime, int limit) throws BaseException {
        if (fromDateTime.isAfter(toDateTime)) {
            throw new InvalidParameterException(
                    CoffeeFaultType.INVALID_INPUT,
                    MessageFormat.format("fromDateTime [{0}] must be before toDateTime [{1}]", fromDateTime, toDateTime));
        }

        if (limit <= 0) {
            throw new InvalidParameterException(CoffeeFaultType.INVALID_INPUT, MessageFormat.format("limit [{0}] must be positive", limit));
        }
    }

    /**
     * tries to parse the date
     * 
     * @param isoDateTime
     *            the date to parse
     * @return the date or {@link Optional#empty()}
     * @throws BaseException
     *             if any error occurs
     */
    protected Optional<OffsetDateTime> parseDate(String isoDateTime) throws BaseException {
        return Optional.ofNullable(DateUtil.tryToParseAbsoluteRelativeDate(isoDateTime));
    }

    /**
     * Default from date -2 weeks, can be overriden
     * 
     * @return the default from date
     */
    protected OffsetDateTime getDefaultFromDate() {
        return ZonedDateTime.now().minusWeeks(2).with(LocalTime.MIDNIGHT).toOffsetDateTime();
    }

    /**
     * Default from date -10 minutes, can be overriden
     * 
     * @return the default to date
     */
    protected OffsetDateTime getDefaultToDate() {
        return ZonedDateTime.now().minusMinutes(10).toOffsetDateTime();
    }
}
