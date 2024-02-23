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

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.exception.BaseException;
import hu.icellmobilsoft.coffee.exception.InvalidParameterException;

/**
 * Utility for function parameter validation.
 *
 * @author attila.kiss
 * @since 2.3.0
 */
public class ParamValidatorUtil {

    private ParamValidatorUtil() {
    }

    /**
     * Ensures that the specified parameter cannot be <code>null</code>.
     *
     * @param <T>
     *            the type of the parameter
     * @param object
     *            the parameter
     * @param paramName
     *            the name of the parameter
     * @return the not <code>null</code> parameter
     * @throws BaseException
     *             if the parameter is <code>null</code>
     */
    public static <T> T requireNonNull(T object, String paramName) throws BaseException {
        if (Objects.isNull(object)) {
            throw newInvalidParameterException("[{0}] object is null!", paramName);
        }
        return object;
    }

    /**
     * Ensures that the specified parameter cannot be blank {@link String}.
     *
     * @param object
     *            the parameter
     * @param paramName
     *            the name of the parameter
     * @return the non blank parameter
     * @throws BaseException
     *             if the parameter is blank
     */
    public static String requireNonBlank(String object, String paramName) throws BaseException {
        if (StringUtils.isBlank(object)) {
            throw newInvalidParameterException("[{0}] object is blank!", paramName);
        }
        return object;
    }

    /**
     * Ensures that the specified parameter cannot be an empty {@link Optional}.
     *
     * @param <T>
     *            the type of the parameter
     * @param object
     *            the parameter
     * @param paramName
     *            the name of the parameter
     * @return the value of the non-empty {@link Optional}
     * @throws BaseException
     *             if the parameter is <code>null</code> or empty
     */
    public static <T> T requireNonEmpty(Optional<T> object, String paramName) throws BaseException {
        requireNonNull(object, paramName);
        return object.orElseThrow(() -> newInvalidParameterException("[{0}] object is empty!", paramName));
    }

    /**
     * Ensures that the specified parameter cannot be an empty {@link Collection}.
     *
     * @param <T>
     *            the type of the {@link Collection} parameter
     * @param object
     *            the parameter
     * @param paramName
     *            the name of the parameter
     * @return the non-empty {@link Collection}
     * @throws BaseException
     *             if the parameter is <code>null</code> or empty
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
