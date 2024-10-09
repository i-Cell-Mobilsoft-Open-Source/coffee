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
package hu.icellmobilsoft.coffee.se.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.se.io.exception.SizeLimitExceededIOException;

@DisplayName("Testing LimitedSizeInputStream")
class LimitedSizeInputStreamTest {

    private static interface InputStreamConsumer {

        void consume(InputStream inputStream) throws IOException;

    }

    private final int MAX_SIZE = 5;

    @Test
    @DisplayName("Testing empty InputStream consumed by read()")
    void testEmptyInputStreamConsumeByRead() {

        // given
        InputStream inputStream = createInputStream(0);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByRead;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing empty InputStream consumed by read(byte[] b)")
    void testEmptyInputStreamConsumeByReadBuffer() {

        // given
        InputStream inputStream = createInputStream(0);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBuffer;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing empty InputStream consumed by read(byte b[], int off, int len)")
    void testEmptyInputStreamConsumeByReadBufferOffset() {

        // given
        InputStream inputStream = createInputStream(0);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBufferOffset;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing InputStream consumed by read()")
    void testInputStreamConsumeByRead() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE - 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByRead;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing InputStream consumed by read(byte[] b)")
    void testInputStreamConsumeByReadBuffer() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE - 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBuffer;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing InputStream consumed by read(byte b[], int off, int len)")
    void testInputStreamConsumeByReadBufferOffset() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE - 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBufferOffset;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing max size InputStream consumed by read()")
    void testMaxSizeInputStreamConsumeByRead() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByRead;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing max size InputStream consumed by read(byte[] b)")
    void testMaxSizeInputStreamConsumeByReadBuffer() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBuffer;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing max size InputStream consumed by read(byte b[], int off, int len)")
    void testMaxSizeInputStreamConsumeByReadBufferOffset() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBufferOffset;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing too big InputStream consumed by read()")
    void testTooBigInputStreamConsumeByRead() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE + 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByRead;

        testSizeLimitExceededCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing too big InputStream consumed by read(byte[] b)")
    void testTooBigInputStreamConsumeByReadBuffer() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE + 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBuffer;

        testSizeLimitExceededCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing too big InputStream consumed by read(byte b[], int off, int len)")
    void testTooBigInputStreamConsumeByReadBufferOffset() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE + 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadBufferOffset;

        testSizeLimitExceededCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing too big InputStream consumed by skip()")
    void testTooBigInputStreamConsumeByReadWhileSkipped() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE + 1);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamBySkip;

        testSizeLimitExceededCase(inputStream, inputStreamConsumer);
    }

    @Test
    @DisplayName("Testing too big InputStream consumed by read() and mark() reset()")
    void testMaxSizeInputStreamConsumeByReadAndMarkReset() {

        // given
        InputStream inputStream = createInputStream(MAX_SIZE);
        InputStreamConsumer inputStreamConsumer = this::consumeInputStreamByReadAndMarkReset;

        testHappyCase(inputStream, inputStreamConsumer);
    }

    private void testHappyCase(InputStream inputStream, InputStreamConsumer inputStreamConsumer) {

        try (inputStream) {

            // when
            inputStreamConsumer.consume(inputStream);

            // then
            // OK

        } catch (IOException e) {

            // then
            Assertions.fail(e);
        }
    }

    private void testSizeLimitExceededCase(InputStream inputStream, InputStreamConsumer inputStreamConsumer) {

        try (inputStream) {

            // when
            inputStreamConsumer.consume(inputStream);

            // then
            Assertions.fail("Should throw SizeLimitExceededIOException");

        } catch (SizeLimitExceededIOException e) {

            // then
            Assertions.assertNotNull(e);

        } catch (IOException e) {

            // then
            Assertions.fail("Should throw SizeLimitExceededIOException instead of ", e);
        }
    }

    private LimitedSizeInputStream createInputStream(int size) {
        return new LimitedSizeInputStream(new ByteArrayInputStream(new byte[size]), MAX_SIZE);
    }

    private void consumeInputStreamByRead(InputStream inputStream) throws IOException {

        while (inputStream.read() >= 0) {
            // nothing to do
        }
    }

    private void consumeInputStreamByReadAndMarkReset(InputStream inputStream) throws IOException {

        inputStream.read();

        inputStream.mark(MAX_SIZE);

        for (int i = 0; i < MAX_SIZE / 2; i++) {
            inputStream.read();
        }

        inputStream.reset();

        while (inputStream.read() >= 0) {
            // nothing to do
        }
    }

    private void consumeInputStreamBySkip(InputStream inputStream) throws IOException {

        inputStream.skip(MAX_SIZE + 1);
    }

    private void consumeInputStreamByReadBuffer(InputStream inputStream) throws IOException {

        byte[] buffer = new byte[2];

        while (inputStream.read(buffer) >= 0) {
            // nothing to do
        }
    }

    private void consumeInputStreamByReadBufferOffset(InputStream inputStream) throws IOException {

        byte[] buffer = new byte[2];

        while (inputStream.read(buffer, 0, 2) >= 0) {
            // nothing to do
        }
    }

}
