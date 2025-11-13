/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.repserv.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.apache.deltaspike.data.api.Repository;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.coffee.module.repserv.action.collect.JpqlCollector;
import hu.icellmobilsoft.coffee.module.repserv.action.collect.RepositoryServiceVisitor;
import hu.icellmobilsoft.coffee.module.repserv.action.config.RepositoryServiceConfig;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.RepositoryMethod;
import hu.icellmobilsoft.coffee.module.repserv.action.write.JavaFileGenerator;
import hu.icellmobilsoft.coffee.module.repserv.action.write.JsonFileGenerator;
import hu.icellmobilsoft.coffee.module.repserv.api.SqlContext;
import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;

/**
 * Annotation processor for {@link RepositoryService} annotation.
 * <p>
 * This processor generates inherited classes for {@link RepositoryService}-annotated classes. The generated classes override all public non-static
 * methods to assign an identifier to a request-scoped {@link SqlContext}.
 * </p>
 *
 * <p>
 * In addition, it generates JSON files for all processed classes, containing metadata and method identifiers required to catalog repository data.
 * </p>
 *
 * <p>
 * This processor is automatically registered using {@link com.google.auto.service.AutoService}.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
@AutoService(Processor.class)
public class RepositoryServiceProcessor extends AbstractProcessor {
    /**
     * Processes annotations handled by this processor.
     * <p>
     * Collects metadata for all classes annotated with {@link RepositoryService}, builds internal representations ({@link ClassData}), and generates
     * Java source files through {@link JavaFileGenerator}.
     * </p>
     *
     * @param annotations
     *            the set of annotation types to process
     * @param roundEnv
     *            the environment for the current processing round
     * @return {@code false} to allow other processors to handle the same annotations
     */
    @SuppressWarnings("java:S1872")
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!processingEnv.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
            return false;
        }

        try {
            RepositoryServiceConfig config = new RepositoryServiceConfig(processingEnv.getOptions());
            List<ClassData> dataList = collectData(config, annotations, roundEnv);

            for (ClassData data : dataList) {
                if (data.getMethodDataList().isEmpty()) {
                    continue;
                }

                try (JavaFileGenerator javaFileGenerator = new JavaFileGenerator(processingEnv, data)) {
                    javaFileGenerator.generate();
                }

                try (JsonFileGenerator jsonFileGenerator = new JsonFileGenerator(config, processingEnv, data)) {
                    jsonFileGenerator.generate();
                }
            }
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Creates a new {@code RepositoryServiceProcessor} instance.
     */
    public RepositoryServiceProcessor() {
        super();
    }

    private List<ClassData> collectData(RepositoryServiceConfig config, Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<RepositoryMethod, String> jpqlByRepositoryMethod = JpqlCollector.collectJpqlMap(annotations, roundEnv);

        return annotations.stream()
                .filter(a -> a.getSimpleName().toString().equals(RepositoryService.class.getSimpleName()))
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(e -> this.createData(config, e, jpqlByRepositoryMethod))
                .toList();
    }

    private ClassData createData(RepositoryServiceConfig config, Element element, Map<RepositoryMethod, String> jpqlByRepositoryMethod) {
        ClassData data = new ClassData();
        RepositoryServiceVisitor visitor = new RepositoryServiceVisitor(config, processingEnv, jpqlByRepositoryMethod);
        visitor.visit(element, data);

        return data;
    }

    /**
     * Returns the set of annotation types supported by this processor.
     *
     * @return a set containing fully qualified annotation names
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Repository.class.getCanonicalName(), RepositoryService.class.getCanonicalName());
    }
}
