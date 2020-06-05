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
package hu.icellmobilsoft.coffee.rest.url;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import hu.icellmobilsoft.coffee.tool.utils.string.StringUtil;

/**
 * Base service url paths
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class BaseServicePath {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticLogger(BaseServicePath.class);

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
     * <p>path.</p>
     */
    public static String path(String... strings) {
        return StringUtils.join(strings);
    }

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
            LOGGER.debug(MessageFormat.format("No query parameter arrived to add to path [{0}] ", path));
            return path;
        }

        List<String> queryParts = queryParams.entrySet().stream()
                .map(e -> StringUtil.encodeValue(e.getKey()) + SIGN_EQUALS + StringUtil.encodeValue(e.getValue())).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder(path);
        sb.append(SIGN_QUESTION);
        sb.append(StringUtils.join(queryParts, SIGN_AND));
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
        String encodedParamKey = StringUtil.encodeValue(paramKey);
        String encodedValue = StringUtil.encodeValue(value);
        return StringUtils.replace(urlPath, "{" + encodedParamKey + "}", encodedValue);
    }

}
