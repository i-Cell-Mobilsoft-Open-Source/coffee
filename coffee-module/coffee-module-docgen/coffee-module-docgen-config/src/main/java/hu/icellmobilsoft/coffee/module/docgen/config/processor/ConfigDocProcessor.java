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
package hu.icellmobilsoft.coffee.module.docgen.config.processor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.coffee.module.docgen.common.processor.AbstractDocGenProcessor;
import hu.icellmobilsoft.coffee.module.docgen.common.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.config.ConfigDoc;
import hu.icellmobilsoft.coffee.module.docgen.config.DynamicConfigTemplate;
import hu.icellmobilsoft.coffee.module.docgen.config.config.ConfigDocConfig;
import hu.icellmobilsoft.coffee.module.docgen.config.data.DocData;
import hu.icellmobilsoft.coffee.module.docgen.config.writer.impl.ConfigDocAsciiDocWriter;

/**
 * Annotation processor for {@link ConfigDoc}
 *
 * @author martin.nagy
 * @since 1.9.0
 */
@AutoService(Processor.class)
public class ConfigDocProcessor extends AbstractDocGenProcessor<ConfigDocConfig, DocData> {

    /**
     * Default constructor, constructs a new object.
     */
    public ConfigDocProcessor() {
        super();
    }

    @Override
    protected ConfigDocConfig getConfig() {
        return new ConfigDocConfig(processingEnv.getOptions());
    }

    @Override
    protected ArrayList<DocData> collectDocData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        ConfigDocVisitor visitor = new ConfigDocVisitor(processingEnv);

        ArrayList<DocData> dataList = new ArrayList<>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                // If it has a DynamicConfigTemplate or any of its ancestors do, it goes into the other adoc; skip it here
                if (isWithoutDynamicConfigTemplate(element)) {
                    visitor.visit(element, dataList);
                }
            }
        }
        return dataList;
    }

    @Override
    protected List<DocData> sortData(List<DocData> dataList) {
        dataList = new ArrayList<>(dataList.stream().collect(Collectors.toMap(DocData::getKey, Function.identity(), (o1, o2) -> o2)).values());
        dataList.sort(Comparator.comparing(DocData::getKey));
        return dataList;
    }

    @Override
    protected IDocWriter<DocData> getDocWriter(ConfigDocConfig config) {
        return new ConfigDocAsciiDocWriter(config);
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
