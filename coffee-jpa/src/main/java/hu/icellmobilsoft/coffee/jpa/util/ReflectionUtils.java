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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;

/**
 * <code>ReflectionUtils</code> - Reflection utilities holder from hypersistence-utils-60
 *
 * @author tamas.cserhati
 * @since 2.1.0
 */
public final class ReflectionUtils {

    private static final String GETTER_PREFIX = "get";

    private static final String SETTER_PREFIX = "set";

    /**
     * Prevent any instantiation.
     */
    private ReflectionUtils() {
        throw new UnsupportedOperationException("The " + getClass() + " is not instantiable!");
    }

    /**
     * Instantiate a new {@link Object} of the provided type.
     *
     * @param <T>
     *            Class type
     * @param className
     *            The fully-qualified Java Class name of the {@link Object} to instantiate
     * @return new Java {@link Object} of the provided type
     * @throws BaseException
     *             on error
     */
    public static <T> T newInstance(String className) throws BaseException {
        Class<?> clazz = getClass(className);
        return newInstance(clazz);
    }

    /**
     * Instantiate a new {@link Object} of the provided type.
     *
     * @param clazz
     *            The Java {@link Class} of the {@link Object} to instantiate
     * @param <T>
     *            Class type
     * @return new Java {@link Object} of the provided type
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) throws BaseException {
        try {
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException e) {
            throw handleException(e);
        } catch (InstantiationException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        } catch (NoSuchMethodException e) {
            throw handleException(e);
        } catch (SecurityException e) {
            throw handleException(e);
        }
    }

    /**
     * Instantiate a new {@link Object} of the provided type.
     *
     * @param clazz
     *            The Java {@link Class} of the {@link Object} to instantiate
     * @param args
     *            The arguments that need to be passed to the constructor
     * @param argsTypes
     *            The argument types that need to be passed to the constructor
     * @param <T>
     *            Class type
     * @return new Java {@link Object} of the provided type
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class clazz, Object[] args, Class<?>[] argsTypes) throws BaseException {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(argsTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        } catch (NoSuchMethodException e) {
            throw handleException(e);
        } catch (InvocationTargetException e) {
            throw handleException(e);
        }
    }

    /**
     * Get the {@link Field} with the given name belonging to the provided Java {@link Class}.
     *
     * @param targetClass
     *            the provided Java {@link Class} the field belongs to
     * @param fieldName
     *            the {@link Field} name
     * @return the {@link Field} matching the given name
     * @throws BaseException
     *             on error
     */
    public static Field getField(Class<?> targetClass, String fieldName) throws BaseException {
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
     * Get the {@link Field} with the given name belonging to the provided Java {@link Class} or {@code null} if no {@link Field} was found.
     *
     * @param targetClass
     *            the provided Java {@link Class} the field belongs to
     * @param fieldName
     *            the {@link Field} name
     * @return the {@link Field} matching the given name or {@code null}
     */
    public static Field getFieldOrNull(Class<?> targetClass, String fieldName) {
        try {
            return getField(targetClass, fieldName);
        } catch (BaseException e) {
            return null;
        }
    }

    /**
     * Get the the value of {@link Field} with the given name belonging to the provided Java {@link Class} or {@code null} if no {@link Field} was
     * found.
     *
     * @param targetClass
     *            the provided Java {@link Class} the field belongs to
     * @param fieldName
     *            the {@link Field} name
     * @param <T>
     *            Class type
     * @return the value of {@link Field} matching the given name or {@code null}
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueOrNull(Class<?> targetClass, String fieldName) throws BaseException {
        try {
            Field field = getField(targetClass, fieldName);
            return (T) field.get(null);
        } catch (BaseException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Get the value of the field matching the given name and belonging to target {@link Object}.
     *
     * @param target
     *            target {@link Object} whose field we are retrieving the value from
     * @param fieldName
     *            field name
     * @param <T>
     *            field type
     * @return field value
     * @throws BaseException
     *             on error
     */
    public static <T> T getFieldValue(Object target, String fieldName) throws BaseException {
        try {
            Field field = getField(target.getClass(), fieldName);
            @SuppressWarnings("unchecked")
            T returnValue = (T) field.get(target);
            return returnValue;
        } catch (IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * Get the value of the field matching the given name and belonging to target {@link Object} or {@code null} if no {@link Field} was found..
     *
     * @param target
     *            target {@link Object} whose field we are retrieving the value from
     * @param fieldName
     *            field name
     * @param <T>
     *            field type
     * @return field value matching the given name or {@code null}
     */
    public static <T> T getFieldValueOrNull(Object target, String fieldName) {
        try {
            Field field = getField(target.getClass(), fieldName);
            @SuppressWarnings("unchecked")
            T returnValue = (T) field.get(target);
            return returnValue;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set the value of the field matching the given name and belonging to target {@link Object}.
     *
     * @param target
     *            target object
     * @param fieldName
     *            field name
     * @param value
     *            field value
     * @throws BaseException
     *             on error
     */
    public static void setFieldValue(Object target, String fieldName, Object value) throws BaseException {
        try {
            Field field = getField(target.getClass(), fieldName);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw handleException(e);
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
     * @return return {@link Method} matching the provided signature
     * @throws BaseException
     *             on error
     */
    public static Method getMethod(Object target, String methodName, Class<?>... parameterTypes) throws BaseException {
        return getMethod(target.getClass(), methodName, parameterTypes);
    }

    /**
     * Get the {@link Method} with the given signature (name and parameter types) belonging to the provided Java {@link Object} or {@code null} if no
     * {@link Method} was found.
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
    public static Method getMethodOrNull(Object target, String methodName, Class<?>... parameterTypes) throws BaseException {
        try {
            return getMethod(target.getClass(), methodName, parameterTypes);
        } catch (RuntimeException e) {
            return null;
        }
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
     * @return the {@link Method} matching the provided signature
     * @throws BaseException
     *             on error
     */
    public static Method getMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) throws BaseException {
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
     * Get the {@link Method} with the given signature (name and parameter types) belonging to the provided Java {@link Object} or {@code null} if no
     * {@link Method} was found.
     *
     * @param targetClass
     *            target {@link Class}
     * @param methodName
     *            method name
     * @param parameterTypes
     *            method parameter types
     * @return return {@link Method} matching the provided signature or {@code null}
     */
    public static Method getMethodOrNull(Class<?> targetClass, String methodName, Class<?>... parameterTypes) {
        try {
            return getMethod(targetClass, methodName, parameterTypes);
        } catch (BaseException | RuntimeException e) {
            return null;
        }
    }

    /**
     * Get the {@link Method} with the given signature (name and parameter types) belonging to the provided Java {@link Class}, excluding inherited
     * ones, or {@code null} if no {@link Method} was found.
     *
     * @param targetClass
     *            target {@link Class}
     * @param methodName
     *            method name
     * @param parameterTypes
     *            method parameter types
     * @return return {@link Method} matching the provided signature or {@code null}
     */
    public static Method getDeclaredMethodOrNull(Class<?> targetClass, String methodName, Class<?>... parameterTypes) {
        try {
            return targetClass.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Check if the provided Java {@link Class} contains a method matching the given signature (name and parameter types).
     *
     * @param targetClass
     *            target {@link Class}
     * @param methodName
     *            method name
     * @param parameterTypes
     *            method parameter types
     * @return the provided Java {@link Class} contains a method with the given signature
     */
    public static boolean hasMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) {
        try {
            targetClass.getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Get the property setter {@link Method} with the given signature (name and parameter types) belonging to the provided Java {@link Object}.
     *
     * @param target
     *            target {@link Object}
     * @param propertyName
     *            property name
     * @param parameterType
     *            setter property type
     * @return the setter {@link Method} matching the provided signature
     * @throws BaseException
     *             on error
     */
    public static Method getSetter(Object target, String propertyName, Class<?> parameterType) throws BaseException {
        String setterMethodName = SETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method setter = getMethod(target, setterMethodName, parameterType);
        setter.setAccessible(true);
        return setter;
    }

    /**
     * Get the property getter {@link Method} with the given name belonging to the provided Java {@link Object}.
     *
     * @param target
     *            target {@link Object}
     * @param propertyName
     *            property name
     * @return the getter {@link Method} matching the provided name
     * @throws BaseException
     *             on error
     */
    public static Method getGetter(Object target, String propertyName) throws BaseException {
        String getterMethodName = GETTER_PREFIX + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        Method getter = getMethod(target, getterMethodName);
        getter.setAccessible(true);
        return getter;
    }

    /**
     * Invoke the provided {@link Method} on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose method we are invoking
     * @param method
     *            method to invoke
     * @param parameters
     *            parameters passed to the method call
     * @param <T>
     *            return value object type
     * @return the value return by the {@link Method} invocation
     * @throws BaseException
     *             on error
     */
    public static <T> T invokeMethod(Object target, Method method, Object... parameters) throws BaseException {
        try {
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(target, parameters);
            return returnValue;
        } catch (InvocationTargetException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the method with the provided signature (name and parameter types) on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose method we are invoking
     * @param methodName
     *            method name to invoke
     * @param parameters
     *            parameters passed to the method call
     * @param <T>
     *            return value object type
     * @return the value return by the method invocation
     * @throws BaseException
     *             on error
     */
    public static <T> T invokeMethod(Object target, String methodName, Object... parameters) throws BaseException {
        try {
            Class<?>[] parameterClasses = new Class[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                parameterClasses[i] = parameters[i].getClass();
            }

            Method method = getMethod(target, methodName, parameterClasses);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(target, parameters);
            return returnValue;
        } catch (InvocationTargetException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the property getter with the provided name on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose property getter we are invoking
     * @param propertyName
     *            property name whose getter we are invoking
     * @param <T>
     *            return value object type
     * @return the value return by the getter invocation
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeGetter(Object target, String propertyName) throws BaseException {
        Method setter = getGetter(target, propertyName);
        try {
            return (T) setter.invoke(target);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        } catch (InvocationTargetException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the property setter with the provided signature (name and parameter types) on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose property setter we are invoking
     * @param propertyName
     *            property name whose setter we are invoking
     * @param parameter
     *            parameter passed to the setter call
     * @throws BaseException
     *             on error
     */
    public static void invokeSetter(Object target, String propertyName, Object parameter) throws BaseException {
        Method setter = getSetter(target, propertyName, parameter.getClass());
        try {
            setter.invoke(target, parameter);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        } catch (InvocationTargetException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the {@link boolean} property setter with the provided name on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose property setter we are invoking
     * @param propertyName
     *            property name whose setter we are invoking
     * @param parameter
     *            {@link boolean} parameter passed to the setter call
     * @throws BaseException
     *             on error
     */
    public static void invokeSetter(Object target, String propertyName, boolean parameter) throws BaseException {
        Method setter = getSetter(target, propertyName, boolean.class);
        try {
            setter.invoke(target, parameter);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        } catch (InvocationTargetException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the {@link int} property setter with the provided name on the given Java {@link Object}.
     *
     * @param target
     *            target {@link Object} whose property setter we are invoking
     * @param propertyName
     *            property name whose setter we are invoking
     * @param parameter
     *            {@link int} parameter passed to the setter call
     * @throws BaseException
     *             on error
     */
    public static void invokeSetter(Object target, String propertyName, int parameter) throws BaseException {
        Method setter = getSetter(target, propertyName, int.class);
        try {
            setter.invoke(target, parameter);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        } catch (InvocationTargetException e) {
            throw handleException(e);
        }
    }

    /**
     * Invoke the {@code static} {@link Method} with the provided parameters.
     *
     * @param method
     *            target {@code static} {@link Method} to invoke
     * @param parameters
     *            parameters passed to the method call
     * @param <T>
     *            return value object type
     * @return the value return by the method invocation
     * @throws BaseException
     *             on error
     */
    public static <T> T invokeStaticMethod(Method method, Object... parameters) throws BaseException {
        try {
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T returnValue = (T) method.invoke(null, parameters);
            return returnValue;
        } catch (InvocationTargetException e) {
            throw handleException(e);
        } catch (IllegalAccessException e) {
            throw handleException(e);
        }
    }

    /**
     * Get the Java {@link Class} with the given fully-qualified name.
     *
     * @param className
     *            the Java {@link Class} name to be retrieved
     * @param <T>
     *            {@link Class} type
     * @return the Java {@link Class} object
     * @throws BaseException
     *             on error
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String className) throws BaseException {
        try {
            return (Class<T>) Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw handleException(e);
        }
    }

    /**
     * Get the Java {@link Class} with the given fully-qualified name or or {@code null} if no {@link Class} was found matching the provided name.
     *
     * @param className
     *            the Java {@link Class} name to be retrieved
     * @param <T>
     *            {@link Class} type
     * @return the Java {@link Class} object or {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassOrNull(String className) {
        try {
            return (Class<T>) getClass(className);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the Java Wrapper {@link Class} associated to the given primitive type.
     *
     * @param clazz
     *            primitive class
     * @return the Java Wrapper {@link Class}
     */
    public static Class<?> getWrapperClass(Class<?> clazz) {
        if (!clazz.isPrimitive())
            return clazz;

        if (clazz == Integer.TYPE)
            return Integer.class;
        if (clazz == Long.TYPE)
            return Long.class;
        if (clazz == Boolean.TYPE)
            return Boolean.class;
        if (clazz == Byte.TYPE)
            return Byte.class;
        if (clazz == Character.TYPE)
            return Character.class;
        if (clazz == Float.TYPE)
            return Float.class;
        if (clazz == Double.TYPE)
            return Double.class;
        if (clazz == Short.TYPE)
            return Short.class;
        if (clazz == Void.TYPE)
            return Void.class;

        return clazz;
    }

    /**
     * Get the first super Class matching the provided package name.
     *
     * @param clazz
     *            Java class
     * @param packageName
     *            package name
     * @param <T>
     *            Class generic type
     * @return the first super Class matching the provided package name or {@code null}.
     */
    public static <T> Class<?> getFirstSuperClassFromPackage(Class<?> clazz, String packageName) {
        if (clazz.getPackage().getName().equals(packageName)) {
            return clazz;
        } else {
            Class<?> superClass = clazz.getSuperclass();
            return (superClass == null || superClass.equals(Object.class)) ? null : (Class<?>) getFirstSuperClassFromPackage(superClass, packageName);
        }
    }

    /**
     * Get the generic types of a given Class.
     *
     * @param parameterizedType
     *            parameterized Type
     * @return generic types for the given Class.
     */
    public static Set<Class<?>> getGenericTypes(ParameterizedType parameterizedType) {
        Set<Class<?>> genericTypes = new LinkedHashSet<>();
        for (Type genericType : parameterizedType.getActualTypeArguments()) {
            if (genericType instanceof Class) {
                genericTypes.add((Class<?>) genericType);
            }
        }
        return genericTypes;
    }

    /**
     * Get the {@link Member} with the given name belonging to the provided Java {@link Class} or {@code null} if no {@link Member} was found.
     *
     * @param targetClass
     *            the provided Java {@link Class} the field or method belongs to
     * @param memberName
     *            the {@link Field} or {@link Method} name
     * @return the {@link Field} or {@link Method} matching the given name or {@code null}
     */
    public static Member getMemberOrNull(Class<?> targetClass, String memberName) {
        Field field = getFieldOrNull(targetClass, memberName);
        return (field != null) ? field : getMethodOrNull(targetClass, memberName);
    }

    /**
     * Get the generic {@link Type} of the {@link Member} with the given name belonging to the provided Java {@link Class} or {@code null} if no
     * {@link Member} was found.
     *
     * @param targetClass
     *            the provided Java {@link Class} the field or method belongs to
     * @param memberName
     *            the {@link Field} or {@link Method} name
     * @return the generic {@link Type} of the {@link Field} or {@link Method} matching the given name or {@code null}
     */
    public static Type getMemberGenericTypeOrNull(Class<?> targetClass, String memberName) {
        Field field = getFieldOrNull(targetClass, memberName);
        return (field != null) ? field.getGenericType() : getMethodOrNull(targetClass, memberName).getGenericReturnType();
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
     * Handle the {@link SecurityException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link SecurityException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(SecurityException e) {
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

    /**
     * Handle the {@link ClassNotFoundException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link ClassNotFoundException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(ClassNotFoundException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }

    /**
     * Handle the {@link InstantiationException} by rethrowing it as an {@link BaseException}.
     *
     * @param e
     *            the original {@link InstantiationException}
     * @return the {@link BaseException} wrapping exception
     */
    private static BaseException handleException(InstantiationException e) {
        return new TechnicalException(CoffeeFaultType.OPERATION_FAILED, e.getLocalizedMessage(), e);
    }

    /**
     * Get the Member type.
     * 
     * @param member
     *            member
     * @return member type
     */
    public static Class<?> getMemberType(Member member) {
        if (Field.class.isInstance(member)) {
            return ((Field) member).getType();
        } else if (Method.class.isInstance(member)) {
            return ((Method) member).getReturnType();
        }
        throw new UnsupportedOperationException(String.format("The [%s] member is neither a Field or a Method!", member));
    }
}
