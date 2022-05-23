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

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@code XmlAdapter} mapping JSR-310 {@code OffsetDateTime} to UTC string
 *
 * @author peter.kovacs
 * @see XmlAdapter
 * @see OffsetTime
 * @since 1.6.0
 */
public class UtcOffsetDateTimeXmlAdapter extends OffsetDateTimeXmlAdapter {


    /**
     * {@inheritDoc}
     * <p>
     * OffsetDateTime to String.
     * Input format is '2021-08-27T13:33:00.000+02:00'.
     * Output format is '2021-08-27T11:33:00Z'.
     */
    @Override
    public String marshal(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC));
    }
}
