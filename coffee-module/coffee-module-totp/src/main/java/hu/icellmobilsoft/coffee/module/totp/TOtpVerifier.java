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

import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * TOtpVerifier interface.
 *
 * @author cstamas
 * @since 1.0.0
 */
public interface TOtpVerifier {

    /**
     * Verifies given OTP against the OTP generated from the other parameters ({@code secretKey}, {@code utcTimestamp}, {@code hashAlgorithm}).
     *
     * @param secretKey
     *            secret key
     * @param verifiedOtp
     *            OTP to verify
     * @param utcTimestamp
     *            UTC timestamp for OTP generation, practically NTP synchronized actual time
     * @param hashAlgorithm
     *            hash algorithm for OTP generation
     * @throws BaseException
     *             if any exception occurs
     */
    public void verify(byte[] secretKey, String verifiedOtp, long utcTimestamp, TOtpAlgorithm hashAlgorithm) throws BaseException;

    /**
     * Verifies given OTP against the OTP generated from the other parameters ({@code secretKey}, {@code utcTimestamp}) and config default hash
     * algorithm.
     *
     * @param secretKey
     *            secret key
     * @param verifiedOtp
     *            OTP to verify
     * @param utcTimestamp
     *            UTC timestamp for OTP generation, practically NTP synchronized actual time
     * @throws BaseException
     *             if any exception occurs
     * @see #verify(byte[], String, long, TOtpAlgorithm)
     */
    public void verify(byte[] secretKey, String verifiedOtp, long utcTimestamp) throws BaseException;

    /**
     * Verifies given OTP against the OTP generated from {@code secretKey}, actual epoch time and config default hash algorithm.
     *
     * @param secretKey
     *            secret key
     * @param verifiedOtp
     *            OTP to verify
     * @throws BaseException
     *             if any exception occurs
     * @see #verify(byte[], String, long)
     */
    public default void verify(byte[] secretKey, String verifiedOtp) throws BaseException {
        verify(secretKey, verifiedOtp, System.currentTimeMillis());
    }

    /**
     * Verifies given TOTP in additional time windows.
     *
     * @param secretKey
     *            secret key
     * @param utcTimestamp
     *            UTC timestamp of the original OTP generation
     * @param verifiedOtp
     *            OTP to verify
     * @param hashAlgorithm
     *            hash algorithm of the original OTP generation
     * @return if TOTP verification is successful
     * @throws BaseException
     *             if any exception occurs
     */
    public boolean verifyOTPInAdditionalWindow(byte[] secretKey, long utcTimestamp, String verifiedOtp, TOtpAlgorithm hashAlgorithm)
            throws BaseException;

}
