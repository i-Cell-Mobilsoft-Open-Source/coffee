/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.se.util.manifest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

/**
 * Tests manifest reading independently of a servlet container.
 *
 * @author balazs.bakos
 * @since 4.0.0
 */
class ManifestUtilTest {

    @Test
    void preservesUtf8ContinuationLinesAndSectionsWhileNormalizingLineEndings() throws IOException {
        String manifest = "Manifest-Version: 1.0\r\nBuild-Description: \u00e1rv\u00edzt\u0171r\u0151\r continuation\n\n"
                + "Name: sample.txt\r\nCustom-Value: last";
        AtomicBoolean closed = new AtomicBoolean();
        InputStream stream = new ByteArrayInputStream(manifest.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public void close() throws IOException {
                closed.set(true);
                super.close();
            }
        };

        assertEquals("Manifest-Version: 1.0\nBuild-Description: \u00e1rv\u00edzt\u0171r\u0151\n continuation\n\n"
                + "Name: sample.txt\nCustom-Value: last\n", ManifestUtil.getManifestContent(stream));
        assertTrue(closed.get());
    }

    @Test
    void missingOrEmptyManifestReturnsEmptyContent() throws IOException {
        assertEquals("", ManifestUtil.getManifestContent(null));
        assertEquals("", ManifestUtil.getManifestContent(new ByteArrayInputStream(new byte[0])));
    }

    @Test
    void propagatesReadFailureAndClosesStream() {
        AtomicBoolean closed = new AtomicBoolean();
        IOException failure = new IOException("unreadable");
        InputStream stream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw failure;
            }

            @Override
            public void close() {
                closed.set(true);
            }
        };

        assertEquals(failure, assertThrows(IOException.class, () -> ManifestUtil.getManifestContent(stream)));
        assertTrue(closed.get());
    }
}
