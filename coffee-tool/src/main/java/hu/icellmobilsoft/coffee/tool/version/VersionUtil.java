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
package hu.icellmobilsoft.coffee.tool.version;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

/**
 * Version helper methods.
 *
 * @author czenczl
 * @since 1.0.0
 */
@Vetoed
public class VersionUtil {

    /**
     * Compares {@code version1} and {@code version2} as {@link ComparableVersion}s. Returns whether {@code version1} is equal or above
     * {@code version2}.
     *
     * @param version1
     *            the first version parameter to compare
     * @param version2
     *            the second version parameter to compare
     * @return true if version1 &gt;= version2 <br>
     *         false if version1 &lt; version2 <br>
     *         false if version1 or version2 is blank
     */
    public static boolean equalOrAbove(String version1, String version2) {
        if (StringUtils.isAnyBlank(version1, version2)) {
            return false;
        }
        ComparableVersion v1 = new ComparableVersion(version1);
        ComparableVersion v2 = new ComparableVersion(version2);

        return v1.compareTo(v2) >= 0;
    }

    /**
     * Compares {@code version1} and {@code version2} as {@link ComparableVersion}s. Returns whether {@code version1} is below {@code version2}.
     *
     * @param version1
     *            the first version parameter to compare
     * @param version2
     *            the second version parameter to compare
     * @return true if version1 &lt; version2 <br>
     *         false if version1 &gt;= version2 <br>
     *         false if version1 or version2 is blank
     */
    public static boolean below(String version1, String version2) {
        return !equalOrAbove(version1, version2);
    }

}
