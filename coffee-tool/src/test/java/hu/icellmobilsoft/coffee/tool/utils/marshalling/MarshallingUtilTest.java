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
package hu.icellmobilsoft.coffee.tool.utils.marshalling;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Test MarshallingUtil.
 *
 * @author balazs.joo
 */
@DisplayName("Testing MarshallingUtil")
public class MarshallingUtilTest {

    private String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testXmlObject><string>test</string></testXmlObject>";
    private static final TestXmlObject testXmlObject = new TestXmlObject();

    @BeforeAll
    static void setUpBeforeClass() {
        testXmlObject.setString("test");
    }

    @DisplayName("Testing marshall()")
    @Test
    void marshall() {

        String actual = MarshallingUtil.marshall(testXmlObject);

        Assertions.assertEquals(xml, actual);
    }

    @DisplayName("Testing marshallUncheckedXml()")
    @Test
    void marshallUncheckedXml() throws JAXBException {

        String actual = MarshallingUtil.marshallUncheckedXml(testXmlObject);

        Assertions.assertEquals(xml, actual);
    }

    @DisplayName("Testing marshallUncheckedXml()")
    @Test
    void marshallUncheckedXmlWithClass() throws JAXBException {

        String actual = MarshallingUtil.marshallUncheckedXml(testXmlObject, TestXmlObject.class);

        Assertions.assertEquals(xml, actual);
    }

    @DisplayName("Testing marshall()")
    @Test
    void marshallWithOutputStream() {

        OutputStream os = new ByteArrayOutputStream();
        MarshallingUtil.marshall(testXmlObject, os);

        Assertions.assertEquals(xml, os.toString());
    }

    @DisplayName("Testing marshall()")
    @Test
    void marshallWithOutputStreamAndClass() {

        OutputStream os = new ByteArrayOutputStream();
        MarshallingUtil.marshall(testXmlObject, os, testXmlObject.getClass());

        Assertions.assertEquals(xml, os.toString());
    }

    @DisplayName("Testing marshallUncheckedXml()")
    @Test
    void marshallUncheckedXmlWithOutputStream() throws JAXBException {

        OutputStream os = new ByteArrayOutputStream();
        MarshallingUtil.marshallUncheckedXml(testXmlObject, os);

        Assertions.assertEquals(xml, os.toString());
    }

    @DisplayName("Testing marshallUncheckedXml()")
    @Test
    void marshallUncheckedXmlWithOutputStreamAndClass() throws JAXBException {

        OutputStream os = new ByteArrayOutputStream();
        MarshallingUtil.marshallUncheckedXml(testXmlObject, os, testXmlObject.getClass());

        Assertions.assertEquals(xml, os.toString());
    }

    @DisplayName("Testing unmarshall()")
    @Test
    void unmarshall() {

        TestXmlObject actual = MarshallingUtil.unmarshall(xml, TestXmlObject.class);

        Assertions.assertNotNull(testXmlObject);
        Assertions.assertEquals(testXmlObject.getString(), actual.getString());
    }

    @DisplayName("Testing unmarshallUncheckedXml()")
    @Test
    void unmarshallUncheckedXml() {

        TestXmlObject actual = MarshallingUtil.unmarshallUncheckedXml(xml, TestXmlObject.class);

        Assertions.assertNotNull(testXmlObject);
        Assertions.assertEquals(testXmlObject.getString(), actual.getString());
    }

    @Test
    @DisplayName("Testing null values")
    void nullValues() throws JAXBException {
        Marshaller marshaller = null;
        OutputStream testOutputStream = new ByteArrayOutputStream();

        // marshall
        Assertions.assertNull(MarshallingUtil.marshall(null));
        Assertions.assertNull(MarshallingUtil.marshall(""));
        Assertions.assertNull(MarshallingUtil.marshall(" "));
        Assertions.assertNull(MarshallingUtil.marshall("AAbbcc"));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(null, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(null, testOutputStream));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(testXmlObject, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(null, null, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(null, testOutputStream, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(null, testOutputStream, testXmlObject.getClass()));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(null, null, testXmlObject.getClass()));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(testXmlObject, null, testXmlObject.getClass()));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall(testXmlObject, null, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall("AAbbcc", testOutputStream));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshall("AAbbcc", testOutputStream, testXmlObject.getClass()));

        // marshall unchecked xml
        Assertions.assertNull(MarshallingUtil.marshallUncheckedXml(null));
        Assertions.assertNull(MarshallingUtil.marshallUncheckedXml(null, testXmlObject.getClass()));
        Assertions.assertNull(MarshallingUtil.marshallUncheckedXml(null, marshaller));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshallUncheckedXml(null, testOutputStream));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshallUncheckedXml(null, null, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshallUncheckedXml(null, testOutputStream, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.marshallUncheckedXml(testXmlObject, null, testXmlObject.getClass()));
        Assertions.assertThrows(JAXBException.class, () -> MarshallingUtil.marshallUncheckedXml("AAbbcc", testOutputStream));

        // unmarshall
        Assertions.assertNull(MarshallingUtil.unmarshall(null, null));
        Assertions.assertNull(MarshallingUtil.unmarshall(null, testXmlObject.getClass()));
        Assertions.assertNull(MarshallingUtil.unmarshall(xml, null));
        Assertions.assertNull(MarshallingUtil.unmarshall("AAbbcc", testXmlObject.getClass()));

        // unmarshall unchecked xml
        Assertions.assertNull(MarshallingUtil.unmarshallUncheckedXml(null, null));
        Assertions.assertNull(MarshallingUtil.unmarshallUncheckedXml(xml, null));
        Assertions.assertNull(MarshallingUtil.unmarshallUncheckedXml(null, testXmlObject.getClass()));
        Assertions.assertNull(MarshallingUtil.unmarshallUncheckedXml("AAbbcc", testXmlObject.getClass()));

        // fillOptionalField
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.fillOptionalField(null, null, null));
        Assertions.assertDoesNotThrow(() -> MarshallingUtil.fillOptionalField(testXmlObject, null, null));
    }

}
