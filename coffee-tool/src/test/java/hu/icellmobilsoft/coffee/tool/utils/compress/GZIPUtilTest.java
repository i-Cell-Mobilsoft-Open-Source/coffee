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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.compress.GZIPUtil;

/**
 * @author balazs.joo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testing GZIPUtil")
public class GZIPUtilTest {

    private static final String TEST = "test";
    private static final byte[] COMPRESSED = { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0, 43, 73, 45, 46, 1, 0, 12, 126, 127, -40, 4, 0, 0, 0 };

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
    @DisplayName("Testing decompress()")
    class Decompress {

        @Test
        @DisplayName("Testing decompress(byte[])")
        void decompress() throws BaseException {

            byte[] actual = GZIPUtil.decompress(COMPRESSED);

            Assertions.assertEquals(new String(actual), TEST);

        }

        @Test
        @DisplayName("Testing decompress(byte[])")
        void decompressWithClass() throws BaseException {

            String actual = GZIPUtil.decompress(COMPRESSED, String.class);

            Assertions.assertEquals(actual, TEST);

        }
    }

    @Test
    @DisplayName("Testing decompressedSize(byte[])")
    void decompressedSize() throws BaseException {

        int actual = GZIPUtil.decompressedSize(COMPRESSED);

        Assertions.assertEquals(actual, 4);

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
