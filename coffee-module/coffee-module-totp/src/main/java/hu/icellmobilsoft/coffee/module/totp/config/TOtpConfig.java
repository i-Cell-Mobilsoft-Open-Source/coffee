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

import hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm;

/**
 * <p>TOtpConfig interface.</p>
 *
 * @author tamas.cserhati
 * @since 1.0.0
 */
public interface TOtpConfig {

    /**
     * The timestep in seconds we use for generating totp, default: 30
     */
    public Integer getTimestep();

    /**
     * The extra time window we need to check when comparing otp, default 0<br>
     * Possible values: 0, 1, 2, 3...
     */
    public Integer getVerifyAdditionalWindowsCount();

    /**
     * OTP length, default 6
     */
    public Integer getDigitsLength();

    /**
     * Hash algorithm we use for generating totp, default: HmacSha1<br>
     * possible values: {@code TOtpAlgorithm.name()}
     */
    public TOtpAlgorithm getHashAlgorithm();

    /**
     * The generated secret byte[] length<br>
     * minimum value should be 16
     */
    public Integer getSecretLength();
}
