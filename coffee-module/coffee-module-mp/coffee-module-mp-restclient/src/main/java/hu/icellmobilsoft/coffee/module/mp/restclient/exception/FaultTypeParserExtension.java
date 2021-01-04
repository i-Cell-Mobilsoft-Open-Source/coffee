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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
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

    private static volatile Set<Class<? extends Enum>> faultTypeClasses = Collections.synchronizedSet(new LinkedHashSet<>());

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
            faultTypeClasses.add((Class<? extends Enum>) javaClass);
        }
    }

    /**
     * Get enums implementing {@link IFaultType}
     *
     * @return the fault type classes
     */
    public static Collection<Class<? extends Enum>> getFaultTypeClasses() {
        return Set.copyOf(faultTypeClasses);
    }

}
