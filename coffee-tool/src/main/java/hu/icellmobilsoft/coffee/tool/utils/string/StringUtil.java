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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

/**
 * StringUtil
 *
 * @author imre.scheffer
 * @author czenczl
 * @since 1.0.0
 */
@Vetoed
public class StringUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(StringUtil.class);

    /**
     * Sharp s biztonságos uppercaselése
     *
     * @param string
     */
    public static String upperCase(final String string) {
        if (StringUtils.isBlank(string)) {
            return null;
        }
        String upper = string;
        if (string.contains("ß")) {
            upper = string.replaceAll("ß", "ẞ");
        }
        return StringUtils.upperCase(upper);
    }

    /**
     * URL encode string value.
     *
     * @param value
     */
    public static String encodeValue(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Not possible case for UTF_8.
            LOGGER.error(e);
            return value;
        }
    }

    /**
     * Masks value, if key ignore-case matches keyPattern.
     *
     * @param key
     *            The key to check against keyPattern.
     * @param value
     *            The value to mask
     * @param keyPattern
     *            Regex to check against
     * @return "*" if key and keyPattern are not blank and key matches keyPattern (case ignored); value otherwise
     */
    public static String maskPropertyValue(String key, Object value, String keyPattern) {
        String valueStr = value == null ? null : value.toString();
        if (StringUtils.isNoneBlank(key, keyPattern, valueStr)) {
            Pattern pattern = Pattern.compile(keyPattern, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                return "*";
            }
        }
        return valueStr;
    }

    /**
     * Masks values belonging to properties ignore-case matching keyPattern in XML or JSON texts. ie.:<br>
     * keypattern = {@code .*?(pass).*?}
     * <table border="1">
     * <tbody>
     * <tr>
     * <td>text</td>
     * <td>result</td>
     * </tr>
     * <tr>
     * <td>{@code <pass>abc</pass>}</td>
     * <td>{@code <pass>*</pass>}</td>
     * </tr>
     * <tr>
     * <td>{@code <userPassword>abc</userPassword>}</td>
     * <td>{@code <userPassword>*</userPassword>}</td>
     * </tr>
     * <tr>
     * <td>{@code <userName>abc</userName>}</td>
     * <td>{@code <userName>abc</userName>}</td>
     * </tr>
     * <tr>
     * <td>"pass":"abc"</td>
     * <td>"pass":"*"</td>
     * </tr>
     * <tr>
     * <td>"userPassword":"abc"</td>
     * <td>"userPassword":"*"</td>
     * </tr>
     * <tr>
     * <td>"userName":"abc"</td>
     * <td>"userName":"abc"</td>
     * </tr>
     * </tbody>
     * </table>
     *
     * @param text
     *            XML or JSON text to replace sensitive data
     * @param keyPattern
     *            The pattern to which keys are checked
     */
    public static String maskValueInXmlJson(String text, String keyPattern) {
        String result = text;
        if (StringUtils.isNoneBlank(text, keyPattern)) {
            // xml replacement: pl.<(.*?(pass|secret).*?)>(.*?)<
            // <somethingPasswordLike>abc123</somethingPasswordLike> -> <somethingPasswordLike>*</somethingPasswordLike>
            result = replaceAllIgnoreCase(result, "<(" + keyPattern + ")>(.*?)<", "<$1>*<");
            // json replacement: pl. (".*?(pass|secret).*?" *?: *?)"(.*?)"
            // "somethingPasswordLike":"abc123" -> "somethingPasswordLike":"*"
            result = replaceAllIgnoreCase(result, "(\"" + keyPattern + "\" *?: *?)\"(.*?)\"", "$1\"*\"");
        }
        return result;
    }

    /**
     * Replace a regex in text.
     *
     * Basically the same as {@code text.replaceAll(regex, replacement)} but replace is case insensitive.
     *
     * @param text
     *            the text to check
     * @param regex
     *            the regular expression to which this string is to be matched
     * @param replacement
     *            the string to be substituted for each match
     */
    public static String replaceAllIgnoreCase(String text, String regex, String replacement) {
        // replacement-re csak null-t ellenőrzünk, üres Stringre engedjük cserélni
        if (StringUtils.isNoneBlank(text, regex) && replacement != null) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            return matcher.replaceAll(replacement);
        }
        return text;
    }

}
