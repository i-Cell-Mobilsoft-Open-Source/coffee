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
package hu.icellmobilsoft.coffee.dto.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Base service url paths
 *
 * @author imre.scheffer
 * @author czenczl
 * @since 1.0.0
 */
public class BaseServicePath {

    private static Logger log = Logger.getLogger(BaseServicePath.class);

    private static final String EMTPY = "";
    private static final String SIGN_QUESTION = "?";
    private static final String SIGN_EQUALS = "=";
    private static final String SIGN_AND = "&";

    /** Constant <code>PARAM_ID="id"</code> */
    public static final String PARAM_ID = "id";

    /** Constant <code>PARAM_USER_ID="userId"</code> */
    public static final String PARAM_USER_ID = "userId";

    /** Constant <code>PARAM_DATE="date"</code> */
    public static final String PARAM_DATE = "date";

    /** Constant <code>DATE="/{ + PARAM_DATE + }"</code> */
    public static final String DATE = "/{" + PARAM_DATE + "}";

    /** Constant <code>ID="/{ + PARAM_ID + }"</code> */
    public static final String ID = "/{" + PARAM_ID + "}";

    /** Constant <code>USER_ID="/{ + PARAM_USER_ID + }"</code> */
    public static final String USER_ID = "/{" + PARAM_USER_ID + "}";

    /** Constant <code>LIST="/list"</code> */
    public static final String LIST = "/list";

    /** Constant <code>INFO="/info"</code> */
    public static final String INFO = "/info";

    /** Constant <code>QUERY="/query"</code> */
    public static final String QUERY = "/query";

    /** Constant <code>REST="/rest"</code> */
    public static final String REST = "/rest";

    /** Constant <code>SYSTEM="/system"</code> */
    public static final String SYSTEM = "/system";

    /** Constant <code>PUBLIC="/public"</code> */
    public static final String PUBLIC = "/public";

    /** Constant <code>EXTERNAL="/external"</code> */
    public static final String EXTERNAL = "/external";

    /** Constant <code>INTERNAL="/internal"</code> */
    public static final String INTERNAL = "/internal";

    /**
     * Fill, join and append query parameters to path.
     *
     * @param path
     *            Path string.
     * @param queryParams
     *            Query parameters map.
     * @return Filled path.
     */
    public static String query(String path, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            log.debug(MessageFormat.format("No query parameter arrived to add to path [{0}] ", path));
            return path;
        }

        List<String> queryParts = queryParams.entrySet().stream().map(e -> encodeValue(e.getKey()) + SIGN_EQUALS + encodeValue(e.getValue()))
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder(path);
        sb.append(SIGN_QUESTION);
        sb.append(join(queryParts, SIGN_AND));
        return sb.toString();
    }

    /**
     * Fill parameter in path
     *
     * @param urlPath
     *            Path string.
     * @param paramKey
     *            Parameter key.
     * @param value
     *            Parameter value.
     * @return Filled path.
     */
    public static String fillParam(String urlPath, String paramKey, String value) {
        String encodedParamKey = encodeValue(paramKey);
        String encodedValue = encodeValue(value);
        return replace(urlPath, "{" + encodedParamKey + "}", encodedValue);
    }

    /**
     * URL encode string value.
     *
     * @param value
     *            value to encode
     * @return encoded value
     */
    public static String encodeValue(String value) {
        if (isBlank(value)) {
            return value;
        }

        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // Not possible case for UTF_8.
            log.error(e.getLocalizedMessage(), e);
            return value;
        }
    }

    /**
     * join strings for path
     * 
     * @param strings
     *            to concatenate
     * @return the concatenated strings
     */
    public static String path(String... strings) {
        if (strings == null) {
            return null;
        }
        return join(Arrays.asList(strings), EMTPY);
    }

    /**
     * join strings with delimiter
     * 
     * @param strings
     *            to concatenate
     * @param delimiter
     *            separated by the specified delimiter
     * 
     * @return the concatenated string list
     */
    public static String join(List<String> strings, String delimiter) {
        if (strings == null) {
            return null;
        }
        return strings.stream().collect(Collectors.joining(delimiter));
    }

    /**
     * isBlank
     * 
     * @param string
     *            string to check
     * @return true if string is blank
     */
    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    /**
     * String replace based on org.apache.commons.lang3.StringUtils
     * 
     * @param text
     *            text to search and replace in
     * @param searchString
     *            text to search and replace in
     * @param replacement
     *            the String to replace it with
     * @return the replaced string
     */
    public static String replace(String text, String searchString, String replacement) {
        if (isBlank(text) || isBlank(searchString) || isBlank(replacement)) {
            return text;
        }
        String searchText = text;
        int INDEX_NOT_FOUND = -1;
        int max = -1;
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == INDEX_NOT_FOUND) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= max < 0 ? 16 : max > 64 ? 64 : max;
        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != INDEX_NOT_FOUND) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

}
