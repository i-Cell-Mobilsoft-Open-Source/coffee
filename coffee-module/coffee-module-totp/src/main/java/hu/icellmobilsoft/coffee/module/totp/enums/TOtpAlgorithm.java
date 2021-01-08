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
package hu.icellmobilsoft.coffee.module.totp.enums;

/**
 * TOTP generalashoz hasznalt hmac algoritmus tipusok
 *
 * @author tamas.cserhati
 * @since 1.0.0
 */
public enum TOtpAlgorithm {

    /**
     * HmacSHA1
     */
    HMACSHA1("HmacSHA1"),
    /**
     * HmacSHA256
     */
    HMACSHA256("HmacSHA256"),
    /**
     * HmacSHA512
     */
    HMACSHA512("HmacSHA512");

    private final String value;

    private TOtpAlgorithm(String value) {
        this.value = value;
    }

    /**
     * Returns {@link TOtpAlgorithm} from {@link String} value.
     *
     * @param value
     *            {@code String} value
     * @return corresponding {@code TOtpAlgorithm} or null if matching value does not exist
     */
    public static TOtpAlgorithm valueFrom(String value) {
        for (TOtpAlgorithm a : values()) {
            if (a.value.equals(value)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Getter for the field {@code value}.
     * 
     * @return value
     */
    public String value() {
        return this.value;
    }

}
