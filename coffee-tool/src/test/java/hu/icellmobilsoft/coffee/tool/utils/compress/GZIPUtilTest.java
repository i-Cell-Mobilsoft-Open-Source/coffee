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
package hu.icellmobilsoft.coffee.tool.utils.compress;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;
import hu.icellmobilsoft.coffee.tool.utils.json.TestObject;

/**
 * @author balazs.joo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing GZIPUtil")
class GZIPUtilTest {

    private static final String XML = "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;&lt;xmlRoot&gt;&lt;xmlChild xmlField=\"xmlValue\"&gt;anotherXmlValue&lt;/xmlChild&gt;&lt;/xmlRoot&gt;";
    private static final String TEST = "test";
    private static final byte[] COMPRESSED = { 31, -117, 8, 0, 0, 0, 0, 0, 0, -1, 43, 73, 45, 46, 1, 0, 12, 126, 127, -40, 4, 0, 0, 0 };

    @Nested
    @DisplayName("Testing isCompressed()")
    class IsCompressed {

        @Test
        @DisplayName("Testing isCompressed(byte[])")
        void isCompressed() {
            Assertions.assertTrue(GZIPUtil.isCompressed(COMPRESSED));
            Assertions.assertFalse(GZIPUtil.isCompressed(TEST.getBytes()));
        }
    }

    @Nested
    @DisplayName("Testing compress()")
    class Compress {

        @Test
        @DisplayName("Testing compress(byte[])")
        void compress() throws BaseException {

            byte[] compressedByte = GZIPUtil.compress(TEST.getBytes());
            Assertions.assertArrayEquals(COMPRESSED, compressedByte);
        }

    }

    @Nested
    @DisplayName("Testing compressJson() and decompressEx()")
    class CompressDecompressJson {

        private TestObject testObject;

        @BeforeEach
        void setUp() {
            testObject = new TestObject();
            testObject.setBytes(new byte[1024]);
            testObject.setString((new String(new byte[1024])));
            testObject.setDate(new Date(Long.parseLong("1549898614051")));
        }

        @Test
        @DisplayName("Testing compressJson() and decompressEx()")
        void compressDecompress() throws BaseException {
            byte[] compressedByte = GZIPUtil.compressJson(testObject);
            Assertions.assertNotNull(compressedByte);

            TestObject decompressedTestData = GZIPUtil.decompressEx(compressedByte, TestObject.class);
            Assertions.assertEquals(testObject, decompressedTestData);
        }

        @Test
        @DisplayName("Testing compressJson() and decompressToInputStream()")
        void compressDecompressToInputStream() throws BaseException, IOException {
            byte[] compressedByte = GZIPUtil.compressJson(testObject);
            Assertions.assertNotNull(compressedByte);

            try (InputStream gzipInputStream = GZIPUtil.decompressToInputStream(compressedByte);
                    InputStreamReader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                Assertions.assertEquals(testObject, JsonUtil.toObject(reader, TestObject.class));
            }
        }

        @Test
        @DisplayName("Testing compress() and decompress() with XML")
        void compressDecompressXml() throws BaseException {
            byte[] compressedByte = GZIPUtil.compress(XML.getBytes(StandardCharsets.UTF_8));
            Assertions.assertNotNull(compressedByte);

            String decompressedTestData = new String(GZIPUtil.decompress(compressedByte), StandardCharsets.UTF_8);
            Assertions.assertEquals(decompressedTestData, XML);
        }

    }

    @Nested
    @DisplayName("Testing decompress()")
    class Decompress {

        private TestObject testObject;
        private byte[] compressed;

        @BeforeEach
        void setUp() {
            testObject = new TestObject();
            testObject.setBytes(new byte[1024]);
            testObject.setString((new String(new byte[1024])));
            testObject.setDate(new Date(Long.parseLong("1549898614051")));
            try {
                compressed = GZIPUtil.compress(JsonUtil.toJson(testObject).getBytes(StandardCharsets.UTF_8));
            } catch (BaseException e) {
                ;
            }
        }

        @Test
        @DisplayName("Testing decompress(byte[])")
        void decompress() throws BaseException {
            byte[] actual = GZIPUtil.decompress(COMPRESSED);
            Assertions.assertEquals(new String(actual), TEST);
        }

        @Test
        @DisplayName("Testing decompress(byte[])")
        void decompressWithClass() throws BaseException {

            TestObject actual = GZIPUtil.decompress(compressed, TestObject.class);

            Assertions.assertEquals(actual, testObject);
        }
    }

    @Nested
    @DisplayName("Testing size()")
    class SizeCheck {
        @Test
        @DisplayName("Testing decompressedSize(byte[])")
        void decompressedSize() throws BaseException {

            int actual = GZIPUtil.decompressedSize(COMPRESSED);

            Assertions.assertEquals(actual, 4);
        }
    }

    @Test
    @DisplayName("Testing null values")
    void nullValues() throws BaseException {
        Assertions.assertFalse(GZIPUtil.isCompressed(null));
        Assertions.assertNull(GZIPUtil.compress(new byte[] {}));
        Assertions.assertNull(GZIPUtil.compress(null));
        Assertions.assertNull(GZIPUtil.decompress(new byte[] {}));
        Assertions.assertNull(GZIPUtil.decompress(null));
        Assertions.assertNull(GZIPUtil.decompress(new byte[] {}, Object.class));
        Assertions.assertNull(GZIPUtil.decompress(null, Object.class));
        Assertions.assertEquals(0, GZIPUtil.decompressedSize(new byte[] {}));
        Assertions.assertEquals(0, GZIPUtil.decompressedSize(null));
    }
}
