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
package hu.icellmobilsoft.coffee.module.totp.constant;

/**
 * <p>TOtpConfigKey interface.</p>
 *
 * @author tamas.cserhati
 * @since 1.0.0
 */
public interface TOtpConfigKey {

    /**
     * How many characters should the generated (T)OTP consist of?
     */
    public static final String PASSWORD_DIGITS_LENGTH = "totp.password.digits.length";

    /**
     * How long should a specific password remain valid (in milliseconds)?
     */
    public static final String PASSWORD_TIMESTEP_MILLISEC = "totp.password.timestep.millisec";

    /**
     * What hash algorithm should the OTP use? {@link hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm}.
     */
    public static final String PASSWORD_HASH_ALGORITHM = "totp.password.hash.algorithm";

    /**
     * When verifying the password, should {@link hu.icellmobilsoft.coffee.module.totp.TOtpVerifier#verify} allow checking additional time windows?<br>
     * For example, when the value is 1, {@link hu.icellmobilsoft.coffee.module.totp.TOtpVerifier#verify} checks the provided (T)OTP in the current, previous, and next time windows as well.
     */
    public static final String VERIFY_ADDITIONAL_WINDOWS_COUNT = "totp.verify.additional.windows.count";

    /**
     * Specifying the length of the secret used for OTP in bytes.
     */
    public static final String PASSWORD_SECRET_LENGTH = "totp.password.secret.length";
}
