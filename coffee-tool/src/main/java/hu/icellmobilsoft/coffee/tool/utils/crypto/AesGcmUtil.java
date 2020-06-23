package hu.icellmobilsoft.coffee.tool.utils.crypto;

import java.security.SecureRandom;
import java.text.MessageFormat;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.inject.Vetoed;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * Util class for AES/GCM/NoPadding encode/decode
 *
 * @author mark.petrenyi
 * @since 1.1.0
 */
@Vetoed
public class AesGcmUtil {

    public static final byte[] DEFAULT_IV = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    // For GCM a 12 byte (not 16!) byte-array is recommend by NIST because it’s faster and more secure
    private static final int IV_BYTE_LENGTH = 12;

    private static final int KEY_LENGTH = 256;
    private static final int KEY_BYTE_LENGTH = KEY_LENGTH / 8;
    private static final int AES_BLOCK_SIZE = 128;

    private static final String AES = "AES";
    private static final String CIPHER_AES_GCM_NOPADDING = "AES/GCM/NoPadding";

    /**
     * Generates a secure random 256 bit length key
     * 
     * @return
     */
    public static byte[] generateKey() {
        byte[] key = new byte[KEY_BYTE_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        return key;
    }

    /**
     * Generates a secure random 12 byte length iv
     *
     * @return
     */
    public static byte[] generateIv() {
        byte[] key = new byte[IV_BYTE_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        return key;
    }

    /**
     * AES-256 encrypting (GCM, NoPadding) with one time key (Since the default IV is used, the key must NOT be reused!)
     *
     * @param oneTimeKey
     *            - one time key used for encryption
     * @param plainText
     *            - byte array to encrypt
     * @return the encrypted data as byte array
     * @throws BaseException
     */
    public static byte[] encryptWithAes256GcmNoPadding(byte[] oneTimeKey, byte[] plainText) throws BaseException {
        return encryptWithAes256GcmNoPadding(oneTimeKey, plainText, DEFAULT_IV);
    }

    /**
     * AES-256 encrypting (GCM, NoPadding)
     *
     * @param key
     *            - key used for encryption
     * @param plainText
     *            - byte array to encrypt
     * @param iv
     *            - initialization vector (NEVER use the same key-iv pair more than once)
     * @return the encrypted data as byte array
     * @throws BaseException
     */
    public static byte[] encryptWithAes256GcmNoPadding(byte[] key, byte[] plainText, byte[] iv) throws BaseException {
        checkKeyAndIVSize(key, iv);
        SecretKey secretKey = new SecretKeySpec(key, AES);

        try {
            final Cipher cipher = Cipher.getInstance(CIPHER_AES_GCM_NOPADDING);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(AES_BLOCK_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            return cipher.doFinal(plainText);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }

    /**
     * AES-256 decrypting (GCM, NoPadding) with one time key (Since the default IV is used, the key must NOT be reused!)
     *
     * @param oneTimeKey
     *            - one time key used for decrypting
     * @param encryptedBytes
     *            - byte array to decrypt
     * @return decrypted data as byte array
     * @throws BaseException
     */
    public static byte[] decryptWithAes256GcmNoPadding(byte[] oneTimeKey, byte[] encryptedBytes) throws BaseException {
        return decryptWithAes256GcmNoPadding(oneTimeKey, encryptedBytes, DEFAULT_IV);
    }

    /**
     * AES-256 decrypting (GCM, NoPadding)
     *
     * @param key
     *            - key used for decrypting
     * @param encryptedBytes
     *            - byte array to decrypt
     * @param iv
     *            - initialization vector
     * @return decrypted data as byte array
     * @throws BaseException
     */
    public static byte[] decryptWithAes256GcmNoPadding(byte[] key, byte[] encryptedBytes, byte[] iv) throws BaseException {
        checkKeyAndIVSize(key, iv);

        try {
            final Cipher cipher = Cipher.getInstance(CIPHER_AES_GCM_NOPADDING);
            SecretKey secretKey = new SecretKeySpec(key, AES);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(AES_BLOCK_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            return cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }

    private static void checkKeyAndIVSize(byte[] key, byte[] iv) throws BaseException {
        if (key == null || key.length != KEY_BYTE_LENGTH) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT,
                    MessageFormat.format("Invalid key length or key is null! Expected key length in bytes: [{0}]", KEY_BYTE_LENGTH));
        }
        if (iv == null || iv.length != IV_BYTE_LENGTH) {
            throw new TechnicalException(CoffeeFaultType.INVALID_INPUT,
                    MessageFormat.format("Invalid IV length or IV is null! Expected IV length in bytes: [{0}]", IV_BYTE_LENGTH));
        }
    }
}
