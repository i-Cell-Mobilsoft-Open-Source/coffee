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
package hu.icellmobilsoft.coffee.rest.log;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import jakarta.ws.rs.core.MediaType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseResponse;
import hu.icellmobilsoft.coffee.dto.common.commonservice.ContextType;
import hu.icellmobilsoft.coffee.dto.common.commonservice.FunctionCodeType;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;

/**
 * RequestResponseLogger class tests
 * 
 * @author peter.szabo
 */
@DisplayName("RequestResponseLogger class tests")
public class RequestResponseLoggerTest {

    private RequestResponseLogger requestResponseLogger = new RequestResponseLogger();

    @Test
    @DisplayName("Test printEntity with Json object and application/json;charset=UTF-8 media type")
    void printJsonEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8.displayName());
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [{\"context\":{\"requestId\":\"TEST\",\"timestamp\""));

    }

    @Test
    @DisplayName("Test printEntity with String object and without media type")
    void printStringEntityTest() {
        String input = "<div>Hello World</div>";
        String responseText = requestResponseLogger.printEntity(input, null, "", false, null);
        Assertions.assertEquals("entity: [" + input + "]\n", responseText);
    }

    @Test
    @DisplayName("Test printEntity with String object and without media type and UNLIMIT")
    void printStringUnlimitEntityTest() {
        String input = "<div>Hello World</div>";
        String responseText = requestResponseLogger.printEntity(input, LogSpecifier.UNLIMIT, "", false, null);
        Assertions.assertEquals("entity: [" + input + "]\n", responseText);
    }

    @Test
    @DisplayName("Test printEntity with String object and without media type and NO_LOG")
    void printStringNoLogEntityTest() {
        String input = "<div>Hello World</div>";
        String responseText = requestResponseLogger.printEntity(input, LogSpecifier.NO_LOG, "", false, null);
        Assertions.assertEquals("entity: []\n", responseText);
    }

    @Test
    @DisplayName("Test printEntity with Object object and without media type")
    void printObjectEntityTest() {
        String responseText = requestResponseLogger.printEntity(new Object(), null, "", false, null);
        Assertions.assertTrue(responseText.startsWith("entity: [java.lang.Object@"));
    }

    @Test
    @DisplayName("Test printEntity with XML object and application/xml media type")
    void printXmlEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = new MediaType("application", "xml", StandardCharsets.UTF_8.displayName());
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [<?xml"));
    }

    @Test
    @DisplayName("Test printEntity with XML object and application/atom+xml media type")
    void printAtomPlusXmlMediaTypeEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = MediaType.APPLICATION_ATOM_XML_TYPE;
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [<?xml"));
    }

    @Test
    @DisplayName("Test printEntity with XML object and text/xml media type")
    void printTextXmlMediaTypeEntityTest() {
        BaseResponse response = new BaseResponse().withMessage("msg").withFuncCode(FunctionCodeType.OK)
                .withContext(new ContextType().withRequestId("TEST").withTimestamp(OffsetDateTime.now()));
        MediaType mediaType = MediaType.TEXT_XML_TYPE;
        String responseText = requestResponseLogger.printEntity(response, null, "", false, mediaType);
        Assertions.assertTrue(responseText.startsWith("entity: [<?xml"));
    }
}
