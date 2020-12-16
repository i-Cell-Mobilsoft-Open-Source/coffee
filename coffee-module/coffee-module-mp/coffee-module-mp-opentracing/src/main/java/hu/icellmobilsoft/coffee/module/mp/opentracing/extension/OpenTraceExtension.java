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
package hu.icellmobilsoft.coffee.module.mp.opentracing.extension;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.util.metadata.AnnotationInstanceProvider;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traceable;
import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.utils.annotation.AnnotationUtil;

/**
 * OpenTraceExtension activator class
 * 
 * @author czenczl
 * @since 1.3.0
 */
public class OpenTraceExtension implements javax.enterprise.inject.spi.Extension {

    private static final Logger LOGGER = Logger.getLogger(OpenTraceExtension.class);

    public <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat) {
        // if the class is traceable add Traced interceptor binding
        Traceable traceable = AnnotationUtil.getAnnotation(pat.getAnnotatedType().getJavaClass(), Traceable.class);
        if (traceable != null) {
            LOGGER.info("Found traceable class: " + pat.getAnnotatedType());

            Traced traced = AnnotationInstanceProvider.of(Traced.class);
            AnnotatedTypeBuilder<T> builder = new AnnotatedTypeBuilder<T>().readFromType(pat.getAnnotatedType());
            builder.addToClass(traced);
            pat.setAnnotatedType(builder.create());

        }
    }

}
