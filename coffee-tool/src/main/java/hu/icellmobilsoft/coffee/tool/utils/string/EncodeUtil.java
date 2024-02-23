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
package hu.icellmobilsoft.coffee.tool.utils.string;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.exception.TechnicalException;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Util class for encoding
 *
 * @author balazs.joo
 * @author Imre Scheffer
 * @since 1.0.0
 */
public class EncodeUtil {

    private static Logger LOGGER = Logger.getLogger(EncodeUtil.class);

    /**
     * {@value #ALGORITHM_SHA_512} from
     * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms">message digest
     * algorithms</a>
     */
    public static final String ALGORITHM_SHA_512 = "SHA-512";
    /**
     * {@value #ALGORITHM_SHA3_512} from
     * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms">message digest
     * algorithms</a>
     */
    public static final String ALGORITHM_SHA3_512 = "SHA3-512";

    /**
     * Default constructor, constructs a new object.
     */
    public EncodeUtil() {
        super();
    }

    /**
     * Encodes input {@link String} with SHA-512.
     *
     * @param str
     *            input {@code String}
     * @return encoded {@code String} or null if invalid input or encoding error
     * @deprecated use {@link #sha_512(String)} instead
     */
    @Deprecated(forRemoval = true, since = "2.5.0")
    public static String Sha512(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        String sha = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance(ALGORITHM_SHA_512);
            crypt.reset();
            crypt.update(str.getBytes(StandardCharsets.UTF_8));
            sha = byteToHex(crypt.digest());
        } catch (NullPointerException e) {
            LOGGER.error("Error in get SHA-512: encoded string is null!", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(MessageFormat.format("Error in get SHA-512 from [{0}]", str), e);
        }

        return StringUtils.upperCase(sha);
    }

    /**
     * Digest message input {@link String} with SHA-512.
     *
     * @param stringInput
     *            input {@code String}
     * @return Upper case encoded {@code String} or null if invalid input or encoding error
     * @throws BaseException
     *             any exception
     * @since 2.5.0
     */
    public static String sha_512(String stringInput) throws BaseException {
        if (StringUtils.isBlank(stringInput)) {
            return null;
        }

        String sha = messageDigest(stringInput.getBytes(StandardCharsets.UTF_8), ALGORITHM_SHA_512);
        return StringUtils.upperCase(sha);
    }

    /**
     * Digest message input {@link String} with SHA3-512.
     *
     * @param stringInput
     *            input {@code String}
     * @return Upper case encoded {@code String} or null if invalid input or encoding error
     * @throws BaseException
     *             any exception
     * @since 2.5.0
     */
    public static String sha3_512(String stringInput) throws BaseException {
        if (StringUtils.isBlank(stringInput)) {
            return null;
        }

        String sha = messageDigest(stringInput.getBytes(StandardCharsets.UTF_8), ALGORITHM_SHA3_512);
        return StringUtils.upperCase(sha);
    }

    /**
     * Encodes input {@link String} with specified algorithm.
     *
     * @param input
     *            input message to digest
     * @param algorithm
     *            one of <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html#messagedigest-algorithms">message
     *            digest algorithms</a>
     * @return message digest output by algorith
     * @throws BaseException
     *             any exception
     * @since 2.5.0
     */
    public static String messageDigest(byte[] input, String algorithm) throws BaseException {
        if (Objects.isNull(input)) {
            throw new InvalidParameterException("input is null!");
        }
        if (StringUtils.isBlank(algorithm)) {
            throw new InvalidParameterException("algorithm is null!");
        }

        try {
            MessageDigest crypt = MessageDigest.getInstance(algorithm);
            crypt.reset();
            crypt.update(input);
            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, MessageFormat.format("Error in get [{0}] from input", algorithm), e);
        }
    }

    /**
     * Format input {@code byte[]} to HEX {@link String}
     *
     * @param hash
     *            byte array
     * @return HEX {@code String}
     */
    public static String byteToHex(final byte[] hash) {
        if (Objects.isNull(hash)) {
            return null;
        }

        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
