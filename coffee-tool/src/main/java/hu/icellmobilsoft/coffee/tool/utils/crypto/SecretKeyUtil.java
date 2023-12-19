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

import java.security.spec.KeySpec;
import java.text.MessageFormat;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * Cryptography key handling util
 *
 * @author imre.scheffer
 * @since 2.5.0
 */
public class SecretKeyUtil {

    /**
     * Default salt
     */
    public static final byte[] SALT = { 0 };
    /**
     * Default iteration
     */
    public static final int ITERATION = 1000;
    /**
     * Encoded velua length
     */
    public static final int KEY_LENGTH = 256;
    /**
     * Secret key algorithm
     */
    public static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    /**
     * AES value
     */
    public static final String ALGORITHM_AES = "AES";

    /**
     * Create secret key, where the following values are set:
     * <ul>
     * <li>SALT: { 0 }</li>
     * <li>ITERATION: {@value #ITERATION}</li>
     * <li>KEY_LENGTH: {@value #KEY_LENGTH}</li>
     * <li>SECRET_KEY_ALGORITHM: {@value #SECRET_KEY_ALGORITHM}</li>
     * <li>ALGORITHM_AES: {@value #ALGORITHM_AES}</li>
     * </ul>
     *
     * @param password
     *            string to encode
     * @return {@link SecretKeySpec}
     * @throws BaseException
     *             any exception
     */
    public static SecretKeySpec defaultAES256SecretKeySpec(char[] password) throws BaseException {
        if (password == null || password.length == 0) {
            throw new InvalidParameterException("Input password is null");
        }

        return AES256SecretKeySpec(password, SALT, ITERATION, KEY_LENGTH, SECRET_KEY_ALGORITHM, ALGORITHM_AES);
    }

    /**
     * Create secret key
     * 
     * @param password
     *            string to encode
     * @param salt
     *            salt source
     * @param iteration
     *            iteration count
     * @param keyLength
     *            he to-be-derived key length
     * @param secretKeyFactoryAlgorithm
     *            the standard name of the requested secret-key algorithm
     * @param secretKeySpecAlgorithm
     *            the name of the secret-key algorithm to be associated with the given key material.
     * @return secret key from the given byte array
     * @throws BaseException
     *             any exception
     */
    public static SecretKeySpec AES256SecretKeySpec(char[] password, byte[] salt, int iteration, int keyLength, String secretKeyFactoryAlgorithm,
            String secretKeySpecAlgorithm) throws BaseException {
        if (password == null) {
            throw new InvalidParameterException("Input password is null");
        }
        if (salt == null) {
            throw new InvalidParameterException("Input salt is null");
        }
        if (StringUtils.isBlank(secretKeyFactoryAlgorithm)) {
            throw new InvalidParameterException("Input secretKeyFactoryAlgorithm is empty");
        }
        if (StringUtils.isBlank(secretKeySpecAlgorithm)) {
            throw new InvalidParameterException("Input secretKeySpecAlgorithm is empty");
        }

        try {
            KeySpec spec = new PBEKeySpec(password, salt, iteration, keyLength);
            SecretKeyFactory f = SecretKeyFactory.getInstance(secretKeyFactoryAlgorithm);
            byte[] keyByte = f.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyByte, secretKeySpecAlgorithm);
        } catch (Exception e) {
            String msg = MessageFormat.format("Error by generating SecretKey: [{0}]", e.getLocalizedMessage());
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, msg, e);
        }
    }
}
