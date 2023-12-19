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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.text.MessageFormat;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * AES/CBC/PKCS5PADDING coding
 *
 * @author imre.scheffer
 * @since 2.5.0
 */
public class AesCbcCryptoUtil {

    /**
     * Default zero initial vector
     */
    public static final byte[] DEFAULT_VECTOR = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    /**
     * Cipher transformation algotithm
     */
    public static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5PADDING";

    private static final String WRONG_OR_MISSING_PARAMETERS_MSG = "One of the required param is missing.";
    private static final String INIT_VECTOR_BLANK_MSG = "Input initVector is blank!";

    /**
     * Encrypt data with parameters, initial vector is default zero
     *
     * @param dataToEncrypt
     *            data to encrypt
     * @param key
     *            encrypting key
     * @return encrypted data in byte[]
     * @throws BaseException
     *             if any error
     */
    public static byte[] encrypt(byte[] dataToEncrypt, char[] key) throws BaseException {
        return encrypt(dataToEncrypt, key, defaultIvParameterSpec());
    }

    /**
     * Encrypt data with parameters, initial vector is default zero
     *
     * @param dataToEncrypt
     *            data to encrypt
     * @param key
     *            encrypting key
     * @return encrypted data in InputStream
     * @throws BaseException
     *             if any error
     */
    public static InputStream encrypt(InputStream dataToEncrypt, char[] key) throws BaseException {
        return encrypt(dataToEncrypt, key, defaultIvParameterSpec());
    }

    /**
     * Encrypt data with parameters
     *
     * @param dataToEncrypt
     *            data to encrypt
     * @param key
     *            encrypting key
     * @param initVector
     *            initial vector
     * @return encrypted data in byte[]
     * @throws BaseException
     *             if any error
     */
    public static byte[] encrypt(byte[] dataToEncrypt, char[] key, String initVector) throws BaseException {
        if (StringUtils.isBlank(initVector)) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, INIT_VECTOR_BLANK_MSG);
        }
        IvParameterSpec ivspec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        return encrypt(dataToEncrypt, key, ivspec);
    }

    /**
     * Encrypt data with parameters
     *
     * @param dataToEncrypt
     *            data to encrypt
     * @param key
     *            encrypting key
     * @param initVector
     *            initial vector
     * @return encrypted data in InputStream
     * @throws BaseException
     *             if any error
     */
    public static InputStream encrypt(InputStream dataToEncrypt, char[] key, String initVector) throws BaseException {
        if (StringUtils.isBlank(initVector)) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, INIT_VECTOR_BLANK_MSG);
        }
        IvParameterSpec ivspec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        return encrypt(dataToEncrypt, key, ivspec);
    }

    private static byte[] encrypt(byte[] dataToEncrypt, char[] key, AlgorithmParameterSpec ivSpec) throws BaseException {
        if (dataToEncrypt == null || key == null || ivSpec == null) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, WRONG_OR_MISSING_PARAMETERS_MSG);
        }
        try {
            Key keySpec = SecretKeyUtil.defaultAES256SecretKeySpec(key);
            Cipher c = Cipher.getInstance(CIPHER_TRANSFORMATION);
            c.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return c.doFinal(dataToEncrypt);
        } catch (Exception e) {
            String msg = MessageFormat.format("Encryption failed: [{0}]", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.FAILED_TO_CIPHER_DOCUMENT, msg, e);
        }
    }

    private static InputStream encrypt(InputStream streamToEncrypt, char[] key, AlgorithmParameterSpec ivSpec) throws BaseException {
        if (streamToEncrypt == null || key == null || ivSpec == null) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, WRONG_OR_MISSING_PARAMETERS_MSG);
        }
        try {
            Key keySpec = SecretKeyUtil.defaultAES256SecretKeySpec(key);
            Cipher c = Cipher.getInstance(CIPHER_TRANSFORMATION);
            c.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return new CipherInputStream(streamToEncrypt, c);
        } catch (Exception e) {
            String msg = MessageFormat.format("Encryption failed: [{0}]", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.FAILED_TO_CIPHER_DOCUMENT, msg, e);
        }
    }

    /**
     * Decrypt data with parameters, initial vector is default zero
     *
     * @param encryptedData
     *            encrypted data
     * @param key
     *            decrypting key
     * @return decrypted data in byte[]
     * @throws BaseException
     *             if any error
     */
    public static byte[] decrypt(byte[] encryptedData, char[] key) throws BaseException {
        return decrypt(encryptedData, key, defaultIvParameterSpec());
    }

    /**
     * Decrypt data with parameters, initial vector is default zero
     *
     * @param encryptedData
     *            encrypted data
     * @param key
     *            decrypting key
     * @return decrypted data in InputStream
     * @throws BaseException
     *             if any error
     */
    public static InputStream decrypt(InputStream encryptedData, char[] key) throws BaseException {
        return decrypt(encryptedData, key, defaultIvParameterSpec());
    }

    /**
     * Decrypt data with parameters
     *
     * @param encryptedData
     *            encrypted data
     * @param key
     *            decrypting key
     * @param initVector
     *            initial vector
     * @return decrypted data in byte[]
     * @throws BaseException
     *             if any error
     */
    public static byte[] decrypt(byte[] encryptedData, char[] key, String initVector) throws BaseException {
        if (StringUtils.isBlank(initVector)) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, INIT_VECTOR_BLANK_MSG);
        }
        IvParameterSpec ivspec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        return decrypt(encryptedData, key, ivspec);
    }

    /**
     * Decrypt data with parameters
     *
     * @param encryptedData
     *            encrypted data
     * @param key
     *            decrypting key
     * @param initVector
     *            initial vector
     * @return decrypted data in InputStream
     * @throws BaseException
     *             if any error
     */
    public static InputStream decrypt(InputStream encryptedData, char[] key, String initVector) throws BaseException {
        if (StringUtils.isBlank(initVector)) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, INIT_VECTOR_BLANK_MSG);
        }
        IvParameterSpec ivspec = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
        return decrypt(encryptedData, key, ivspec);
    }

    private static byte[] decrypt(byte[] encodedData, char[] key, AlgorithmParameterSpec ivSpec) throws BaseException {
        if (encodedData == null || key == null || ivSpec == null) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, WRONG_OR_MISSING_PARAMETERS_MSG);
        }
        try {
            Key keySpec = SecretKeyUtil.defaultAES256SecretKeySpec(key);
            Cipher c = Cipher.getInstance(CIPHER_TRANSFORMATION);
            c.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return c.doFinal(encodedData);
        } catch (Exception e) {
            String msg = MessageFormat.format("Decryption failed: [{0}]", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.FAILED_TO_DECIPHER_DOCUMENT, msg, e);
        }
    }

    private static InputStream decrypt(InputStream encodedData, char[] key, AlgorithmParameterSpec ivSpec) throws BaseException {
        if (encodedData == null || key == null || ivSpec == null) {
            throw new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, WRONG_OR_MISSING_PARAMETERS_MSG);
        }
        try {
            Key keySpec = SecretKeyUtil.defaultAES256SecretKeySpec(key);
            Cipher c = Cipher.getInstance(CIPHER_TRANSFORMATION);
            c.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new CipherInputStream(encodedData, c);
        } catch (Exception e) {
            String msg = MessageFormat.format("Decryption failed: [{0}]", e.getLocalizedMessage());
            throw new BaseException(CoffeeFaultType.FAILED_TO_DECIPHER_DOCUMENT, msg, e);
        }
    }

    private static AlgorithmParameterSpec defaultIvParameterSpec() {
        return new IvParameterSpec(DEFAULT_VECTOR);
    }

    /**
     * Truncate/expand InitVector source string to 16-length string
     * 
     * @param sourceString
     *            any string for InitVector
     * @return 16-length String
     */
    public static String to16LengthInitVector(String sourceString) {
        return StringUtils.rightPad(StringUtils.substring(sourceString, 0, 16), 16, '0');
    }

}
