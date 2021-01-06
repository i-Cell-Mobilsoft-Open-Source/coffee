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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import hu.icellmobilsoft.coffee.dto.error.IFaultType;
import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Extension for processing IFaultType implementations. In order to discover an implementation beans.xml must be present in the implementation's
 * module.
 *
 * @author mark.petrenyi
 * @since 1.3.0
 */
public class FaultTypeParserExtension implements Extension {

    private static final int DEFAULT_FAULT_TYPE_PRIORITY = 500;

    private static List<Class<? extends Enum>> faultTypeClasses = new ArrayList<>();
    private static volatile Map<Class<? extends Enum>, Integer> faultTypePriorityMap = new HashMap<>();

    /**
     * Process annotated type.
     *
     * @param <T>
     *            the type parameter
     * @param processAnnotatedType
     *            the process annotated type
     */
    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> processAnnotatedType) {
        AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
        Class<T> javaClass = annotatedType.getJavaClass();
        if (IFaultType.class.isAssignableFrom(javaClass) && javaClass.isEnum()) {
            Logger.getLogger(FaultTypeParserExtension.class).debug("IFaultType implementation found:[{0}], registering as fault type enum",
                    javaClass);
            synchronized (FaultTypeParserExtension.class) {
                faultTypePriorityMap.put((Class<? extends Enum>) javaClass, getPriority(annotatedType));
            }
        }
    }

    /**
     * After type discovery. Feltölti a faultTypeClasses listát priority szerint rendezve
     *
     * @param afterTypeDiscovery
     *            the after type discovery
     */
    void afterTypeDiscovery(@Observes AfterTypeDiscovery afterTypeDiscovery) {
        synchronized (FaultTypeParserExtension.class) {
            List<Class<? extends Enum>> sortedFaultTypes = faultTypePriorityMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).collect(Collectors.toList());
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

    /**
     * Get enums implementing {@link IFaultType}
     *
     * @return the fault type classes
     */
    public static Collection<Class<? extends Enum>> getFaultTypeClasses() {
        return List.copyOf(faultTypeClasses);
    }

}
