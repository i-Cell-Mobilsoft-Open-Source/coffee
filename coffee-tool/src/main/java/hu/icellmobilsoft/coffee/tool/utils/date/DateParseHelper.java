/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.date;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;

/**
 * Helper osztály, java 8 time-ok parsolásához
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@Vetoed
public class DateParseHelper {

    /**
     * String to {@code OffsetDateTime}
     *
     * @param from
     *            Parse string as OffsetDateTime. Valid values are UTC time in millis; and ISO_OFFSET_DATE_TIME or ISO_LOCAL_DATE_TIME
     *            ('2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30') representation of date
     * @return UTC offset if input is time in millis, or ISO_LOCAL_DATE_TIME String, specified offset otherwise
     * @throws BaseException
     *             if input parameter is unparsable
     */
    public static OffsetDateTime parseOffsetDateTimeEx(String from) throws BaseException {
        if (StringUtils.isBlank(from)) {
            return null;
        }
        try {
            return parseOffsetDateTime(from);
        } catch (Exception e) {
            throw new BaseException(MessageFormat.format(
                    "Couldn''t parse value:[{0}], msg:[{1}]! Value must be millis from UNIX epoch, "
                            + "or must comfort to one of 'YYYY-MM-DDThh:mm:ss.SSS', 'YYYY-MM-DDThh:mm:ss.SSSZ', 'YYYY-MM-DDThh:mm:ss.SSS+hh:mm'!",
                    from, e.getLocalizedMessage()));
        }
    }

    private static OffsetDateTime parseOffsetDateTime(String from) {
        try {
            Long timeInMillis = Long.parseLong(from);
            Instant instant = Instant.ofEpochMilli(timeInMillis);
            return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
        } catch (NumberFormatException e) {
            try {
                return OffsetDateTime.parse(from, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (DateTimeParseException ex) {
                LocalDateTime localDateTime = LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
            }
        }
    }

    /**
     * String to {@code OffsetTime}
     *
     * @param from
     *            Parse string as OffsetTime. Valid values are ISO_OFFSET__TIME or ISO_LOCAL__TIME ('10:15:30+01:00' or '10:15:30') representation of
     *            date
     * @return UTC offset if input is ISO_LOCAL_TIME String, specified offset otherwise
     * @throws BaseException
     *             if input parameter is unparsable
     */
    public static OffsetTime parseOffsetTimeEx(String from) throws BaseException {
        if (StringUtils.isBlank(from)) {
            return null;
        }
        try {
            return parseOffsetTime(from);
        } catch (Exception e) {
            throw new BaseException(MessageFormat.format(
                    "Couldn''t parse value:[{0}], msg:[{1}]! Value must be string and comfort to one of 'hh:mm:ss.SSS', 'hh:mm:ss.SSSZ', 'hh:mm:ss.SSS+hh:mm'!",
                    from, e.getLocalizedMessage()));
        }
    }

    private static OffsetTime parseOffsetTime(String from) {
        try {
            return OffsetTime.parse(from, DateTimeFormatter.ISO_OFFSET_TIME);
        } catch (DateTimeParseException ex) {
            LocalTime localTime = LocalTime.parse(from, DateTimeFormatter.ISO_LOCAL_TIME);
            return OffsetTime.of(localTime, ZoneOffset.UTC);
        }
    }

    /**
     * Unmarshalling object to {@code LocalDate}
     *
     * @param from
     *            Parse string as LocalDate. Valid values are UTC time in millis; and ISO_DATE ('2011-12-03' or '2011-12-03+01:00') representation of
     *            date
     * @return parsed {@code LocalDate}
     * @throws BaseException
     *             if input parameter is unparsable
     */
    public static LocalDate parseLocalDateEx(String from) throws BaseException {
        if (StringUtils.isBlank(from)) {
            return null;
        }
        try {
            return parseLocalDate(from);
        } catch (Exception e) {
            throw new BaseException(MessageFormat.format("Couldn''t parse value:[{0}], msg:[{1}]! Value must be millis from UNIX epoch, "
                    + "or must comfort to one of 'YYYY-MM-DD', 'YYYY-MM-DDZ', 'YYYY-MM-DD+hh:mm'!", from, e.getLocalizedMessage()));
        }
    }

    private static LocalDate parseLocalDate(String from) {
        try {
            Long timeInMillis = Long.parseLong(from);
            Instant instant = Instant.ofEpochMilli(timeInMillis);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
        } catch (NumberFormatException e) {
            return LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        }
    }
}
