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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;

/**
 * @author balazs.joo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing GZIPUtil")
class GZIPUtilTest {

    private static final String TEST = "test";
    private static final byte[] COMPRESSED = { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0, 43, 73, 45, 46, 1, 0, 12, 126, 127, -40, 4, 0, 0, 0 };
    // https://www.oracle.com/java/technologies/javase/16-relnotes.html
    // Prior to JDK 16, GZIPOutputStream set the OS field in the GZIP header to 0 (meaning FAT filesystem), which does not match the default value
    // specified in section 2.3.1.2 of the GZIP file format specification version 4.3 (RFC 1952).
    // As of JDK 16, the GZIP OS Header Field is set to 255, which is the default value as defined in RFC 1952.
    private static final byte[] COMPRESSEDJ16 = { 31, -117, 8, 0, 0, 0, 0, 0, 0, -1, 43, 73, 45, 46, 1, 0, 12, 126, 127, -40, 4, 0, 0, 0 };

    @Nested
    @DisplayName("Testing isCompressed()")
    class IsCompressed {

        @Test
        @DisplayName("Testing isCompressed(byte[]), JRE < 16")
        @EnabledForJreRange(max = JRE.JAVA_15)
        void isCompressed() {
            Assertions.assertTrue(GZIPUtil.isCompressed(COMPRESSED));
            Assertions.assertFalse(GZIPUtil.isCompressed(TEST.getBytes()));
        }

        @Test
        @DisplayName("Testing isCompressed(byte[]), JRE >= 16")
        @EnabledForJreRange(min = JRE.JAVA_16)
        void isCompressedJ16() {
            Assertions.assertTrue(GZIPUtil.isCompressed(COMPRESSEDJ16));
            Assertions.assertFalse(GZIPUtil.isCompressed(TEST.getBytes()));
        }
    }

    @Nested
    @DisplayName("Testing compress()")
    class Compress {

        @Test
        @DisplayName("Testing compress(byte[]), JRE < 16")
        @EnabledForJreRange(max = JRE.JAVA_15)
        void compressBellowJ16() throws BaseException {

            byte[] compressedByte = GZIPUtil.compress(TEST.getBytes());
            Assertions.assertArrayEquals(COMPRESSED, compressedByte);
        }

        @Test
        @DisplayName("Testing compress(byte[]), JRE >= 16")
        @EnabledForJreRange(min = JRE.JAVA_16)
        void compress() throws BaseException {

            byte[] compressedByte = GZIPUtil.compress(TEST.getBytes());
            Assertions.assertArrayEquals(COMPRESSEDJ16, compressedByte);
        }
    }

    @Nested
    @DisplayName("Testing compressJson() and decompressEx()")
    class CompressDecompressJson {

        private TestData testData;

        @BeforeEach
        void setUp() {
            testData = new TestData();
            testData.setS(new String(new byte[9_999_999])); // nagy string
            testData.setBd(new BigDecimal(42));
            testData.setB(true);
        }

        @Test
        @DisplayName("Testing compressJson() and decompressEx()")
        void compressDecompress() throws BaseException {
            byte[] compressedByte = GZIPUtil.compressJson(testData);
            Assertions.assertNotNull(compressedByte);

            TestData decompressedTestData = GZIPUtil.decompressEx(compressedByte, TestData.class);
            Assertions.assertEquals(testData, decompressedTestData);
        }

        @Test
        @DisplayName("Testing compressJson() and decompressToInputStream()")
        void compressDecompressToInputStream() throws BaseException {
            byte[] compressedByte = GZIPUtil.compressJson(testData);
            Assertions.assertNotNull(compressedByte);

            try (InputStream gzipInputStream = GZIPUtil.decompressToInputStream(compressedByte);
                    InputStreamReader reader = new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8)) {
                Assertions.assertEquals(testData, JsonUtil.toObjectGson(reader, TestData.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("Testing decompress()")
    class Decompress {

        @Test
        @DisplayName("Testing decompress(byte[]), JRE < 16")
        @EnabledForJreRange(max = JRE.JAVA_15)
        void decompress() throws BaseException {

            byte[] actual = GZIPUtil.decompress(COMPRESSED);

            Assertions.assertEquals(new String(actual), TEST);
        }

        @Test
        @DisplayName("Testing decompress(byte[]), JRE < 16")
        @EnabledForJreRange(max = JRE.JAVA_15)
        void decompressWithClass() throws BaseException {

            String actual = GZIPUtil.decompress(COMPRESSED, String.class);

            Assertions.assertEquals(actual, TEST);
        }

        @Test
        @DisplayName("Testing decompress(byte[]), JRE >= 16")
        @EnabledForJreRange(min = JRE.JAVA_16)
        void decompressJ16() throws BaseException {

            byte[] actual = GZIPUtil.decompress(COMPRESSEDJ16);

            Assertions.assertEquals(new String(actual), TEST);
        }

        @Test
        @DisplayName("Testing decompress(byte[]), JRE >= 16")
        @EnabledForJreRange(min = JRE.JAVA_16)
        void decompressWithClassJ16() throws BaseException {

            String actual = GZIPUtil.decompress(COMPRESSEDJ16, String.class);

            Assertions.assertEquals(actual, TEST);
        }
    }

    @Nested
    @DisplayName("Testing size()")
    class SizeCheck {

        @Test
        @DisplayName("Testing decompressedSize(byte[]), JRE < 16")
        @EnabledForJreRange(max = JRE.JAVA_15)
        void decompressedSize() throws BaseException {

            int actual = GZIPUtil.decompressedSize(COMPRESSED);

            Assertions.assertEquals(actual, 4);
        }

        @Test
        @DisplayName("Testing decompressedSize(byte[]), JRE >= 16")
        @EnabledForJreRange(min = JRE.JAVA_16)
        void decompressedSizeJ16() throws BaseException {

            int actual = GZIPUtil.decompressedSize(COMPRESSEDJ16);

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

    /**
     * Test DTO for compressing/decompressing.
     */
    public static class TestData {
        private String s;
        private BigDecimal bd;
        private boolean b;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            TestData testData = (TestData) o;
            return b == testData.b && Objects.equals(s, testData.s) && Objects.equals(bd, testData.bd);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s, bd, b);
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public BigDecimal getBd() {
            return bd;
        }

        public void setBd(BigDecimal bd) {
            this.bd = bd;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }
    }
}
