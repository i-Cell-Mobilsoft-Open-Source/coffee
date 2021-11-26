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
package hu.icellmobilsoft.coffee.module.redis.annotation;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;


/**
 * Annotation processor for validating ConsumerPool, ProducerPool use. It prohibits using both for the same target. <br>
 *
 * @author peter.kovacs
 * @since 1.8.0
 */
@SupportedAnnotationTypes({"hu.icellmobilsoft.coffee.module.redis.annotation.ConsumerPool", "hu.icellmobilsoft.coffee.module.redis.annotation.ProducerPool"})
public class StreamAnnotationProcessor extends AbstractProcessor {

    private Messager messager;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(ConsumerPool.class)) {
            Annotation producerAnnotation = element.getAnnotation(ProducerPool.class);
            if (producerAnnotation != null) {

                String msg = MessageFormat.format("Multiple Pool types are defined for the [{0}]!", element.getSimpleName());
                messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(ProducerPool.class)) {
            Annotation consumerAnnotation = element.getAnnotation(ConsumerPool.class);
            if (consumerAnnotation != null) {
                String msg = MessageFormat.format("Multiple Pool types are defined for the [{0}]!", element.getSimpleName());
                messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
