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

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final Pattern sinceTagPattern = Pattern.compile("\n\\s*@since ([^\n]+)", Pattern.MULTILINE);
    private static final String KEY_DELIMITER = ".";
    private static final int DEFAULT_TITLE_HEADING_LEVEL = 3;
    private final ProcessingEnvironment processingEnv;


    /**
     * Creates a visitor instance with the {@code processingEnv}
     * 
     * @param processingEnv
     *            annotation processing environment holder
     */
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
        Optional<String> descriptionOpt = configDocAnnotation.map(ConfigDoc::description).filter(StringUtils::isNotBlank);
        String defaultValue = configDocAnnotation.map(ConfigDoc::defaultValue).filter(StringUtils::isNotBlank).orElse(null);
        String since = configDocAnnotation.map(ConfigDoc::since).filter(StringUtils::isNotBlank).orElse(null);
        boolean isStartupParam = configDocAnnotation.map(ConfigDoc::isStartupParam).orElse(false);
        boolean isRuntimeOverridable = configDocAnnotation.map(ConfigDoc::isRuntimeOverridable).orElse(false);
        String title = configDocAnnotation.map(ConfigDoc::title).filter(StringUtils::isNotBlank).orElse(null);
        int titleHeadingLevel = configDocAnnotation.map(ConfigDoc::titleHeadingLevel).orElse(DEFAULT_TITLE_HEADING_LEVEL);
        if (descriptionOpt.isPresent()) {
            dataList.add(new DocData(key, source, descriptionOpt.get(), defaultValue, since, isStartupParam, isRuntimeOverridable, title, titleHeadingLevel));
            return;
        }

        dataList.add(createDataFromJavaDoc(element, key, source, defaultValue, since, isStartupParam, isRuntimeOverridable, title, titleHeadingLevel));
    }

    private DocData createDataFromJavaDoc(VariableElement element, String key, String source, String defaultValue, String since,
                                          boolean isStartupParam, boolean isRuntimeOverridable, String title, int titleHeadingLevel) {
        String description = getJavaDoc(element);
        if (null == description) {
            String msg = MessageFormat.format("No java API doc attached to field [{0}] in this file: [{1}]", element, source);
            throw new IllegalArgumentException(msg);
        }

        Matcher matcher = sinceTagPattern.matcher(description);
        if (matcher.find()) {
            if (since == null) {
                since = matcher.group(1);
            }
            description = matcher.replaceAll("");
        }
        return new DocData(key, source, description.trim(), defaultValue, since, isStartupParam, isRuntimeOverridable, title, titleHeadingLevel);
    }

    private String getJavaDoc(VariableElement element) {
        return processingEnv.getElementUtils().getDocComment(element);
    }

    private boolean isConfigKey(Object value) {
        return value instanceof String;
    }
}
