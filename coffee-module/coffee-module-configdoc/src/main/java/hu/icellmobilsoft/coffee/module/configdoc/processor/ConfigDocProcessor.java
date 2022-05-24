/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.configdoc.processor;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.coffee.module.configdoc.ConfigDoc;
import hu.icellmobilsoft.coffee.module.configdoc.DynamicConfigTemplate;
import hu.icellmobilsoft.coffee.module.configdoc.config.ConfigDocConfig;
import hu.icellmobilsoft.coffee.module.configdoc.data.DocData;
import hu.icellmobilsoft.coffee.module.configdoc.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.configdoc.writer.impl.AsciiDocWriter;

/**
 * Annotation processor for {@link ConfigDoc}
 *
 * @author martin.nagy
 * @since 1.9.0
 */
@AutoService(Processor.class)
public class ConfigDocProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ConfigDocConfig config = new ConfigDocConfig(processingEnv.getOptions());

        List<DocData> dataList = collectDocData(annotations, roundEnv);

        if (!dataList.isEmpty()) {
            dataList = new ArrayList<>(dataList.stream().collect(Collectors.toMap(DocData::getKey, Function.identity(), (o1, o2) -> o2)).values());
            dataList.sort(Comparator.comparing(DocData::getKey));

            writeToFile(dataList, new AsciiDocWriter(config), config);
        }

        return true;
    }

    private void writeToFile(List<DocData> dataList, IDocWriter<DocData> docWriter, ConfigDocConfig config) {
        try (Writer writer = createWriter(config)) {
            docWriter.write(dataList, writer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private Writer createWriter(ConfigDocConfig config) throws IOException {
        Path path = Paths.get(config.getOutputDir(), config.getOutputFileName());
        return config.isOutputToClassPath() ? processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path.toString()).openWriter()
                : Files.newBufferedWriter(path);
    }

    private ArrayList<DocData> collectDocData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ConfigDocVisitor visitor = new ConfigDocVisitor(processingEnv);

        ArrayList<DocData> dataList = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Ha van rajta vagy valamelyik ősén DynamicConfigTemplate, akkor az másik adocba kerül, itt skippeljük
                if (isWithoutDynamicConfigTemplate(element)) {
                    visitor.visit(element, dataList);
                }
            }
        }
        return dataList;
    }

    private boolean isWithoutDynamicConfigTemplate(Element element) {
        return element == null
                || (element.getAnnotation(DynamicConfigTemplate.class) == null && isWithoutDynamicConfigTemplate(element.getEnclosingElement()));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(ConfigDoc.class.getCanonicalName());
    }
}
