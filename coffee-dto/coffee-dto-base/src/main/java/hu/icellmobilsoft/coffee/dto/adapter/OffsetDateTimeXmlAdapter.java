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
package hu.icellmobilsoft.coffee.dto.adapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@code XmlAdapter} mapping JSR-310 {@code OffsetDateTime} to ISO-8601 string or epoch milli long
 * <p>
 * String format details: {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}, {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}
 *
 * @see XmlAdapter
 * @see OffsetDateTime
 * @author mark.petrenyi
 * @since 1.0.0
 */
public class OffsetDateTimeXmlAdapter extends XmlAdapter<String, OffsetDateTime> {

    /**
     * {@inheritDoc}
     *
     * Unmarshalling object to {@link OffsetDateTime}
     */
    @Override
    public OffsetDateTime unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        /**
         * Object to marshall as OffsetDateTime. Valid values are UTC time in millis represented by {@link Number} or {@link String}; and
         * ISO_OFFSET_DATE_TIME or ISO_LOCAL_DATE_TIME ('2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30') representation of date as {@link String}
         */
        try {
            Long timeInMillis = Long.parseLong(v);
            return parseLong(timeInMillis);
        } catch (NumberFormatException e) {
            return parseString(v);
        }
    }

    private OffsetDateTime parseString(String from) {
        try {
            return OffsetDateTime.parse(from, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException ex) {
            LocalDateTime localDateTime = LocalDateTime.parse(from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
        }
    }

    private OffsetDateTime parseLong(Long timeInMillis) {
        Instant instant = Instant.ofEpochMilli(timeInMillis);
        return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * {@inheritDoc}
     *
     * OffsetDateTime to String. Output format is '2011-12-03T10:15:30+01:00'.
     * 
     * @see DateTimeFormatter#ISO_OFFSET_DATE_TIME
     */
    @Override
    public String marshal(OffsetDateTime offsetDateTime) throws Exception {
        if (offsetDateTime == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime);
    }
}
