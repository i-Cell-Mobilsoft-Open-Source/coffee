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

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;

/**
 * <p>TOtpVerifier interface.</p>
 *
 * @author cstamas
 * @since 1.0.0
 */
public interface TOtpVerifier {

    /**
     * A parameterben kapott OTP-t a tobbi parameter segitsegevel generalt OTP-vel hasonlitja ossze
     *
     * @param secretKey
     *            - titkos kulcs
     * @param verifiedOtp
     *            - az ellenőrizendő OTP
     * @param utcTimestamp
     *            - UTC timestamp, amivel az ellenőrizendő OTP-t generáljuk, célszerűen NTP segítségével szinkronizált aktuális idő
     * @param hashAlgorithm
     *            - OTP generáláshoz használandó hash algoritmus
     * @throws BaseException
     */
    public void verify(byte[] secretKey, String verifiedOtp, long utcTimestamp, TOtpAlgorithm hashAlgorithm) throws BaseException;

    /**
     * A parameterben kapott OTP-t az a parameterben kapott epoch time es default konfiguracio alapjan generalt OTP-vel hasonlitja ossze
     *
     * @param secretKey
     *            - titkos kulcs
     * @param verifiedOtp
     *            - az ellenőrizendő OTP
     * @param utcTimestamp
     *            - UTC timestamp, amivel az ellenőrizendő OTP-t generáljuk, célszerűen NTP segítségével szinkronizált aktuális idő
     * @throws BaseException
     */
    public void verify(byte[] secretKey, String verifiedOtp, long utcTimestamp) throws BaseException;

    /**
     * A parameterben kapott OTP-t az aktualis epoch time es default konfiguracio alapjan generalt OTP-vel hasonlitja ossze
     *
     * @param secretKey
     *            - titkos kulcs
     * @param verifiedOtp
     *            - az ellenőrizendő OTP
     * @throws BaseException
     */
    public default void verify(byte[] secretKey, String verifiedOtp) throws BaseException {
        verify(secretKey, verifiedOtp, System.currentTimeMillis());
    }

    /**
     * Tovabbi idoablakokban is ellenorzi a kapott TOTP-t
     *
     * @param secretKey
     *            - titkos kulcs
     * @param utcTimestamp
     *            - az eredeti OTP generáláshoz használmt UTC timestamp
     * @param verifiedOtp
     *            - az ellenőrizendő OTP
     * @param hashAlgorithm
     *            - az OTP generáláshoz használt hash algoritmus
     */
    public boolean verifyOTPInAdditionalWindow(byte[] secretKey, long utcTimestamp, String verifiedOtp, TOtpAlgorithm hashAlgorithm)
            throws BaseException;

}
