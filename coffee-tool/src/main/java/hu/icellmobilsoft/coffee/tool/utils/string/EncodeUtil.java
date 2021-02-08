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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.Objects;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Util class for encoding
 *
 * @author balazs.joo
 * @since 1.0.0
 */
@Vetoed
public class EncodeUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(EncodeUtil.class);

    /**
     * Encodes input {@link String} with SHA-512.
     *
     * @param str
     *            input {@code String}
     * @return encoded {@code String} or null if invalid input or encoding error
     */
    public static String Sha512(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        String sha = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-512");
            crypt.reset();
            crypt.update(str.getBytes(StandardCharsets.UTF_8));
            sha = byteToHex(crypt.digest());
        } catch (NullPointerException e) {
            LOGGER.error("Error in get SHA-512: encoded string is null!", e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(MessageFormat.format("Error in get SHA-512 from [{0}]", str), e);
        }

        return StringUtils.upperCase(sha);
    }

    /**
     * Format input {@code byte[]} to HEX {@link String}
     *
     * @param hash
     *            byte array
     * @return HEX {@code String}
     */
    public static String byteToHex(final byte[] hash) {
        if (Objects.isNull(hash)) {
            return null;
        }

        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
