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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.coffee.module.configdoc.DynamicConfigTemplate;
import hu.icellmobilsoft.coffee.module.configdoc.data.DocData;
import hu.icellmobilsoft.coffee.module.configdoc.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.configdoc.writer.impl.AsciiDocWriter;

/**
 * Annotation processor for {@link DynamicConfigTemplate}. Creates asciidoc template based on interfaces, classes annotated with
 * {@link DynamicConfigTemplate}, using {@link hu.icellmobilsoft.coffee.module.configdoc.ConfigDoc} to generate template.
 *
 * @author mark.petrenyi
 * @since 1.10.0
 */
@AutoService(Processor.class)
public class DynamicConfigTemplateProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, List<DocData>> docFileMap = collectDocData(annotations, roundEnv);
        if (MapUtils.isNotEmpty(docFileMap)) {
            docFileMap.forEach(this::writeDataListSorted);
        }

        return true;
    }

    private void writeDataListSorted(String fileName, List<DocData> lDataList) {
        if (CollectionUtils.isNotEmpty(lDataList)) {
            List<DocData> sortedList = new ArrayList<>(
                    lDataList.stream().collect(Collectors.toMap(DocData::getKey, Function.identity(), (o1, o2) -> o2)).values());
            sortedList.sort(Comparator.comparing(DocData::getKey));
            writeToFile(sortedList, new AsciiDocWriter(), fileName);
        }
    }

    private void writeToFile(List<DocData> lDataList, IDocWriter docWriter, String fileName) {
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", fileName);

            try (Writer writer = fileObject.openWriter()) {
                docWriter.write(lDataList, writer);
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private Map<String, List<DocData>> collectDocData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ConfigDocVisitor visitor = new ConfigDocVisitor(processingEnv);
        Map<String, List<DocData>> docFileMap = new HashMap<>();

        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (isClassOrInterface(element)) {
                    List<DocData> fieldData = new ArrayList<>();
                    visitor.visit(element, fieldData);
                    // csak templatet hozunk létre, hogy később lehessen használni, nem szükséges paraméterzni a filenevet
                    String templateFileName = DynamicConfigTemplate.TEMPLATE_DIR + element.asType().toString() + DynamicConfigTemplate.TEMPLATE_TYPE;
                    docFileMap.put(templateFileName, fieldData);
                }
            }
        }
        return docFileMap;
    }

    private boolean isClassOrInterface(Element element) {
        return element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(DynamicConfigTemplate.class.getCanonicalName());
    }

}
