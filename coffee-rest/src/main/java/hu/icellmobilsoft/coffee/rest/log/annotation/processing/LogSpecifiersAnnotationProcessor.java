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
package hu.icellmobilsoft.coffee.rest.log.annotation.processing;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifier;
import hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifiers;
import hu.icellmobilsoft.coffee.rest.log.annotation.enumeration.LogSpecifierTarget;

/**
 * Annotation processor for validating LogSpecifiers use. It prohibits using the LogSpecifier annotation multiple times for the same target. <br>
 * ie.:<br>
 * Invalid:<br>
 *
 * <pre>
 * &#64;LogSpecifier(target = LogSpecifierTarget.ALL, maxEntityLogSize = 0) // defines maxEntityLogSize = 0 for request and response
 * &#64;LogSpecifier(target = LogSpecifierTarget.REQUEST, maxEntityLogSize = 10) // defines maxEntityLogSize = 10 for request ExampleRequest
 * postExampleRequest(ExampleRequest exampleRequest) throws InterfaceBaseException;
 * </pre>
 *
 * @author mark.petrenyi
 * @since 1.0.0
 */
@SupportedAnnotationTypes("hu.icellmobilsoft.coffee.rest.log.annotation.LogSpecifiers")
public class LogSpecifiersAnnotationProcessor extends AbstractProcessor {

    private Messager messager;

    /**
     * Default constructor, constructs a new object.
     */
    public LogSpecifiersAnnotationProcessor() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    /** {@inheritDoc} */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(LogSpecifiers.class)) {
            Map<LogSpecifierTarget, Set<LogSpecifier>> logSpecifiersByTarget = new HashMap<>();
            LogSpecifiers logSpecifiers = element.getAnnotation(LogSpecifiers.class);
            if (logSpecifiers != null) {
                for (LogSpecifier logSpecifier : logSpecifiers.value()) {
                    for (LogSpecifierTarget logSpecifierTarget : logSpecifier.target()) {
                        Set<LogSpecifier> logSpecifiersForTarget = logSpecifiersByTarget.getOrDefault(logSpecifierTarget, new HashSet<>());
                        logSpecifiersForTarget.add(logSpecifier);
                        logSpecifiersByTarget.put(logSpecifierTarget, logSpecifiersForTarget);
                    }
                }
            }
            for (Map.Entry<LogSpecifierTarget, Set<LogSpecifier>> entry : logSpecifiersByTarget.entrySet()) {
                if (entry.getValue() != null && entry.getValue().size() > 1) {
                    String msg = MessageFormat.format("Multiple LogSpecifiers are defined for the [{0}] of [{1}]! Conflicting LogSpecifiers:[{2}]",
                            entry.getKey(), element.getSimpleName(), entry.getValue());
                    messager.printMessage(Diagnostic.Kind.ERROR, msg, element);
                }
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
