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

import java.util.List;
import java.util.Optional;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementKindVisitor9;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.module.configdoc.ConfigDoc;
import hu.icellmobilsoft.coffee.module.configdoc.data.DocData;

/**
 * Collects the {@link DocData} from the {@link ConfigDoc} annotated classes and fields.
 *
 * @author martin.nagy
 * @since 1.9.0
 */
public class ConfigDocVisitor extends ElementKindVisitor9<Void, List<DocData>> {
    private final ProcessingEnvironment processingEnv;

    public ConfigDocVisitor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    public Void visitVariableAsField(VariableElement e, List<DocData> dataList) {
        processField(e, dataList);
        return super.visitVariableAsField(e, dataList);
    }

    @Override
    public Void visitType(TypeElement e, List<DocData> dataList) {
        for (Element enclosedElement : e.getEnclosedElements()) {
            visit(enclosedElement, dataList);
        }
        return super.visitType(e, dataList);
    }

    private void processField(VariableElement element, List<DocData> dataList) {
        Optional<ConfigDoc> configDocAnnotation = Optional.ofNullable(element.getAnnotation(ConfigDoc.class));
        if (configDocAnnotation.isPresent() && configDocAnnotation.get().exclude()) {
            return;
        }

        Object value = element.getConstantValue();
        if (!isConfigKey(value)) {
            return;
        }

        String key = (String) value;
        String source = element.getEnclosingElement().asType().toString();
        String description = configDocAnnotation.map(ConfigDoc::description).filter(StringUtils::isNotBlank).orElseGet(() -> getJavaDoc(element));
        String defaultValue = configDocAnnotation.map(ConfigDoc::defaultValue).orElse(null);

        dataList.add(new DocData(key, source, description, defaultValue));
    }

    private String getJavaDoc(VariableElement element) {
        return processingEnv.getElementUtils().getDocComment(element);
    }

    private boolean isConfigKey(Object value) {
        return value instanceof String;
    }
}
