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
package hu.icellmobilsoft.coffee.tool.utils.string;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Util class for password
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class PasswordUtil {

    /**
     * Encode string (password)
     *
     * @param string to encode
     */
    public static String encodeString(String string) {
        if (StringUtils.isBlank(string)){
            return null;
        }

        return DigestUtils.sha512Hex(string);
    }

    /**
     * Compares raw and encoded String
     *
     * @param string        raw String to
     * @param encodedString encoded String
     * @return true is raw String is same as encoded
     */
    public static boolean isSame(String string, String encodedString) {
        return StringUtils.equals(encodeString(string), encodedString);
    }

    /**
     * Check password strong, minimum 8 characters, 1 upper and 1 lower alpha , 1 numeric, 1 special.
     */
    public static boolean isStrong(String password) {
        if (StringUtils.isBlank(password) || password.length() < 8) {
            return false;
        }

        boolean numeric = false;
        boolean alphaLower = false;
        boolean alphaUpper = false;
        boolean special = false;
        for (char c : password.toCharArray()) {
            if (CharUtils.isAsciiNumeric(c)) {
                numeric = true;
            } else if (CharUtils.isAsciiAlphaLower(c)) {
                alphaLower = true;
            } else if (CharUtils.isAsciiAlphaUpper(c)) {
                alphaUpper = true;
            } else {
                special = true;
            }

            if (numeric && alphaLower && alphaUpper && special) {
                return true;
            }
        }

        return false;
    }

}
