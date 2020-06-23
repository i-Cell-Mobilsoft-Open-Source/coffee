package hu.icellmobilsoft.coffee.tool.utils.crypto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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
}