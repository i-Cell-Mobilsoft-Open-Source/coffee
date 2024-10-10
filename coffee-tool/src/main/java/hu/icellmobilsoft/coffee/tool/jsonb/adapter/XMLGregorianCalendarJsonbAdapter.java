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
import java.util.GregorianCalendar;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.date.DateXmlUtil;

/**
 * {@link Duration} jsonb adapter
 *
 * @author bucherarnold
 * @since 2.9.0
 */
public class XMLGregorianCalendarJsonbAdapter implements JsonbAdapter<XMLGregorianCalendar, String> {

    private static Logger LOGGER = Logger.getLogger(XMLGregorianCalendarJsonbAdapter.class);

    /**
     * Default constructor, constructs a new object.
     */
    public XMLGregorianCalendarJsonbAdapter() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public String adaptToJson(XMLGregorianCalendar calendar) {
        return calendar.toXMLFormat();
    }

    /** {@inheritDoc} */
    @Override
    public XMLGregorianCalendar adaptFromJson(String calendar) {
        try {
            XMLGregorianCalendar xmlCal = toXMLGregorianCalendar(calendar);
            return xmlCal.normalize();
        } catch (Exception e) {
            String msg = MessageFormat.format("Could not deserialize value:[{0}]!", calendar);
            LOGGER.error(msg, e);
            throw new JsonbException(msg, e);
        }
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(String string) {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(Long.parseLong(string));
            return DateXmlUtil.getDatatypeFactory().newXMLGregorianCalendar(calendar);
        } catch (NumberFormatException e) {
            return DateXmlUtil.getDatatypeFactory().newXMLGregorianCalendar(string);
        }
    }
}
