/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.validation;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.inject.Vetoed;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;

/**
 * Paraméter validáló utility.
 *
 * @author attila.kiss
 */
@Vetoed
public class ParamValidatorUtil {

    private ParamValidatorUtil() {
    }

    /**
     * Biztosítja, hogy a megadott paraméter ne lehessen <code>null</code>.
     *
     * @param <T>
     *            a paraméter típusa
     * @param object
     *            a paraméter
     * @param paramName
     *            a paraméter neve
     * @return a nem null paraméter
     * @throws BaseException
     *             ha a paraméter <code>null</code>
     */
    public static <T> T requireNonNull(T object, String paramName) throws BaseException {
        if (Objects.isNull(object)) {
            throw newInvalidParameterException("[{0}] object is null!", paramName);
        }
        return object;
    }

    /**
     * Biztosítja, hogy a megadott paraméter ne lehessen blank {@link String}.
     *
     * @param object
     *            a paraméter
     * @param paramName
     *            a paraméter neve
     * @return a nem blank paraméter
     * @throws BaseException
     *             ha a paraméter blank
     */
    public static String requireNonBlank(String object, String paramName) throws BaseException {
        if (StringUtils.isBlank(object)) {
            throw newInvalidParameterException("[{0}] object is blank!", paramName);
        }
        return object;
    }

    /**
     * Biztosítja, hogy a megadott paraméter ne lehessen üres {@link Optional}.
     *
     * @param object
     *            a paraméter
     * @param paramName
     *            a paraméter neve
     * @return a nem üres {@link Optional} paraméter értéke
     * @throws BaseException
     *             ha a paraméter üres vagy <code>null</code>
     */
    public static <T> T requireNonEmpty(Optional<T> object, String paramName) throws BaseException {
        requireNonNull(object, paramName);
        return object.orElseThrow(() -> newInvalidParameterException("[{0}] object is empty!", paramName));
    }

    /**
     * Biztosítja, hogy a megadott paraméter ne lehessen üres {@link Collection}.
     *
     * @param object
     *            a paraméter
     * @param paramName
     *            a paraméter neve
     * @return a nem üres paraméter
     * @throws BaseException
     *             ha a paraméter üres vagy <code>null</code>
     */
    public static <T> Collection<T> requireNonEmpty(Collection<T> object, String paramName) throws BaseException {
        requireNonNull(object, paramName);
        if (object.isEmpty()) {
            throw newInvalidParameterException("[{0}] object is empty!", paramName);
        }
        return object;
    }

    private static BaseException newInvalidParameterException(String messagePattern, Object... messageArguments) {
        return new InvalidParameterException(MessageFormat.format(messagePattern, messageArguments));
    }

}
