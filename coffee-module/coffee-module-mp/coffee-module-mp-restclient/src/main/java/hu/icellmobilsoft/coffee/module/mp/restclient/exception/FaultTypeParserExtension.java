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
package hu.icellmobilsoft.coffee.module.mp.restclient.exception;

import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterTypeDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;

import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.se.api.annotation.FaultTypeCode;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Extension for processing FaultType enums. The built-in {@link CoffeeFaultType} is always registered. Custom enums annotated with the legacy
 * CDI based {@code hu.icellmobilsoft.coffee.cdi.annotation.FaultTypeCode} are discovered if beans.xml is present in the implementation's module,
 * the CDI-free {@link FaultTypeCode} does not make an enum discoverable in annotated bean archives on its own.
 *
 * @author mark.petrenyi
 * @since 1.3.0
 */
public class FaultTypeParserExtension implements Extension {

    private static final int DEFAULT_FAULT_TYPE_PRIORITY = 500;

    @SuppressWarnings("rawtypes")
    private static List<Class<? extends Enum>> faultTypeClasses = new ArrayList<>();
    @SuppressWarnings("rawtypes")
    private static volatile Map<Class<? extends Enum>, Integer> faultTypePriorityMap = new HashMap<>();

    /**
     * Default constructor, constructs a new object.
     */
    public FaultTypeParserExtension() {
        super();
    }

    /**
     * Process annotated type.
     *
     * @param <T>
     *            the type parameter
     * @param processAnnotatedType
     *            the process annotated type
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {
        AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
        Class<T> javaClass = annotatedType.getJavaClass();
        if (isFaultTypeCodePresent(annotatedType, javaClass)
                && !InvocationHandler.class.isAssignableFrom(javaClass) && javaClass.isEnum()
                && !hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType.class.equals(javaClass)) {
            Logger.getLogger(FaultTypeParserExtension.class)
                    .debug("IFaultType implementation found:[{0}], registering as fault type enum", javaClass);
            synchronized (FaultTypeParserExtension.class) {
                faultTypePriorityMap.put((Class<? extends Enum>) javaClass, getPriority(annotatedType));
            }
        }
    }

    /**
     * After type discovery. It fills the `faultTypeClasses` list sorted by priority.
     *
     * @param afterTypeDiscovery
     *            the after type discovery
     */
    @SuppressWarnings("rawtypes")
    void afterTypeDiscovery(@Observes AfterTypeDiscovery afterTypeDiscovery) {
        synchronized (FaultTypeParserExtension.class) {
            // CDI-free @FaultTypeCode does not make the enum discoverable, so the built-in fault type is registered explicitly
            faultTypePriorityMap.putIfAbsent(CoffeeFaultType.class, DEFAULT_FAULT_TYPE_PRIORITY);
            List<Class<? extends Enum>> sortedFaultTypes = faultTypePriorityMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<Class<? extends Enum>, Integer> comparingByValue()
                            .thenComparing(entry -> entry.getKey().getName()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            faultTypeClasses.addAll(sortedFaultTypes);
        }
    }

    private static <T> int getPriority(AnnotatedType<T> annotatedType) {
        int priority = 0;
        if (annotatedType.isAnnotationPresent(Priority.class)) {
            Priority priorityAnnotation = annotatedType.getAnnotation(Priority.class);
            priority = priorityAnnotation.value();
        }
        return priority > 0 ? priority : DEFAULT_FAULT_TYPE_PRIORITY;
    }

    private static <T> boolean isFaultTypeCodePresent(AnnotatedType<T> annotatedType, Class<T> javaClass) {
        return annotatedType.isAnnotationPresent(FaultTypeCode.class)
                || javaClass.isAnnotationPresent(FaultTypeCode.class)
                || annotatedType.isAnnotationPresent(hu.icellmobilsoft.coffee.cdi.annotation.FaultTypeCode.class)
                || javaClass.isAnnotationPresent(hu.icellmobilsoft.coffee.cdi.annotation.FaultTypeCode.class);
    }

    /**
     * Get enums with FaultTypeCode annotations
     *
     * @return the fault type classes
     */
    @SuppressWarnings("rawtypes")
    public static Collection<Class<? extends Enum>> getFaultTypeClasses() {
        return List.copyOf(faultTypeClasses);
    }

}
