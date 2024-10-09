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
package hu.icellmobilsoft.coffee.rest.validation.xml.reader;

import java.io.InputStream;
import java.text.MessageFormat;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * It reads the requestVersion value from XML stream, try not to use it, instead pass the requestversion in header or url.
 *
 * Example of the required xml structure:
 *
 * <pre>
 * &lt;xml&gt;
 *     ...
 *     &lt;header&gt;
 *         &lt;requestVersion&gt;1.1&lt;/requestVersion&gt;
 *         ...
 *     &lt;/header&gt;
 *     ...
 * &lt;/xml&gt;
 * </pre>
 *
 * @see IRequestVersionReader
 * @author imre.scheffer
 * @author ferenc.lutischan
 * @since 1.0.0
 */
@Alternative
@Dependent
public class XmlRequestVersionReader implements IRequestVersionReader {

    private static final String STREAM_EXCEPTION = "Premature end of file";

    /**
     * Default constructor, constructs a new object.
     */
    public XmlRequestVersionReader() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * It returns header.requestVersion. It's capable of extracting other parameters as well, but currently not needed.
     */
    @Override
    public String readVersion(InputStream is) throws TechnicalException {
        if (is == null) {
            return null;
        }
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // Do we need this line?: 1. The security issue is theoretically handled in the configuration. 2. We do not return read data.
        inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(is);
            return readDocument(reader);
        } catch (XMLStreamException e) {
            throw new TechnicalException(CoffeeFaultType.INVALID_REQUEST,
                    MessageFormat.format("Error in read inputstream: [{0}]", e.getLocalizedMessage()), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    Logger.getLogger(XmlRequestVersionReader.class).warn(e.getLocalizedMessage());
                }
            }
        }
    }

    private String readDocument(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                if (elementName.equals("header")) {
                    return readHeader(reader);
                }
            }
        }
        throw new XMLStreamException(STREAM_EXCEPTION);
    }

    private String readHeader(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int eventType = reader.next();
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                if (elementName.equals("requestVersion")) {
                    return readCharacters(reader);
                }
            }
        }
        throw new XMLStreamException(STREAM_EXCEPTION);
    }

    private String readCharacters(XMLStreamReader reader) throws XMLStreamException {
        StringBuilder result = new StringBuilder();
        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
            case XMLStreamConstants.CHARACTERS:
            case XMLStreamConstants.CDATA:
                result.append(reader.getText());
                break;
            case XMLStreamConstants.END_ELEMENT:
                return result.toString();
            }
        }
        throw new XMLStreamException(STREAM_EXCEPTION);
    }
}
