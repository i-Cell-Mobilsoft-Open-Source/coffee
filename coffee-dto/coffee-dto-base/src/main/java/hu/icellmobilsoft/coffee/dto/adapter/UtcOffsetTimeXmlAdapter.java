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

import java.time.OffsetTime;
import java.time.ZoneOffset;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@code XmlAdapter} mapping JSR-310 {@code OffsetTime} to UTC string
 *
 * @author peter.kovacs
 * @see XmlAdapter
 * @see OffsetTime
 * @since 1.6.0
 */
public class UtcOffsetTimeXmlAdapter extends OffsetTimeXmlAdapter {


    /**
     * {@inheritDoc}
     * <p>
     * OffsetTime to String.
     * Input format is '15:07:34.2160856+02:00'.
     * Output format is '13:07:34.216085600Z'.
     */
    @Override
    public String marshal(OffsetTime offsetTime) {
        if (offsetTime == null) {
            return null;
        }
        return offsetTime.withOffsetSameInstant(ZoneOffset.UTC).toString();
    }
}
