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
package hu.icellmobilsoft.coffee.module.totp.config;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import org.apache.commons.lang3.EnumUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import hu.icellmobilsoft.coffee.module.totp.constant.TOtpConfigKey;
import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;

/**
 * (T)OTP generalashoz hasznalhato konfiguraciok
 *
 * @author tamas.cserhati
 * @since 1.0.0
 */
@Dependent
public class DefaultTOtpConfigImpl implements TOtpConfig {

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = TOtpConfigKey.PASSWORD_DIGITS_LENGTH, defaultValue = "6")
    private Provider<Integer> digitsLength;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = TOtpConfigKey.PASSWORD_TIMESTEP_MILLISEC, defaultValue = "30000")
    private Provider<Integer> timestep;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = TOtpConfigKey.PASSWORD_HASH_ALGORITHM, defaultValue = "HmacSHA1")
    private Provider<String> hashAlgorithm;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = TOtpConfigKey.PASSWORD_SECRET_LENGTH, defaultValue = "16")
    private Provider<Integer> secretLength;

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    @ConfigProperty(name = TOtpConfigKey.VERIFY_ADDITIONAL_WINDOWS_COUNT, defaultValue = "0")
    private Provider<Integer> verifyAdditionalWindowsCount;

    /** {@inheritDoc} */
    @Override
    public Integer getTimestep() {
        return timestep.get();
    }

    /** {@inheritDoc} */
    @Override
    public Integer getVerifyAdditionalWindowsCount() {
        return verifyAdditionalWindowsCount.get();
    }

    /** {@inheritDoc} */
    @Override
    public Integer getDigitsLength() {
        return digitsLength.get();
    }

    /** {@inheritDoc} */
    @Override
    public Integer getSecretLength() {
        return secretLength.get();
    }

    /** {@inheritDoc} */
    @Override
    public TOtpAlgorithm getHashAlgorithm() {
        return EnumUtils.getEnumIgnoreCase(TOtpAlgorithm.class, hashAlgorithm.get());
    }

}
