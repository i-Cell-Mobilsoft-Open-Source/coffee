/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Json &lt;-&gt; YearMonth
 * <p>
 * Handling YearMonth, \d{4}-\d{2}
 * <p>
 * {@link DateTimeFormatter#ofPattern(String)} pattern {@code yyyy-MM} {@link YearMonth}
 *
 * @author janos.boroczki
 * @since 1.13.0
 *
 */
public class YearMonthConverter implements JsonSerializer<YearMonth>, JsonDeserializer<YearMonth> {

    @Override
    public YearMonth deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            handleNotJsonPrimitive(json);
        }

        String offsetDateTimeAsString = json.getAsString();
        try {
            return YearMonth.parse(offsetDateTimeAsString);
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}]!", offsetDateTimeAsString);
            throw new JsonParseException(msg, e);
        }
    }

    @Override
    public JsonElement serialize(YearMonth src, Type type, JsonSerializationContext jsonSerializationContext) {
        try {
            return new JsonPrimitive(src.toString());
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not serialize src:[{0}]!", src);
            throw new JsonParseException(msg, e);
        }
    }

    private void handleNotJsonPrimitive(JsonElement json) {
        String jsonString = json.toString();
        String msg;
        if (json.isJsonObject()) {
            msg = MessageFormat.format("Invalid element! Element is object: [{0}]! Provide value only, such as: 2011-12!", jsonString);
        } else if (json.isJsonArray()) {
            msg = MessageFormat.format("Invalid element! Element is an array: [{0}]! Provide valid value, such as: 2011-12!", jsonString);
        } else if (json.isJsonNull()) {
            msg = "Invalid element! Element is null! Provide valid value, such as: 2011-12!";
        } else {
            msg = MessageFormat.format("Invalid element! [{0}]! Provide valid value, such as: 2011-12!", jsonString);
        }
        throw new JsonParseException(msg);
    }
}
