/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.jsonb.adapter;

import java.text.MessageFormat;
import java.util.Date;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * {@link Date} jsonb adapter
 *
 * @author bucherarnold
 * @since 2.9.0
 */
public class DateJsonbAdapter implements JsonbAdapter<Date, Long> {

    private static Logger LOGGER = Logger.getLogger(DateJsonbAdapter.class);

    /**
     * Default constructor, constructs a new object.
     */
    public DateJsonbAdapter() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Long adaptToJson(Date date) {
        return date.getTime();
    }

    /** {@inheritDoc} */
    @Override
    public Date adaptFromJson(Long date) {
        if (date == null) {
            return null;
        }
        try {
            return new Date(date);
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}], returning null!", date);
            LOGGER.error(msg, e);
            throw new JsonbException(msg, e);
        }
    }
}
