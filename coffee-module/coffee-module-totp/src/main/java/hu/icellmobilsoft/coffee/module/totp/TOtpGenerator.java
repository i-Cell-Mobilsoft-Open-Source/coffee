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
package hu.icellmobilsoft.coffee.module.totp;

import java.security.SecureRandom;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;

/**
 * TOtpGenerator interface.
 *
 * @author cstamas
 * @since 1.0.0
 */
public interface TOtpGenerator {

    /**
     * Generates TOTP with given secret key, UTC timestamp, OTP length and hash algorithm (RFC 6238).
     *
     * @param secretKey
     *            secret key for TOTP generation
     * @param utcTimestamp
     *            UTC timestamp for TOTP generation
     * @param otpLength
     *            generated OTP length, valid values between 1 and 9
     * @param hashAlgorithm
     *            hash algorithm for TOTP generation
     * @return TOTP
     * @throws BaseException
     *             if any exception occurs
     */
    public String generatePassword(byte[] secretKey, long utcTimestamp, int otpLength, TOtpAlgorithm hashAlgorithm) throws BaseException;

    /**
     * Generates TOTP with given secret key, UTC timestamp. Uses hash algorithm and OTP length defined in config.
     *
     * @param secretKey
     *            secret key for TOTP generation
     * @param utcTimestamp
     *            UTC timestamp for TOTP generation
     * @return TOTP
     * @throws BaseException
     *             if any exception occurs
     */
    public String generatePassword(byte[] secretKey, long utcTimestamp) throws BaseException;

    /**
     * Generates TOTP with given secret key and the server's actual epoch time. Uses hash algorithm and OTP length defined in config.
     *
     * @param secretKey
     *            secret key for TOTP generation
     * @return TOTP
     * @throws BaseException
     *             if any exception occurs
     */
    public default String generatePassword(byte[] secretKey) throws BaseException {
        return generatePassword(secretKey, System.currentTimeMillis());
    }

    /**
     * Returns a secret key - with length given in bytes - in base32 format (A-Z2-7) using {@link SecureRandom}. This secret key can be used to
     * generate a QR code to be shared with the user.
     *
     * @param length
     *            length of generated secret
     * @return secret key
     * @throws BaseException
     *             if any exception occurs
     * @see #generateSecretBase32()
     */
    public default String generateSecretBase32(int length) throws BaseException {
        return encodeWithBase32(generateSecret(length));
    }

    /**
     * Returns a fix 16 bytes long secret key in base32 format (A-Z2-7) using {@link SecureRandom}. This secret key can be used to generate a QR code
     * to be shared with the user. The length of the key can be declared using {@link #generateSecretBase32(int)}.
     * 
     * @return 16 bytes long secret key
     * @throws BaseException
     *             if any exception occurs
     * @see #generateSecretBase32(int)
     */
    public String generateSecretBase32() throws BaseException;

    /**
     * Returns a fix 16 bytes long secret key using {@link SecureRandom}. Valid byte values in the array are between 0 and 32 for later readability.
     * 
     * @return 16 bytes long secret key
     * @throws BaseException
     *             if any exception occurs
     * @see #generateSecret(int)
     */
    public byte[] generateSecret() throws BaseException;

    /**
     * Returns a secret key with length given in bytes using {@link SecureRandom}. Valid byte values in the array are between 0 and 32 for later
     * readability.
     *
     * @param length
     *            length of generated secret in bytes
     * @return secret key
     * @throws BaseException
     *             if any exception occurs
     * @see #generateSecret()
     */
    public default byte[] generateSecret(int length) throws BaseException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) random.nextInt(32);
        }
        return bytes;
    }

    /**
     * Base32 encodes given byte array.
     *
     * @param arr
     *            byte array to encode
     * @return encoded array
     */
    private String encodeWithBase32(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            int val = arr[i];
            if (val < 26) {
                sb.append((char) ('A' + val));
            } else {
                sb.append((char) ('2' + (val - 26)));
            }
        }
        return sb.toString();
    }
}
