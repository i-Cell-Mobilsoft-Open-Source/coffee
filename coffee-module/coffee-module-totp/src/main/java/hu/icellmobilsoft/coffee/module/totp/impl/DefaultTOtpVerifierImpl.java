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
package hu.icellmobilsoft.coffee.module.totp.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.BusinessException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.totp.TOtpGenerator;
import hu.icellmobilsoft.coffee.module.totp.TOtpVerifier;
import hu.icellmobilsoft.coffee.module.totp.config.TOtpConfig;
import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;

/**
 *
 * T(ime Based) OTP ellenorzesehez szukseges osztaly default implementcaioja
 *
 * @author cstamas
 * @since 1.0.0
 */
@Dependent
public class DefaultTOtpVerifierImpl implements TOtpVerifier {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TOtpConfig otpConfig;

    @Inject
    private TOtpGenerator totpGenerator;

    private static final String LOG_MESSAGE_VERIFICATION_FAILED = "OTP verification failed! client.otp: [{0}], server.otp: [{1}], server.timestamp: [{2}]";
    private static final String LOG_MESSAGE_EXTRA_VERIFICATION_FAILED = "OTP verification failed in extra time window: [{0}]! client.otp: [{1}], server.otp: [{2}], server.timestamp: [{3}]";
    private static final String LOG_MESSAGE_INVALID_PASSWORD = "Invalid password has been given [{0}]";
    private static final String EXCEPTION_MESSAGE_INVALID_PASSWORD = "OTP authentication failed, invalid password";

    /** {@inheritDoc} */
    @Override
    public void verify(byte[] secretKey, String verifiedOtp, long utcTimestamp, TOtpAlgorithm hashAlgorithm) throws BaseException {
        if (ObjectUtils.isEmpty(secretKey) || StringUtils.isBlank(verifiedOtp) || hashAlgorithm == null) {
            throw new InvalidParameterException("otp.verify parameters cannot be null or empty");
        }
        String serverOtp = totpGenerator.generatePassword(secretKey, utcTimestamp, verifiedOtp.length(), hashAlgorithm);
        if (StringUtils.equals(serverOtp, verifiedOtp)) {
            return;
        }
        log.debug(LOG_MESSAGE_VERIFICATION_FAILED, verifiedOtp, serverOtp, utcTimestamp);
        if (verifyOTPInAdditionalWindow(secretKey, utcTimestamp, verifiedOtp, hashAlgorithm)) {
            return;
        }
        log.debug(LOG_MESSAGE_INVALID_PASSWORD, verifiedOtp);
        throw new BusinessException(CoffeeFaultType.INVALID_ONE_TIME_PASSWORD, EXCEPTION_MESSAGE_INVALID_PASSWORD);
    }

    /** {@inheritDoc} */
    @Override
    public void verify(byte[] secretKey, String verifiedOtp, long utcTimestamp) throws BaseException {
        verify(secretKey, verifiedOtp, utcTimestamp, otpConfig.getHashAlgorithm());
    }

    /** {@inheritDoc} */
    @Override
    public boolean verifyOTPInAdditionalWindow(byte[] secretKey, long utcTimestamp, String verifiedOtp, TOtpAlgorithm hashAlgorithm)
            throws BaseException {

        Long timestamp = null;
        String serverOtp = null;

        for (int i = 0; i < otpConfig.getVerifyAdditionalWindowsCount(); i++) {
            timestamp = utcTimestamp - (i + 1) * otpConfig.getTimestep();
            serverOtp = totpGenerator.generatePassword(secretKey, timestamp, verifiedOtp.length(), hashAlgorithm);
            if (StringUtils.equals(verifiedOtp, serverOtp)) {
                return true;
            } else {
                log.debug(LOG_MESSAGE_EXTRA_VERIFICATION_FAILED, String.valueOf(i + 1), verifiedOtp, serverOtp, timestamp);
            }

            timestamp = utcTimestamp + (i + 1) * otpConfig.getTimestep();
            serverOtp = totpGenerator.generatePassword(secretKey, timestamp, verifiedOtp.length(), hashAlgorithm);
            if (StringUtils.equals(verifiedOtp, serverOtp)) {
                return true;
            } else {
                log.debug(LOG_MESSAGE_EXTRA_VERIFICATION_FAILED, String.valueOf(-(i + 1)), verifiedOtp, serverOtp, timestamp);
            }
        }
        return false;
    }
}
