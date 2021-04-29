/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Json &lt;-&gt; Duration converter.
 *
 * @author bence.agocsi-kiss
 * @since 1.5.0
 */
public class DurationConverter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(DurationConverter.class);

    public JsonElement serialize(Duration duration, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(String.valueOf(duration));
    }

    public Duration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            return DatatypeFactory.newInstance().newDuration(jsonElement.getAsString());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }
}
