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
package hu.icellmobilsoft.coffee.tool.utils.clazz;

import jakarta.enterprise.inject.Vetoed;

/**
 * Resource utils
 *
 * @author robert.kaplar
 * @since 1.0.0
 */
@Vetoed
public class ResourceUtil {

    private static final String POSTFIX_JAR = ".jar";
    private static final String POSTFIX_WAR = ".war";

    private static final String PATH_SEPARATOR = "/";

    /**
     * Determine which JAR or WAR file a class is from
     *
     * @param clazz
     *            class to search for
     * @return JAR or WAR file location
     */
    public static String getAppName(Class<?> clazz) {
        if (clazz == null || clazz.getProtectionDomain() == null || clazz.getProtectionDomain().getCodeSource() == null
                || clazz.getProtectionDomain().getCodeSource().getLocation() == null) {
            return null;
        }
        String result = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
        result = truncatePostfix(result, POSTFIX_JAR);
        result = truncatePostfix(result, POSTFIX_WAR);
        result = truncatePrefix(result, PATH_SEPARATOR);
        return result;
    }

    private static String truncatePostfix(String result, String postfix) {
        if (result.indexOf(postfix) != -1) {
            result = result.substring(0, result.indexOf(postfix));
        }
        return result;
    }

    private static String truncatePrefix(String result, String prefix) {
        if (result.indexOf(prefix) != -1) {
            result = result.substring(result.lastIndexOf(prefix) + 1);
        }
        return result;
    }
}
