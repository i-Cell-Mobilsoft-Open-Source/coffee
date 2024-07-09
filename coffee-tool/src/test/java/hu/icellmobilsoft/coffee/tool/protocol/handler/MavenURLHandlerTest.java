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
package hu.icellmobilsoft.coffee.tool.protocol.handler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import hu.icellmobilsoft.coffee.tool.protocol.handler.MavenURLHandler;

/**
 * @author imre.scheffer
 *
 */
@DisplayName("Testing maven URL")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MavenURLHandlerTest {

    @Order(1)
    @Test
    @DisplayName("new URL without handler")
    void newUrlException() throws IOException {
        Assertions.assertThrows(MalformedURLException.class, () -> new URL("maven:"));
    }

    // The order matters because URL.setURLStreamHandlerFactory applies to the entire JVM runtime
    @Order(2)
    @Test
    @DisplayName("new URL with hadler")
    void newUrlMavenRegistered() throws IOException {
        URL.setURLStreamHandlerFactory(protocol -> "maven".equals(protocol) ? new MavenURLHandler() : null);

        Assertions.assertAll(() -> new URL("maven:"));
    }
}
