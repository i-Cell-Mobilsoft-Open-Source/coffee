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
package hu.icellmobilsoft.coffee.module.mongodb.codec.time.xmlgregoriancalendar;

import java.text.MessageFormat;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * MongoDB XMLGregorianCalendar <-> Date converter codec
 *
 * @author robert.kaplar
 * @since 1.0.0
 */
public class XMLGregorianCalendarCodec implements Codec<XMLGregorianCalendar> {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(XMLGregorianCalendarCodec.class);

    /** {@inheritDoc} */
    @Override
    public void encode(BsonWriter writer, XMLGregorianCalendar value, EncoderContext encoderContext) {
        writer.writeDateTime(value.toGregorianCalendar().toInstant().toEpochMilli());
    }

    /** {@inheritDoc} */
    @Override
    public XMLGregorianCalendar decode(BsonReader reader, DecoderContext decoderContext) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(reader.readDateTime());
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            String msg = MessageFormat.format("XMLGregorianCalendarCodec DatatypeConfigurationException=[{0}] occured", e.getLocalizedMessage());
            LOGGER.error(msg, e);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Class<XMLGregorianCalendar> getEncoderClass() {
        return XMLGregorianCalendar.class;
    }

}
