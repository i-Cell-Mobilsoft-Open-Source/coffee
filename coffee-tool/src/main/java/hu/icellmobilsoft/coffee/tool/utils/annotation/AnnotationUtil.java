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
package hu.icellmobilsoft.coffee.tool.utils.annotation;

import java.lang.annotation.Annotation;
import java.util.Optional;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Annotációkat kezelő gyűjtő
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class AnnotationUtil {

    /**
     * Finds the {@code Annotation} instance of annotationClazz, including proxied class
     *
     * @see <a href="https://developer.jboss.org/message/964608#964608">https://developer.jboss.org/message/964608#964608</a>
     * @param <A>
     *            type of returned {@link Annotation}
     * @param clazz
     *            class on which the desired {@code Annotation} is searched
     * @param annotationClass
     *            class of returned {@code Annotation}
     * @return {@code Annotation} instance
     */
    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationClass) {
        if (clazz == null) {
            return null;
        }
        final A annotation = clazz.getAnnotation(annotationClass);

        if (annotation == null && (clazz.isSynthetic())) {
            return getAnnotation(clazz.getSuperclass(), annotationClass);
        } else {
            return annotation;
        }
    }

    /**
     * Finds the annotation instance of annotationClazz on the given InjectionPoint.
     *
     * @param <A>
     *            type of returned {@code Annotation}
     * @param injectionPoint
     *            injection point on which the desired {@code Annotation} is searched
     * @param annotationClazz
     *            class of returned {@code Annotation}
     * @return {@code Optional} instance of {@code annotationClazz}, can be empty
     */
    public static <A extends Annotation> Optional<A> getAnnotation(InjectionPoint injectionPoint, Class<A> annotationClazz) {
        if (injectionPoint == null || annotationClazz == null) {
            return Optional.empty();
        }
        if (injectionPoint.getAnnotated() != null && injectionPoint.getAnnotated().isAnnotationPresent(annotationClazz)) {
            return Optional.ofNullable(injectionPoint.getAnnotated().getAnnotation(annotationClazz));
        } else if (injectionPoint.getQualifiers() != null) {
            for (Annotation qualifier : injectionPoint.getQualifiers()) {
                if (qualifier != null && annotationClazz.isAssignableFrom(qualifier.getClass())) {
                    return Optional.of((A) qualifier);
                }
            }
        }
        return Optional.empty();
    }
}
