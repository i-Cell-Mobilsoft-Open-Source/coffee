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
package hu.icellmobilsoft.coffee.module.otp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.AppLoggerImpl;
import hu.icellmobilsoft.coffee.cdi.logger.LogContainer;
import hu.icellmobilsoft.coffee.cdi.logger.LogProducer;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.BusinessException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.totp.TOtpGenerator;
import hu.icellmobilsoft.coffee.module.totp.TOtpVerifier;
import hu.icellmobilsoft.coffee.module.totp.config.DefaultTOtpConfigImpl;
import hu.icellmobilsoft.coffee.module.totp.config.TOtpConfig;
import hu.icellmobilsoft.coffee.module.totp.constant.TOtpConfigKey;
import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;
import hu.icellmobilsoft.coffee.module.totp.impl.DefaultTOtpGeneratorImpl;
import hu.icellmobilsoft.coffee.module.totp.impl.DefaultTOtpVerifierImpl;
import io.smallrye.config.inject.ConfigExtension;

/**
 * TOTPUtil osztaly unitteszt
 * 
 * @author cstamas
 *
 */
@EnableWeld
@Tag("weld")
@ExtendWith(WeldJunit5Extension.class)
@DisplayName("Testing TOtpGenerator class")
public class TOtpTest {

    @Inject
    @ThisLogger
    private AppLogger log;

    @Inject
    private TOtpGenerator totpGenerator;

    @Inject
    private TOtpVerifier totpVerifier;

    @Inject
    private TOtpConfig otpConfig;

    private static String secret;
    private static byte[] secretKey;
    static long currentTime;
    static long offsetTime;

    @SuppressWarnings("unchecked")
    @WeldSetup
    public WeldInitiator weld = WeldInitiator
            .from(WeldInitiator.createWeld().addExtensions(ConfigExtension.class).addBeanClasses(LogContainer.class, AppLoggerImpl.class,
                    LogProducer.class, DefaultTOtpConfigImpl.class, DefaultTOtpVerifierImpl.class, DefaultTOtpGeneratorImpl.class))
            .activate(RequestScoped.class).build();

    @BeforeAll
    public static void init() {
        System.setProperty(TOtpConfigKey.PASSWORD_TIMESTEP_MILLISEC, "30000");
        System.setProperty(TOtpConfigKey.PASSWORD_DIGITS_LENGTH, "6");
        System.setProperty(TOtpConfigKey.PASSWORD_HASH_ALGORITHM, TOtpAlgorithm.HMACSHA1.value());
        System.setProperty(TOtpConfigKey.PASSWORD_SECRET_LENGTH, "16");

        // ezzel a secrettel teszteltem az otp authenticatort
        secret = StringUtils.replace("APMC QKSC YITU IK5Z", " ", "");
        secretKey = new Base32().decode(secret);

        currentTime = System.currentTimeMillis();
        // direkt egy eltolt idot hasznalunk majd a jelszohoz
        LogProducer.getStaticDefaultLogger(TOtpTest.class).info("secret: [{0}] ", secret);
    }

    public String generateTOtp() throws BaseException {
        return generateTOtp(0);
    }

    public String generateTOtp(long offset) throws BaseException {
        offsetTime = System.currentTimeMillis() + offset;
        log.info("offset.time: [{0}], current.time: [{1}]", offsetTime, currentTime);
        // generalunk egy direkt az idoablakon kivuli jelszot
        String otp = totpGenerator.generatePassword(secretKey, offsetTime);
        log.info("otp:[{0}] generated with algorithm: [{1}]", otp, otpConfig.getHashAlgorithm());
        // ez csak erdekessegkepp: az aktualis idoablakbol hatralevo ido masodpercben
        log.info("validity: [{0}] seconds]", 30 - Math.round((currentTime / 1000) % 30));

        return otp;
    }

    public void verifyOtp(byte[] secretKey, String generatedOtp, long currentTime) throws BaseException {
        log.info("extra check: [{0}]", otpConfig.getVerifyAdditionalWindowsCount());
        totpVerifier.verify(secretKey, generatedOtp, currentTime);
    };

    @Test
    @DisplayName("should throw INVALID_ONE_TIME_PASSWORD exception")
    public void generateInvalidTOtp() {
        // nincs extra idoablak vizsgalat, de masik idoablakban generaljuk a jelszot
        System.setProperty(TOtpConfigKey.VERIFY_ADDITIONAL_WINDOWS_COUNT, "2");
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            String otp = "123435";
            // a server es kliens kozotti idoeltolodast teszteljuk egy masik idoablakban torteno validalassal
            totpVerifier.verify(secretKey, otp, currentTime);
        });
        System.setProperty(TOtpConfigKey.VERIFY_ADDITIONAL_WINDOWS_COUNT, "0");
        assertTrue(exception.getFaultTypeEnum() == CoffeeFaultType.INVALID_ONE_TIME_PASSWORD);
    }

    @Test
    @DisplayName("should generate a valid otp")
    public void generateValidTOtp() {
        // beallitunk (+-) 1 extra idoablak vizsgalatot
        System.setProperty(TOtpConfigKey.VERIFY_ADDITIONAL_WINDOWS_COUNT, "1");
        Assertions.assertDoesNotThrow(() -> {
            String otp = generateTOtp();
            totpVerifier.verify(secretKey, otp, currentTime);
        });
    }

    @Test
    @DisplayName("should generate a valid otp with 8 digits")
    public void generateDifferentLength() {
        int digitsLength = 8;
        System.setProperty(TOtpConfigKey.PASSWORD_DIGITS_LENGTH, String.valueOf(digitsLength));
        Assertions.assertDoesNotThrow(() -> {
            String otp = generateTOtp();
            assertTrue(otp.length() == digitsLength);
        });
    }

    @Test
    @DisplayName("testing with different hmac")
    public void generateDifferentAlgorithm() {
        try {
            String otp1 = generateTOtp();
            System.setProperty(TOtpConfigKey.PASSWORD_HASH_ALGORITHM, TOtpAlgorithm.HMACSHA512.value());
            String otp512 = generateTOtp();
            Assertions.assertDoesNotThrow(() -> {
                assertFalse(StringUtils.equals(otp1, otp512));
            });

        } catch (Exception e) {
            assertNull(e);
        } finally {
            System.setProperty(TOtpConfigKey.PASSWORD_HASH_ALGORITHM, TOtpAlgorithm.HMACSHA1.value());
        }
    }

}
