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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.rest.validation.xml.reader.JsonRequestVersionReader;
import hu.icellmobilsoft.coffee.rest.xmlvalidation.reader.dto.ContextDto;
import hu.icellmobilsoft.coffee.rest.xmlvalidation.reader.dto.JsonDto;

/**
 * @author imre.scheffer
 *
 */
@DisplayName("Testing JsonRequestVersionReaderTest")
public class JsonRequestVersionReaderTest {

    @Test
    @DisplayName("Testing read existing requestVersion from JSON object")
    void readFromXML() throws TechnicalException {
        // Given
        ContextDto contextDto = new ContextDto();
        contextDto.setRequestVersion("1.1");
        JsonDto jsonDto = new JsonDto();
        jsonDto.setContext(contextDto);

        JsonRequestVersionReader reader = new JsonRequestVersionReader();

        // When
        String requestVersion = reader.readFromJSON(jsonDto);
        // Then
        assertEquals("1.1", requestVersion);
    }
}
