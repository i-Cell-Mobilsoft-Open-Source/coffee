/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * AES/CBC/PKCS5PADDING crypting tests
 *
 * @author imre.scheffer
 * @since 2.5.0
 */
@DisplayName("Testing AesCbcCryptoUtil")
public class AesCbcCryptoUtilTest {

    private static final String DATA_SOURCE = "0cfd41cc-3230-493f-82d7-017c0f7f7f8428L71E4WIHQZ";
    private static final byte[] DATA = DATA_SOURCE.getBytes(StandardCharsets.UTF_8);
    private static final char[] KEY = "1234567890123456".toCharArray();
    private static final String TEST_INIT_VECTOR_16_LENGTH = "Test initVector!";
    private static final String TEST_INIT_VECTOR_15_LENGTH = "Test initVector";
    private static final String TEST_INIT_VECTOR_17_LENGTH = "Test initVector!!";

    private Logger LOG = Logger.getLogger(AesCbcCryptoUtilTest.class.getSimpleName());

    @Nested
    @DisplayName("Testing encode and decode byte[] data")
    class ByteArrayInputTest {

        @Test
        @DisplayName("Testing with default initial vector")
        void withDefaultVector() {
            // given

            byte[] encoded = null;
            // when
            try {
                encoded = AesCbcCryptoUtil.encrypt(DATA, KEY);
            } catch (BaseException e) {
                Assertions.fail("Exception on enrypt data: " + e.getLocalizedMessage(), e);
            }
            LOG.info(MessageFormat.format("encrypted byteArray: [{0}]", new String(encoded, StandardCharsets.UTF_8)));

            byte[] decrypted = null;
            try {
                decrypted = AesCbcCryptoUtil.decrypt(encoded, KEY);
            } catch (BaseException e) {
                Assertions.fail("Exception on decrypt data: " + e.getLocalizedMessage(), e);
            }
            String decryptedString = new String(decrypted, StandardCharsets.UTF_8);
            // then
            Assertions.assertEquals(DATA_SOURCE, decryptedString);
        }

        @Test
        @DisplayName("Testing with test vector")
        void withTestVector() {
            // given

            byte[] encoded = null;
            // when
            try {
                encoded = AesCbcCryptoUtil.encrypt(DATA, KEY, TEST_INIT_VECTOR_16_LENGTH);
            } catch (BaseException e) {
                Assertions.fail("Exception on enrypt data: " + e.getLocalizedMessage(), e);
            }
            LOG.info(MessageFormat.format("encrypted byteArray: [{0}]", new String(encoded, StandardCharsets.UTF_8)));

            byte[] decrypted = null;
            try {
                decrypted = AesCbcCryptoUtil.decrypt(encoded, KEY, TEST_INIT_VECTOR_16_LENGTH);
            } catch (BaseException e) {
                Assertions.fail("Exception on decrypt data: " + e.getLocalizedMessage(), e);
            }
            String decryptedString = new String(decrypted, StandardCharsets.UTF_8);
            // then
            Assertions.assertEquals(DATA_SOURCE, decryptedString);
        }

        @Test
        @DisplayName("Testing with wrong vector")
        void withWrongVector() {
            // given

            BaseException actualException = Assertions.assertThrows(BaseException.class,
                    () -> AesCbcCryptoUtil.encrypt(DATA, KEY, TEST_INIT_VECTOR_15_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.FAILED_TO_CIPHER_DOCUMENT, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.encrypt(DATA, KEY, TEST_INIT_VECTOR_17_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.FAILED_TO_CIPHER_DOCUMENT, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(DATA, KEY, TEST_INIT_VECTOR_15_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.FAILED_TO_DECIPHER_DOCUMENT, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(DATA, KEY, TEST_INIT_VECTOR_17_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.FAILED_TO_DECIPHER_DOCUMENT, actualException.getFaultTypeEnum());
        }

        @Test
        @DisplayName("Testing with null/blank input")
        void withNullBlankInput() {
            // given

            byte[] nullArray = null;

            BaseException actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.encrypt(nullArray, null, null));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class,
                    () -> AesCbcCryptoUtil.encrypt(nullArray, KEY, TEST_INIT_VECTOR_16_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.encrypt(DATA, null, TEST_INIT_VECTOR_16_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.encrypt(DATA, KEY, null));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(nullArray, null, null));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class,
                    () -> AesCbcCryptoUtil.decrypt(nullArray, KEY, TEST_INIT_VECTOR_16_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(DATA, null, TEST_INIT_VECTOR_16_LENGTH));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(DATA, KEY, null));
            Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

        }
    }

    @Nested
    @DisplayName("Testing encode and decode InputStream data")
    class InputStreamInputTest {

        @Test
        @DisplayName("Testing with default initial vector")
        void withDefaultVector() {
            // given

            InputStream encoded = null;
            // when
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(DATA);
                encoded = AesCbcCryptoUtil.encrypt(bais, KEY);
            } catch (BaseException e) {
                Assertions.fail("Exception on enrypt data: " + e.getLocalizedMessage(), e);
            }

            byte[] readedInputStream = readInputStream(encoded);
            LOG.info(MessageFormat.format("encrypted inputStream: [{0}]", new String(readedInputStream, StandardCharsets.UTF_8)));

            InputStream decrypted = null;
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(readedInputStream);
                decrypted = AesCbcCryptoUtil.decrypt(bais, KEY);
            } catch (BaseException e) {
                Assertions.fail("Exception on decrypt data: " + e.getLocalizedMessage(), e);
            }
            readedInputStream = readInputStream(decrypted);
            String decryptedString = new String(readedInputStream, StandardCharsets.UTF_8);
            // then
            Assertions.assertEquals(DATA_SOURCE, decryptedString);
        }

        @Test
        @DisplayName("Testing with test vector")
        void withTestVector() {
            // given

            InputStream encoded = null;
            // when
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(DATA);
                encoded = AesCbcCryptoUtil.encrypt(bais, KEY, TEST_INIT_VECTOR_16_LENGTH);
            } catch (BaseException e) {
                Assertions.fail("Exception on enrypt data: " + e.getLocalizedMessage(), e);
            }

            byte[] readedInputStream = readInputStream(encoded);
            LOG.info(MessageFormat.format("encrypted inputStream: [{0}]", new String(readedInputStream, StandardCharsets.UTF_8)));

            InputStream decrypted = null;
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(readedInputStream);
                decrypted = AesCbcCryptoUtil.decrypt(bais, KEY, TEST_INIT_VECTOR_16_LENGTH);
            } catch (BaseException e) {
                Assertions.fail("Exception on decrypt data: " + e.getLocalizedMessage(), e);
            }
            readedInputStream = readInputStream(decrypted);
            String decryptedString = new String(readedInputStream, StandardCharsets.UTF_8);
            // then
            Assertions.assertEquals(DATA_SOURCE, decryptedString);
        }

        @Test
        @DisplayName("Testing with wrong vector")
        void withWrongVector() {
            // given

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(DATA);
                InputStream dataStream = AesCbcCryptoUtil.encrypt(bais, KEY, TEST_INIT_VECTOR_16_LENGTH);

                BaseException actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.encrypt(dataStream, KEY, TEST_INIT_VECTOR_15_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.FAILED_TO_CIPHER_DOCUMENT, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.encrypt(dataStream, KEY, TEST_INIT_VECTOR_17_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.FAILED_TO_CIPHER_DOCUMENT, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.decrypt(dataStream, KEY, TEST_INIT_VECTOR_15_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.FAILED_TO_DECIPHER_DOCUMENT, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.decrypt(dataStream, KEY, TEST_INIT_VECTOR_17_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.FAILED_TO_DECIPHER_DOCUMENT, actualException.getFaultTypeEnum());

            } catch (BaseException e) {
                Assertions.fail("Exception on enrypt data: " + e.getLocalizedMessage(), e);
            }
        }

        @Test
        @DisplayName("Testing with null/blank input")
        void withNullBlankInput() {
            // given

            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(DATA);
                InputStream dataStream = AesCbcCryptoUtil.encrypt(bais, KEY, TEST_INIT_VECTOR_16_LENGTH);
                InputStream nullStream = null;

                BaseException actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.encrypt(nullStream, null, null));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.encrypt(nullStream, KEY, TEST_INIT_VECTOR_16_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.encrypt(dataStream, null, TEST_INIT_VECTOR_16_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.encrypt(dataStream, KEY, null));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(nullStream, null, null));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.decrypt(nullStream, KEY, TEST_INIT_VECTOR_16_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class,
                        () -> AesCbcCryptoUtil.decrypt(dataStream, null, TEST_INIT_VECTOR_16_LENGTH));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

                actualException = Assertions.assertThrows(BaseException.class, () -> AesCbcCryptoUtil.decrypt(dataStream, KEY, null));
                Assertions.assertEquals(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, actualException.getFaultTypeEnum());

            } catch (BaseException e) {
                Assertions.fail("Exception on enrypt data: " + e.getLocalizedMessage(), e);
            }
        }
    }

    private byte[] readInputStream(InputStream is) {
        try {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            Assertions.fail("Exception on read inputStream: " + e.getLocalizedMessage(), e);
            return null;
        }
    }
}
