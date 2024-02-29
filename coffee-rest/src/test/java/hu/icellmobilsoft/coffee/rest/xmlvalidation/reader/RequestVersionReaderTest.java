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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.EmptyRequestVersionReader;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.XmlRequestVersionReader;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing XmlRequestVersionReader")
public class RequestVersionReaderTest {

    private XmlRequestVersionReader requestVersionReader = new XmlRequestVersionReader();
    private EmptyRequestVersionReader emptyVersionReader = new EmptyRequestVersionReader();

    @Test
    @DisplayName("Testing read existing requestVersion from xml")
    void readFromXML() throws TechnicalException {
        // Given
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("reqversion.xml");
        // When
        String requestVersion = requestVersionReader.readVersion(is);
        // Then
        assertEquals("1.9", requestVersion);
    }

    @Test
    @DisplayName("Testing not existing requestVersion in xml")
    void readXmlWithNotExistingRequestVersion() {
        // Given
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("reqversion_not_exists.xml");
        // When
        TechnicalException te = assertThrows(TechnicalException.class, () -> {
            requestVersionReader.readVersion(is);
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
        String requestVersion = requestVersionReader.readVersion(is);
        // Then
        assertEquals(null, requestVersion);
    }

    @Test
    @DisplayName("Testing read existing requestVersion from xml")
    void readEmptyFromXML() throws TechnicalException {
        // Given
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("reqversion.xml");
        // When
        String requestVersion = emptyVersionReader.readVersion(is);
        // Then
        assertNull(requestVersion);
    }

    @Test
    @DisplayName("Testing not existing requestVersion in xml")
    void readEmptyXmlWithNotExistingRequestVersion() throws TechnicalException {
        // Given
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("reqversion_not_exists.xml");
        // When
        String requestVersion = emptyVersionReader.readVersion(is);
        // Then
        assertNull(requestVersion);
    }

    @Test
    @DisplayName("Testing when inputStream is null")
    void readEmptyWhenInputStreamIsNull() throws TechnicalException {
        // Given
        InputStream is = null;
        // When
        String requestVersion = emptyVersionReader.readVersion(is);
        // Then
        assertNull(requestVersion);
    }

}
