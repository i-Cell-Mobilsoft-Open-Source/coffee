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
package hu.icellmobilsoft.coffee.jpa.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * <code>ReflectionUtils</code> - Reflection utilities holder from hypersistence-utils-60
 *
 * @author tamas.cserhati
 * @since 2.1.0
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Get the {@link Field} with the given name belonging to the provided Java {@link Class}.
     *
     * @param targetClass
     *            the provided Java {@link Class} the field belongs to
     * @param fieldName
     *            the {@link Field} name
     * @return the {@link Field} matching the given name or {@code null} if one parameter is null or empty
     * @throws BaseException
     *             on error
     */
    private static Field getField(Class<?> targetClass, String fieldName) throws BaseException {
        if (targetClass == null || StringUtils.isBlank(fieldName)) {
            return null;
        }

        Field field = null;

        try {
            field = targetClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try {
                field = targetClass.getField(fieldName);
            } catch (NoSuchFieldException ignore) {
                // ignore
            }

            if (!targetClass.getSuperclass().equals(Object.class)) {
                return getField(targetClass.getSuperclass(), fieldName);
            } else {
                throw handleException(e);
            }
        } finally {
            if (field != null) {
                field.setAccessible(true);
            }
        }

        return field;
    }

    /**
     * Get the value of the field matching the given name and belonging to target {@link Object}
     *
     * @param target
     *            target {@link Object} whose field we are retrieving the value from
     * @param fieldName
     *            field name
     * @param clazz
     *            class of returning object
     * @param <T>
     *            field type
     * @return field value matching the given name or {@code null}
     */
    static <T> T getFieldValueOrNull(Object target, String fieldName, Class<T> clazz) {
        if (target == null || clazz == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        try {
            Field field = getField(target.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            return clazz.cast(field.get(target));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the {@link Method} with the given signature (name and parameter types) belonging to the provided Java {@link Object}.
     *
     * @param target
     *            target {@link Object}
     * @param methodName
     *            method name
     * @param parameterTypes
     *            method parameter types
     * @return return {@link Method} matching the provided signature or {@code null}
     * @throws BaseException
     *             on error
     */
    private static Method getMethod(Object target, String methodName, Class<?>... parameterTypes) throws BaseException {
        if (target == null || StringUtils.isBlank(methodName)) {
            return null;
        }
        return getMethod(target.getClass(), methodName, parameterTypes);
    }

    /**
     * Get the {@link Method} with the given signature (name and parameter types) belonging to the provided Java {@link Class}.
     *
     * @param targetClass
     *            target {@link Class}
     * @param methodName
     *            method name
     * @param parameterTypes
     *            method parameter types
     * @return the {@link Method} matching the provided signature or {@code null}
     * @throws BaseException
     *             on error
     */
    static Method getMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) throws BaseException {
        if (targetClass == null || StringUtils.isBlank(methodName)) {
            return null;
        }
        try {
            return targetClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            try {
                return targetClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException ignore) {
                // ignore
            }

            if (!targetClass.getSuperclass().equals(Object.class)) {
                return getMethod(targetClass.getSuperclass(), methodName, parameterTypes);
            } else {
                throw handleException(e);
            }
        }
    }

    /**
     * Invoke the method with the provided signature (name and parameter types) on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose method we are invoking
     * @param methodName
     *            method name to invoke
     * @param clazz
     *            class of returning object
     * @param parameters
     *            parameters passed to the method call
     * @param <T>
     *            return value object type
     * @return the value return by the method invocation or {@code null}
     * @throws BaseException
     *             on error
     */
    static <T> T invokeMethod(Object target, String methodName, Class<T> clazz, Object... parameters) throws BaseException {
        if (target == null || clazz == null || StringUtils.isBlank(methodName)) {
            return null;
        }
        try {
            Class<?>[] parameterClasses = new Class[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                parameterClasses[i] = parameters[i].getClass();
            }

            Method method = getMethod(target, methodName, parameterClasses);
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return clazz.cast(method.invoke(target, parameters));
        } catch (InvocationTargetException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the {@code static} {@link Method} with the provided parameters.
     *
     * @param method
     *            target {@code static} {@link Method} to invoke
     * @param clazz
     *            class of returning object
     * @param parameters
     *            parameters passed to the method call
     * @param <T>
     *            return value object type
     * @return the value return by the method invocation or {@code null}
     * @throws BaseException
     *             on error
     */
    static <T> T invokeStaticMethod(Method method, Class<T> clazz, Object... parameters) throws BaseException {
        if (method == null || clazz == null) {
            return null;
        }
        try {
            method.setAccessible(true);
            return clazz.cast(method.invoke(null, parameters));
        } catch (InvocationTargetException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * Handle the {@link NoSuchFieldException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link NoSuchFieldException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(NoSuchFieldException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }

    /**
     * Handle the {@link NoSuchMethodException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link NoSuchMethodException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(NoSuchMethodException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }

    /**
     * Handle the {@link IllegalAccessException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link IllegalAccessException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(IllegalAccessException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }

    /**
     * Handle the {@link InvocationTargetException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link InvocationTargetException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(InvocationTargetException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }
}
