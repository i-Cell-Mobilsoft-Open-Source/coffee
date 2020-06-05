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
package hu.icellmobilsoft.coffee.tool.gson;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.time.OffsetDateTime;

import org.jboss.logging.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import hu.icellmobilsoft.coffee.tool.utils.date.DateParseHelper;
import hu.icellmobilsoft.coffee.tool.utils.date.DatePrintUtil;

/**
 * Json <-> OffsetDateTime
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 * @since 1.0.0
 */
public class OffsetDateTimeConverter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(OffsetDateTimeConverter.class);

    /** {@inheritDoc} */
    @Override
    public OffsetDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String offsetDateTimAsString = json.getAsString();
        try {
            return DateParseHelper.parseOffsetDateTimeEx(offsetDateTimAsString);
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}]!", offsetDateTimAsString);
            LOGGER.error(msg, e);
            throw new JsonParseException(msg, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(OffsetDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            String marshalled = DatePrintUtil.isoOffsetDateTime(src);
            return new JsonPrimitive(marshalled);
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not serialize src:[{0}]!", src);
            LOGGER.error(msg, e);
            throw new JsonParseException(msg, e);
        }
    }
}
