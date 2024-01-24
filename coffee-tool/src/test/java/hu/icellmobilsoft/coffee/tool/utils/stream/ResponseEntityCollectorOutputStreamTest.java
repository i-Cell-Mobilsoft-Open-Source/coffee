/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.stream;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ResponseEntityCollectorOutputStream}
 *
 * @author mark.petrenyi
 * @since 2.5.0
 */
class ResponseEntityCollectorOutputStreamTest {

    private static final String TEST_TEXT = "This is a test text. Árvíztűrő tükörfúrógép";

    private static final byte[] TEST_BYTES_UTF_8= TEST_TEXT.getBytes(StandardCharsets.UTF_8);


    /**
     * Test outputStream collcetor
     *
     * @throws IOException on error
     */
    @Test
    void outputStreamCollectorTest() throws IOException {
        // given
        InputStream input = new ByteArrayInputStream(TEST_BYTES_UTF_8);
        OutputStream original = new ByteArrayOutputStream(TEST_BYTES_UTF_8.length);
        ResponseEntityCollectorOutputStream out = new ResponseEntityCollectorOutputStream(original, 1000);
        // when
        // kiírunk a streamre
        IOUtils.copy(input, out);
        //then
        byte[] actualCollectedBytes = out.getEntity();
        String actualString = new String(actualCollectedBytes, StandardCharsets.UTF_8);
        Assertions.assertEquals(TEST_TEXT, actualString);
        Assertions.assertArrayEquals(TEST_BYTES_UTF_8, actualCollectedBytes);
    }
}
