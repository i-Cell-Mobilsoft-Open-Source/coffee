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
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Json <-> XMLGregorianCalendar
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class XMLGregorianCalendarConverter implements JsonSerializer<XMLGregorianCalendar>, JsonDeserializer<XMLGregorianCalendar> {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(XMLGregorianCalendarConverter.class);

    /** {@inheritDoc} */
    @Override
    public JsonElement serialize(XMLGregorianCalendar calendar, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(calendar.toXMLFormat());
    }

    /** {@inheritDoc} */
    @Override
    public XMLGregorianCalendar deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        try {
            XMLGregorianCalendar xmlCal = toXMLGregorianCalendar(jsonElement.getAsString());
            return xmlCal.normalize();
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}]!", jsonElement.getAsString());
            LOGGER.error(msg, e);
            throw new JsonParseException(msg, e);
        }
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(String string) throws DatatypeConfigurationException {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.parseLong(string));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (NumberFormatException e) {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(string);
        }
    }
}
