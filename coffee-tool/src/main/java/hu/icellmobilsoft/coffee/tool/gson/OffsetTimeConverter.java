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
import java.time.OffsetTime;

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
 * Json <-> OffsetTime
 *
 * @author mark.petrenyi
 * @author imre.scheffer
 * @since 1.0.0
 */
public class OffsetTimeConverter implements JsonSerializer<OffsetTime>, JsonDeserializer<OffsetTime> {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(OffsetTimeConverter.class);

    /** {@inheritDoc} */
    @Override
    public OffsetTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString();
        try {
            return DateParseHelper.parseOffsetTimeEx(value);
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}], returning null!", value);
            LOGGER.error(msg, e);
            throw new JsonParseException(msg, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(OffsetTime src, Type typeOfSrc, JsonSerializationContext context) {
        try {
            Object marshalled = DatePrintUtil.isoOffsetTime(src);
            return new JsonPrimitive((String) marshalled);
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not serialize src:[{0}]!", src);
            LOGGER.error(msg, e);
            throw new JsonParseException(msg, e);
        }
    }
}
