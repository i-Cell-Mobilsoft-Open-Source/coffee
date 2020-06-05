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
package hu.icellmobilsoft.coffee.tool.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.tool.properties.PropertyReader;

/**
 * @author mark.petrenyi
 */
@DisplayName("Testing PropertyReader")
class PropertyReaderTest {

    public static final String PROPERTY_READER_TEST_PROPERTIES = "PropertyReaderTest.properties";
    public static final String CONFIG_FILE_KEY = "application.configurationFile";
    private PropertyReader underTest;
    private static final String TEST_KEY = "testKey";

    @BeforeEach
    void setUp() {
        underTest = PropertyReader.getInstance();
    }

    @BeforeAll
    static void setUpBeforeClass() {
        URL resourceURL = PropertyReaderTest.class.getClassLoader().getResource(PROPERTY_READER_TEST_PROPERTIES);
        if (resourceURL == null) {
            fail(String.format("Resource:[%s] is not found!", PROPERTY_READER_TEST_PROPERTIES));
        }
        String resourcePath = resourceURL.getPath();
        System.setProperty(CONFIG_FILE_KEY, resourcePath);
    }

    @AfterAll
    static void tearDownAfterClass() {
        System.clearProperty(CONFIG_FILE_KEY);
    }

    @Nested
    @DisplayName("Testing getSystemProperty()")
    class GetSystemPropertyTest {

        private static final String CLASS_TEST_VALUE = "classTestValue";
        public static final String DEFAULT_VALUE = "defaultValue";
        public static final String TEST_VALUE = "testValue";

        @Test
        @DisplayName("Testing getSystemProperty() with class provided, key is present")
        void withCLass() {
            // given
            String testInput = TEST_KEY;
            String expected = CLASS_TEST_VALUE;

            // when
            String actual = underTest.getSystemProperty(PropertyReaderTest.class, testInput);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Testing getSystemProperty() with class and defaultValue provided, key is not present")
        void withCLassWithDefault() {
            // given
            String testInput = TEST_KEY + "notExisting";
            String expected = DEFAULT_VALUE;

            // when
            String actual = underTest.getSystemProperty(PropertyReaderTest.class, testInput, DEFAULT_VALUE);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Testing getSystemProperty() without defaultValue provided, key is not present")
        void withCLassWithoutDefault() {
            // given
            String testInput = TEST_KEY + "notExisting";
            String expected = null;

            // when
            String actual = underTest.getSystemProperty(PropertyReaderTest.class, testInput);

            // then
            assertEquals(expected, actual);
        }

        @Test
        @DisplayName("Testing getSystemProperty() without class provided, key is present")
        void withoutClass() {
            // given
            String expected = TEST_VALUE;

            // when
            String actual = underTest.getSystemProperty(null, TEST_KEY);
            assertEquals(expected, actual);
        }
    }
}
