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

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@code XmlAdapter} mapping JSR-310 {@code OffsetTime} to ISO-8601 string
 * <p>
 * String format details: {@link DateTimeFormatter#ISO_OFFSET_TIME}, {@link DateTimeFormatter#ISO_LOCAL_TIME}
 *
 * @see XmlAdapter
 * @see OffsetTime
 * @author mark.petrenyi
 * @since 1.0.0
 */
public class OffsetTimeXmlAdapter extends XmlAdapter<String, OffsetTime> {

    /**
     * {@inheritDoc}
     *
     * Unmarshalling object to {@link OffsetTime}
     */
    @Override
    public OffsetTime unmarshal(String v) throws Exception {
        if (v == null) {
            return null;
        }
        /**
         * Object to marshall as OffsetTime. Valid values are ISO_OFFSET__TIME or ISO_LOCAL__TIME ('10:15:30+01:00' or '10:15:30') representation of
         * date as {@link String}
         */
        return parseString(v);
    }

    private OffsetTime parseString(String from) {
        try {
            return OffsetTime.parse(from, DateTimeFormatter.ISO_OFFSET_TIME);
        } catch (DateTimeParseException ex) {
            LocalTime localTime = LocalTime.parse(from, DateTimeFormatter.ISO_LOCAL_TIME);
            return OffsetTime.of(localTime, ZoneOffset.UTC);
        }
    }

    /**
     * {@inheritDoc}
     *
     * OffsetTime to String. Output format is '10:15:30+01:00'.
     * 
     * @see DateTimeFormatter#ISO_OFFSET_TIME
     */
    @Override
    public String marshal(OffsetTime offsetTime) throws Exception {
        if (offsetTime == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_TIME.format(offsetTime);
    }
}
