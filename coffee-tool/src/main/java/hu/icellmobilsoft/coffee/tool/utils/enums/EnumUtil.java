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
package hu.icellmobilsoft.coffee.tool.utils.enums;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for enums.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class EnumUtil {

    /**
     * Enum to enum conversion
     *
     * @param <B>
     *            type of desired enum
     * @param source
     *            enum to convert
     * @param targetClass
     *            class of desired enum
     * @return instance of {@code targetClass} (having same name as {@code source}) - or null, if source is null
     */
    public static <B extends Enum<B>> B convert(Enum<?> source, Class<B> targetClass) {
        if (source == null || targetClass == null) {
            return null;
        }
        return EnumUtils.getEnum(targetClass, source.name());
    }

    /**
     * Enum to enum comparison by name
     *
     * @param source
     *            first enum to compare
     * @param target
     *            second enum to compare
     * @return whether enum names are equal
     */
    public static boolean equalName(Enum<?> source, Enum<?> target) {
        if (source == null && target == null) {
            return true;
        } else if (source == null || target == null) {
            return false;
        }
        return StringUtils.equals(source.name(), target.name());
    }
}
