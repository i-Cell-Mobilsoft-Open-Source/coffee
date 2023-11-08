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
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Json &lt;-&gt; Date converter.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class DateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private static Logger LOGGER = Logger.getLogger(DateConverter.class);

    /**
     * Default constructor, constructs a new object.
     */
    public DateConverter() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(date.getTime());
    }

    /** {@inheritDoc} */
    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return new Date(jsonElement.getAsLong());
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}], returning null!", jsonElement.getAsLong());
            LOGGER.error(msg, e);
            throw new JsonParseException(msg, e);
        }
    }
}
