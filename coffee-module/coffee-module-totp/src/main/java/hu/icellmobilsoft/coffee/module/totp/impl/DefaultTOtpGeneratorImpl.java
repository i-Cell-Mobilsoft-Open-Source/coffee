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

import java.math.BigInteger;
import java.security.GeneralSecurityException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.exception.TechnicalException;
import hu.icellmobilsoft.coffee.module.totp.TOtpGenerator;
import hu.icellmobilsoft.coffee.module.totp.config.TOtpConfig;
import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;

/**
 *
 * T(ime Based) OTP es titkos kulcs generalashoz szukseges osztaly default implementcaioja
 *
 * @author cstamas
 * @since 1.0.0
 */
@Dependent
public class DefaultTOtpGeneratorImpl implements TOtpGenerator {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TOtpConfig otpConfig;

    private static final int[] DIGITS_POWER = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };

    /**
     * Default constructor, constructs a new object.
     */
    public DefaultTOtpGeneratorImpl() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public String generatePassword(byte[] secretKey, long currentUTCTimestamp, int otpLength, TOtpAlgorithm hashAlgorithm) throws BaseException {
        if (ObjectUtils.isEmpty(secretKey) || hashAlgorithm == null) {
            throw new InvalidParameterException("otp.generatePassword parameters cannot be null or empty");
        }
        long timeWindow = currentUTCTimestamp / otpConfig.getTimestep();
        String sTimeMs = StringUtils.leftPad(Long.toHexString(timeWindow).toUpperCase(), 16, '0');
        byte[] msg = hexStr2Bytes(sTimeMs);
        byte[] hash = hmacSha(hashAlgorithm, secretKey, msg);
        int offset = hash[(hash.length - 1)] & 0xF;
        int binary = (hash[offset] & 0x7F) << 24 | (hash[(offset + 1)] & 0xFF) << 16 | (hash[(offset + 2)] & 0xFF) << 8 | hash[(offset + 3)] & 0xFF;
        int otp = binary % DIGITS_POWER[otpLength];
        return StringUtils.leftPad(Integer.toString(otp), otpLength, '0');
    }

    /** {@inheritDoc} */
    @Override
    public String generatePassword(byte[] secretKey, long currentUTCTimestamp) throws BaseException {
        return generatePassword(secretKey, currentUTCTimestamp, otpConfig.getDigitsLength(), otpConfig.getHashAlgorithm());
    }

    /**
     * This method uses the JCE to provide the crypto algorithm. HMAC computes a Hashed Message Authentication Code with the crypto hash algorithm as
     * a parameter.
     *
     * @param crypto:
     *            the crypto algorithm (HmacSHA1, HmacSHA256, HmacSHA512)
     * @param keyBytes:
     *            the bytes to use for the HMAC key
     * @param text:
     *            the message or text to be authenticated
     */
    private byte[] hmacSha(TOtpAlgorithm crypto, byte[] keyBytes, byte[] text) throws BaseException {
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto.value());
            SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
        }
    }

    /**
     * Converts hexadecimal {@link String} to byte arary.
     * 
     * @param hex
     *            hexadecimal representation
     * @return byte array representation
     */
    private byte[] hexStr2Bytes(String hex) {
        byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();

        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length; ++i)
            ret[i] = bArray[(i + 1)];
        return ret;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] generateSecret() throws BaseException {
        return generateSecret(otpConfig.getSecretLength());
    }

    /** {@inheritDoc} */
    @Override
    public String generateSecretBase32() throws BaseException {
        return generateSecretBase32(otpConfig.getSecretLength());
    }

}
