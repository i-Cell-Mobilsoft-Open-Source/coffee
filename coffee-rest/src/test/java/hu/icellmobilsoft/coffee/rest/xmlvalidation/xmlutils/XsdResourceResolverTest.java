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
package hu.icellmobilsoft.coffee.rest.xmlvalidation.xmlutils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.w3c.dom.ls.LSInput;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.rest.validation.xml.utils.XsdResourceResolver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Testing XsdResourceResolver")
public class XsdResourceResolverTest {

    @Test
    @DisplayName("Testing resolve resource with empty path param")
    void xsdResourceResolverCreateWithEmptyPath() {
        // Given
        XsdResourceResolver xsdResourceResolver = new XsdResourceResolver();
        xsdResourceResolver.setXsdDirPath("");
        // When
        LSInput input = xsdResourceResolver.resolveResource(null, null, null, "", null);
        // Then
        assertNull(input);
    }

    @Test
    @DisplayName("Testing resolve resource with good params")
    void resolveResourceWithGoodParams() throws BaseException {
        // kihasznalja, hogy az src/test/resource-ban megtalalhato a file
        // Given
        XsdResourceResolver xsdResourceResolver = new XsdResourceResolver();
        xsdResourceResolver.setXsdDirPath("./reqversion.xml");
        // When
        LSInput input = xsdResourceResolver.resolveResource(null, null, null, "reqversion.xml", null);
        // Then
        assertNotNull(input.getByteStream());
    }

    @Test
    @DisplayName("Testing resolve resource with wrong resource")
    void resolveResourceWithWrongResource() throws BaseException {
        // Given
        XsdResourceResolver xsdResourceResolver = new XsdResourceResolver();
        xsdResourceResolver.setXsdDirPath("wrong/wrong.xsd");
        // When
        LSInput lsInput = xsdResourceResolver.resolveResource(null, null, null, "wrong.xsd", null);
        // Then
        assertNull(lsInput);
    }
}
