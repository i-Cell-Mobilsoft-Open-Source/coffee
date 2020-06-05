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
 * <p>TOtpGenerator interface.</p>
 *
 * @author cstamas
 * @since 1.0.0
 */
public interface TOtpGenerator {

    /**
     * General egy TOTP-t a parameterben kapott kulcs, UTC idopont, otp hossz es hash algoritmus alapjan (RFC 6238)
     *
     * @param secretKey
     *            - titkos kulcs
     * @param utcTimestamp
     *            - az OTP generáláshoz használt UTC timestamp
     * @param otpLength
     *            - a generat TOTP hossza (1-9 kozott)
     * @param hashAlgorithm
     *            - az OTP generáláshoz használt hash algoritmus
     * @throws BaseException
     */
    public String generatePassword(byte[] secretKey, long utcTimestamp, int otpLength, TOtpAlgorithm hashAlgorithm) throws BaseException;

    /**
     * General egy TOTP-t a parameterben kapott epoch time es kulcs alapjan a configban beallitott hash algoritmussal es otp hosszal
     *
     * @param secretKey
     *            - titkos kulcs
     * @param utcTimestamp
     *            - az OTP generáláshoz használt UTC timestamp
     */
    public String generatePassword(byte[] secretKey, long utcTimestamp) throws BaseException;

    /**
     * General egy TOTP-t a szerver aktualis epoch time-ja es a parameterben kapott kulcs alapjan a configban beallitott hash algoritmussal
     *
     * @param secretKey
     *            - titkos kulcs
     */
    public default String generatePassword(byte[] secretKey) throws BaseException {
        return generatePassword(secretKey, System.currentTimeMillis());
    }

    /**
     * Hasonlo a {@link #generateSecretBase32()} -hoz, de megadhato a secretKey hossza byteban
     *
     * @param length
     *            - a titkos kulcs hossza
     */
    public default String generateSecretBase32(int length) throws BaseException {
        return encodeWithBase32(generateSecret(length));
    }

    /**
     * Egy 16 byte hosszu, base32 (A-Z2-7) formatumu titkos kulcsot hoz letre a {@link SecureRandom} hasznalataval. Segitsegevel generalhato egy QR
     * kod, ami megoszthato a felhasznaloval. Ha meg szeretnenk adni a kulcs hosszat, akkor hasznaljuk a {@link #generateSecretBase32(int)} metodust.
     */
    public String generateSecretBase32() throws BaseException;

    /**
     * Egy 16 byte hosszu titkos kulcsot hoz letre a {@link SecureRandom} hasznalataval. A tombben levo byteok csak 0-32 kozotti erteket vehetnek fel
     * a kesobbi olvashatosag miatt.
     */
    public byte[] generateSecret() throws BaseException;

    /**
     * Hasonlo a {@link #generateSecret()} -hez, de megadhato a secretKey hossza byteban. A tombben levo byteok csak 0-32 kozotti erteket vehetnek fel
     * a kesobbi olvashatosag miatt
     *
     * @param length
     *            - a titkos kulcs hossza
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
     * base32 enkodoljuk a megadott byte tombot (a tombben csak 0-32 kozott szerepelhetnek byteok a kesobbi olvashatosag miatt)
     *
     * @param arr
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
