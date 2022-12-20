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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * StringUtil
 *
 * @author imre.scheffer
 * @author czenczl
 * @since 1.0.0
 */
@Vetoed
public class StringUtil {

    private static Logger LOGGER = Logger.getLogger(StringUtil.class);
    private static RegexPatternCache patternCache = new RegexPatternCache();

    /**
     * Transforms input {@link String} to upper-case. Transforms "ß" to "ẞ".
     *
     * @param string
     *            input {@code String}
     * @return upper-case {@code String} or null if empty input
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
     * URL encode {@link String} value.
     *
     * @param value
     *            {@code String} to encode
     * @return URL encoded {@code String} or {@code value} if empty input or encoding error
     */
    public static String encodeValue(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Not possible case for UTF_8.
            LOGGER.error(e.getLocalizedMessage(), e);
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
     * @param keyPatterns
     *            Regex array to check against
     * @return "*" if key and keyPattern are not blank and key matches keyPattern (case ignored); value otherwise
     */
    public static String maskPropertyValue(String key, Object value, String... keyPatterns) {
        String valueStr = toString(value);
        if (StringUtils.isNoneBlank(key, valueStr) && keyPatterns != null) {
            for (String keyPattern : keyPatterns) {
                if (StringUtils.isNotBlank(keyPattern)) {
                    Pattern pattern = patternCache.getPattern(keyPattern);
                    Matcher matcher = pattern.matcher(key);
                    if (matcher.matches()) {
                        return "*";
                    }
                }
            }
        }
        return valueStr;
    }

    /**
     * Makes String out of value, returning null for null and unwrapping not empty optionals.
     * 
     * @param value
     *            Object to convert
     * @return - {@code null} if value is null <br>
     *         - {@code ((Optional<?>) value).get().toString()} if value is a not empty Optional <br>
     *         - {@code ArrayUtils.toString(value)} if value (or the value inside Optional) is an array <br>
     *         - {@code value.toString()} otherwise
     */
    private static String toString(Object value) {
        if (value == null) {
            return null;
        }
        Object unwrapped = value;
        if (value instanceof Optional && ((Optional<?>) value).isPresent()) {
            unwrapped = ((Optional<?>) value).get();
        }
        if (unwrapped.getClass().isArray()) {
            return ArrayUtils.toString(unwrapped);
        }
        return unwrapped.toString();
    }

    /**
     * Masks values belonging to properties ignore-case matching keyPattern in XML or JSON texts. ie.:<br>
     * keypattern = {@code .*?(pass).*?}
     * <table border="1">
     * <caption>Example input-output pairs</caption> <tbody>
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
     * @param keyPatterns
     *            The patterns to which keys are checked
     * @return masked {@code String}
     */
    public static String maskValueInXmlJson(String text, String... keyPatterns) {
        String result = text;
        if (StringUtils.isNotBlank(text) && keyPatterns != null) {
            for (String keyPattern : keyPatterns) {
                // xml replacement: pl.<(.*?(pass).*?)>(.*?)<
                // <somethingPasswordLike>abc123</somethingPasswordLike> -> <somethingPasswordLike>*</somethingPasswordLike>
                result = replaceAllIgnoreCase(result, "<(" + keyPattern + ")>(.*?)<", "<$1>*<");
                // json replacement: pl. (".*?(pass).*?" *?: *?)"(.*?)"
                // "somethingPasswordLike":"abc123" -> "somethingPasswordLike":"*"
                result = replaceAllIgnoreCase(result, "(\"" + keyPattern + "\" *?: *?)\"(.*?)\"", "$1\"*\"");
            }
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
     * @return replaced {@code String} or input {@code text} if invalid input
     */
    public static String replaceAllIgnoreCase(String text, String regex, String replacement) {
        // replacement-re csak null-t ellenőrzünk, üres Stringre engedjük cserélni
        if (StringUtils.isNoneBlank(text, regex) && replacement != null) {
            Pattern pattern = patternCache.getPattern(regex);
            Matcher matcher = pattern.matcher(text);
            return matcher.replaceAll(replacement);
        }
        return text;
    }

    /**
     * Mask authentication credentials in URI connection string.
     *
     * This function replaces the "username:password" part between the "//" and "@" signs in the URI with "*:*". E.g.:<br>
     * "mongodb://username:password@localhost:12345" becomes "mongodb://*:*@localhost:12345"
     *
     * @param uri
     *            the uri to mask
     * @return masked URI or unchanged if invalid input
     */
    public static String maskUriAuthenticationCredentials(String uri) {
        if (StringUtils.isBlank(uri)) {
            return uri;
        }

        String regex = "//.*@";
        String replacement = "//*:*@";

        Pattern pattern = patternCache.getPattern(regex);
        Matcher matcher = pattern.matcher(uri);

        return matcher.replaceAll(replacement);
    }
}
