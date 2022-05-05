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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementKindVisitor9;

import hu.icellmobilsoft.coffee.module.configdoc.DynamicConfigDocs;
import hu.icellmobilsoft.coffee.module.configdoc.data.DynamicDocData;

/**
 * Collects the {@link DynamicDocData} from the {@link DynamicConfigDocs} annotated classes and fields.
 *
 * @author mark.petrenyi
 * @since 1.10.0
 */
public class DynamicConfigDocsVisitor extends ElementKindVisitor9<DynamicDocData, Void> {
    private final ProcessingEnvironment processingEnv;

    /**
     * Creates a visitor instance with the {@code processingEnv}
     * 
     * @param processingEnv
     *            annotation processing environment holder
     */
    public DynamicConfigDocsVisitor(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    @Override
    protected DynamicDocData defaultAction(Element e, Void unused) {
        return processElement(e);
    }

    private DynamicDocData processElement(Element element) {
        DynamicConfigDocs annotation = element.getAnnotation(DynamicConfigDocs.class);
        if (annotation == null) {
            return null;
        }
        DynamicDocData headerResult = new DynamicDocData();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            Element annotationMirrorElement = annotationMirror.getAnnotationType().asElement();
            DynamicDocData parentHeader = visit(annotationMirrorElement);
            headerResult.merge(parentHeader);
        }
        DynamicDocData annotationHeader = toHeader(annotation);
        headerResult.merge(annotationHeader);
        return headerResult;
    }

    private DynamicDocData toHeader(DynamicConfigDocs annotation) {
        DynamicDocData header = new DynamicDocData();
        header.setTitle(annotation.title());
        header.setDescription(annotation.description());
        header.setTemplateVariables(annotation.templateVariables());
        try {
            // compile time vagyunk így ez lehet elhasal,
            // de az exceptionből megkapjuk a template classnak megfelelő TypeMirror-t
            header.setTemplateClassName(annotation.template().getCanonicalName());
        } catch (MirroredTypeException e) {
            TypeMirror typeMirror = e.getTypeMirror();
            header.setTemplateClassName(typeMirror.toString());
        }
        return header;
    }
}
