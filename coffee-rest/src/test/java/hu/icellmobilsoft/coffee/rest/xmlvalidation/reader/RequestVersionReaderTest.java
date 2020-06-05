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
package hu.icellmobilsoft.coffee.rest.xmlvalidation.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.XmlRequestVersionReader;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing XmlRequestVersionReader")
public class RequestVersionReaderTest {

    private XmlRequestVersionReader requestVersionReader = new XmlRequestVersionReader();

    @Test
    @DisplayName("Testing read existing requestVersion from xml")
    void readFromXML() throws TechnicalException {
        // Given
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("reqversion.xml");
        // When
        String requestVersion = requestVersionReader.readFromXML(is);
        // Then
        assertEquals("1.9", requestVersion);
    }

    @Test
    @DisplayName("Testing not existing requestVersion in xml")
    void readXmlWithNotExistingRequestVersion() {
        // Given
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("reqversion_not_exists.xml");
        // When
        TechnicalException te = assertThrows(TechnicalException.class, () -> {
            requestVersionReader.readFromXML(is);
        });
        // Then
        assertTrue(te.getMessage().contains("Error in read inputstream"));
    }

    @Test
    @DisplayName("Testing when inputStream is null")
    void readWhenInputStreamIsNull() throws TechnicalException {
        // Given
        InputStream is = null;
        // When
        String requestVersion = requestVersionReader.readFromXML(is);
        // Then
        assertEquals(null, requestVersion);
    }
}
