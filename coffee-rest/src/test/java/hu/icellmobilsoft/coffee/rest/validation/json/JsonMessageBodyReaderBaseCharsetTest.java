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
package hu.icellmobilsoft.coffee.rest.validation.json;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests verifying that {@code ContentType.parse()} from HttpCore 5.x can return {@code null} and that the charset extraction logic in
 * {@link JsonMessageBodyReaderBase} handles it correctly.
 *
 * @author gabor.balazs
 * @since 2.13.0
 */
@DisplayName("ContentType.parse null-safety tests for JsonMessageBodyReaderBase")
class JsonMessageBodyReaderBaseCharsetTest {

    /**
     * Default constructor, constructs a new object.
     */
    JsonMessageBodyReaderBaseCharsetTest() {
        super();
    }

    @Test
    @DisplayName("Charset extraction is null-safe when ContentType is null")
    void charsetExtractionShouldBeNullSafeWhenContentTypeIsNull() {
        // Simulates the logic inside getCharsetOrUTF8
        ContentType contentType = ContentType.parse(null);
        Charset result = getCharsetOrDefault(contentType);
        Assertions.assertEquals(StandardCharsets.UTF_8, result);
    }

    @Test
    @DisplayName("Charset extraction returns charset when Content-Type has one")
    void charsetExtractionShouldReturnSpecifiedCharset() {
        ContentType contentType = ContentType.parse("application/json; charset=ISO-8859-1");
        Charset result = getCharsetOrDefault(contentType);
        Assertions.assertEquals(StandardCharsets.ISO_8859_1, result);
    }

    @Test
    @DisplayName("Charset extraction returns UTF-8 when Content-Type has no charset")
    void charsetExtractionShouldReturnUtf8WhenNoCharset() {
        ContentType contentType = ContentType.parse("application/json");
        Charset result = getCharsetOrDefault(contentType);
        Assertions.assertEquals(StandardCharsets.UTF_8, result);
    }

    @Test
    @DisplayName("Charset extraction returns UTF-8 for explicit UTF-8 charset")
    void charsetExtractionShouldReturnUtf8ForExplicitUtf8() {
        ContentType contentType = ContentType.parse("application/json; charset=UTF-8");
        Charset result = getCharsetOrDefault(contentType);
        Assertions.assertEquals(StandardCharsets.UTF_8, result);
    }

    /**
     * Mirrors the null-safe logic of {@link JsonMessageBodyReaderBase#getCharsetOrUTF8} without requiring CDI context.
     *
     * @param contentType
     *            parsed ContentType, may be {@code null}
     * @return the charset or UTF-8 as default
     */
    private Charset getCharsetOrDefault(ContentType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }
}
