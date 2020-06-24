package hu.icellmobilsoft.coffee.tool.utils.crypto;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;

/**
 * Test class for {@link AesGcmUtil}
 * 
 * @author mark.petrenyi
 * @since
 */
@DisplayName("Testing AesGcmUtil")
class AesGcmUtilTest {

    private static final String TEST_INPUT_TEXT = "test input test";
    private static final String TEST_KEY = "12345678901234567890123456789012";

    @Test
    @DisplayName("Testing aes256GcmNoPadding encrypt and decrypt - default iv")
    void aes256GcmNoPadding() throws Exception {
        // given
        byte[] key = TEST_KEY.getBytes("UTF-8");
        // when
        byte[] encoded = AesGcmUtil.encryptWithAes256GcmNoPadding(key, TEST_INPUT_TEXT.getBytes("UTF-8"));
        byte[] decoded = AesGcmUtil.decryptWithAes256GcmNoPadding(key, encoded);
        // then
        Assertions.assertEquals(TEST_INPUT_TEXT, new String(decoded, "UTF-8"));
    }

    @Test
    @DisplayName("Testing aes256GcmNoPadding encrypt and decrypt - custom iv")
    void aes256GcmNoPaddingCustomIv() throws Exception {
        // given
        byte[] key = TEST_KEY.getBytes("UTF-8");
        byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        // when
        byte[] encoded = AesGcmUtil.encryptWithAes256GcmNoPadding(key, TEST_INPUT_TEXT.getBytes("UTF-8"), iv);
        byte[] decoded = AesGcmUtil.decryptWithAes256GcmNoPadding(key, encoded, iv);
        // then
        Assertions.assertEquals(TEST_INPUT_TEXT, new String(decoded, "UTF-8"));
    }

    @Test
    @DisplayName("Testing aes256GcmNoPadding encrypt and decrypt - key mismatch")
    void aes256GcmNoPaddingKeyMismatch() throws Exception {
        // given
        byte[] key = TEST_KEY.getBytes("UTF-8");
        byte[] key2 = "98765432109876543210987654321098".getBytes("UTF-8");
        // when
        byte[] encoded = AesGcmUtil.encryptWithAes256GcmNoPadding(key, TEST_INPUT_TEXT.getBytes("UTF-8"));
        // byte[] decoded = AesGcmUtil.decryptWithAes256GcmNoPadding(key2, encoded);
        // then
        Executable decryptExecution = () -> AesGcmUtil.decryptWithAes256GcmNoPadding(key2, encoded);
        Assertions.assertThrows(TechnicalException.class, decryptExecution);
    }

    @Test
    @DisplayName("Testing aes256GcmNoPadding encrypt and decrypt - iv mismatch")
    void aes256GcmNoPaddingIvMismatch() throws Exception {
        // given
        byte[] key = TEST_KEY.getBytes("UTF-8");
        byte[] iv1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        byte[] iv2 = { 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        // when
        byte[] encoded = AesGcmUtil.encryptWithAes256GcmNoPadding(key, TEST_INPUT_TEXT.getBytes("UTF-8"), iv1);
        // then
        Executable decryptExecution = () -> AesGcmUtil.decryptWithAes256GcmNoPadding(key, encoded, iv2);
        Assertions.assertThrows(TechnicalException.class, decryptExecution);
    }

    @Test
    @DisplayName("Testing generateKey()")
    void generateKey() throws Exception {
        // given
        // when
        byte[] key = AesGcmUtil.generateKey();
        // then
        int expectedByteLength = 256 / 8; // 256 bit
        Assertions.assertNotNull(key);
        Assertions.assertEquals(expectedByteLength, key.length);
    }

    @Test
    @DisplayName("Testing generateIv()")
    void generateIv() throws Exception {
        // given
        // when
        byte[] key = AesGcmUtil.generateIv();
        // then
        int expectedByteLength = 12;
        Assertions.assertNotNull(key);
        Assertions.assertEquals(expectedByteLength, key.length);
    }

    @Test
    @DisplayName("Testing aes256GcmNoPadding encrypt and decrypt - with generated key and iv")
    void aes256GcmNoPaddingWithGeneratedKeyAndIv() throws Exception {
        // given
        byte[] key = AesGcmUtil.generateKey();
        byte[] iv = AesGcmUtil.generateIv();
        // when
        byte[] encoded = AesGcmUtil.encryptWithAes256GcmNoPadding(key, TEST_INPUT_TEXT.getBytes("UTF-8"), iv);
        byte[] decoded = AesGcmUtil.decryptWithAes256GcmNoPadding(key, encoded, iv);
        // then
        Assertions.assertEquals(TEST_INPUT_TEXT, new String(decoded, "UTF-8"));
    }

    @DisplayName("Testing encryptWithAes256GcmNoPadding for invalid input")
    @ParameterizedTest(name = "Testing encryptWithAes256GcmNoPadding() with invalid input:[{0}]")
    // given
    @MethodSource("invalidInputProvider")
    void encryptWithAes256GcmNoPadding_invalidInput(byte[] key, byte[] text, byte[] iv) throws Exception {
        Executable encryptExecution = () -> AesGcmUtil.encryptWithAes256GcmNoPadding(key, text, iv);
        Assertions.assertThrows(TechnicalException.class, encryptExecution);
    }

    @DisplayName("Testing decryptWithAes256GcmNoPadding for invalid input")
    @ParameterizedTest(name = "Testing encryptWithAes256GcmNoPadding() with invalid input:[{0}]")
    // given
    @MethodSource("invalidInputProvider")
    void decryptWithAes256GcmNoPadding_invalidInput(byte[] key, byte[] text, byte[] iv) throws Exception {
        Executable encryptExecution = () -> AesGcmUtil.decryptWithAes256GcmNoPadding(key, text, iv);
        Assertions.assertThrows(TechnicalException.class, encryptExecution);
    }

    static Stream<Arguments> invalidInputProvider() throws Exception {
        byte[] validKey = TEST_KEY.getBytes("UTF-8");
        byte[] shortKey = { 1, 2, 3 };
        byte[] longKey = (TEST_KEY + TEST_KEY).getBytes("UTF-8");

        byte[] validIV = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        byte[] shortIv = { 1, 2, 3 };
        byte[] longIv = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };

        byte[] validText = TEST_INPUT_TEXT.getBytes("UTF-8");

        return Stream.of(//
                Arguments.arguments(null, validText, validIV, "null key"), //
                Arguments.arguments(shortKey, validText, validIV, "too short key"), //
                Arguments.arguments(longKey, validText, validIV, "too long key"), //
                Arguments.arguments(validKey, validText, null, "null iv"), //
                Arguments.arguments(validKey, validText, shortIv, "too short iv"), //
                Arguments.arguments(validKey, validText, longIv, "too long iv"), //
                Arguments.arguments(validKey, null, validIV, "null text") //
        );
    }
}