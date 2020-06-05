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
     * Generalt (T)OTP hany karakterbol alljon
     */
    public static final String PASSWORD_DIGITS_LENGTH = "totp.password.digits.length";

    /**
     * Mennyi ideig legyen ervenyes egy adott jelszo (msec).
     */
    public static final String PASSWORD_TIMESTEP_MILLISEC = "totp.password.timestep.millisec";

    /**
     * Az OTP milyen hash algoritmust hasznaljon {@link hu.icellmobilsoft.coffee.module.totp.enums.TOtpAlgorithm}.
     */
    public static final String PASSWORD_HASH_ALGORITHM = "totp.password.hash.algorithm";

    /**
     * A jelszo ellenorzesekor {@link hu.icellmobilsoft.coffee.module.totp.TOtpUtil#verify} engedjen-e tovabbi idoablakokat vizsgalni<br>
     * pl. 1-es ertek eseten megvizsgalja a megadott (T)OTP-t a kovetkezo es elozo idoablakban is
     */
    public static final String VERIFY_ADDITIONAL_WINDOWS_COUNT = "totp.verify.additional.windows.count";

    /**
     * az OTP-hez hasznalt secret hossza byteban megadva.
     */
    public static final String PASSWORD_SECRET_LENGTH = "totp.password.secret.length";
}
