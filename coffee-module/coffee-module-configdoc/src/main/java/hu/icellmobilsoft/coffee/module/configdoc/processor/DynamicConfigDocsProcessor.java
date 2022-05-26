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
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.auto.service.AutoService;

import hu.icellmobilsoft.coffee.module.configdoc.DynamicConfigDocs;
import hu.icellmobilsoft.coffee.module.configdoc.DynamicConfigTemplate;
import hu.icellmobilsoft.coffee.module.configdoc.config.ConfigDocConfig;
import hu.icellmobilsoft.coffee.module.configdoc.data.DynamicDocData;
import hu.icellmobilsoft.coffee.module.configdoc.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.configdoc.writer.impl.DynamicAsciiDocWriter;

/**
 * Annotation processor for {@link DynamicConfigDocs}, creates documentation with using the {@link DynamicConfigDocs#templateVariables()} and the
 * template adoc generated from to {@link DynamicConfigDocs#template()}}.
 *
 * @author mark.petrenyi
 * @since 1.10.0
 */
@AutoService(Processor.class)
public class DynamicConfigDocsProcessor extends AbstractProcessor {

    private Set<DynamicDocData> docHeaders = new HashSet<>();
    private Map<String, String> templateMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // @DynamicConfigDocs annotációk feldolgozása
        docHeaders.addAll(processDynamicConfigDocsAnnotations(annotations, roundEnv));

        // hivatkozott templatek felolvasása
        for (DynamicDocData header : docHeaders) {
            templateMap.computeIfAbsent(header.getTemplateClassName(), this::readTemplate);
        }
        // utolsó körben írjuk ki a fájlt, hátha egyszerre fordul a template és a dinamikus doksi ezért lehet, hogy adott körben nem sikerül
        // felolvasni
        // a template-et, de utolsóra meg kell legyen mindegyik
        if (roundEnv.processingOver()) {
            // összeszedjük a template-eket, rendezünk
            List<DynamicDocData> dataToWrite = collectDataToWrite(docHeaders, templateMap);

            ConfigDocConfig config;
            try {
                config = new ConfigDocConfig(processingEnv.getOptions());
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                return true;
            }
            writeConfigDocFile(dataToWrite, new DynamicAsciiDocWriter(), config);
        }
        return true;
    }

    private List<DynamicDocData> collectDataToWrite(Collection<DynamicDocData> dataCollection, Map<String, String> templates) {
        List<DynamicDocData> dataToWrite = new ArrayList<>();
        for (DynamicDocData dynamicDocData : dataCollection) {
            String templateClassName = dynamicDocData.getTemplateClassName();
            if (ArrayUtils.isNotEmpty(dynamicDocData.getTemplateVariables()) && templates.containsKey(templateClassName)) {
                dynamicDocData.setTemplate(templates.get(templateClassName));
                dataToWrite.add(dynamicDocData);
            } else {
                String msg = MessageFormat.format("Could not found template or template variables for dynamicDocData:[{0}]", dynamicDocData);
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
            }
        }
        dataToWrite.sort(DynamicDocData.COMPARATOR);
        return dataToWrite;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(DynamicConfigDocs.class.getCanonicalName());
    }

    private Set<DynamicDocData> processDynamicConfigDocsAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<DynamicDocData> headers = new HashSet<>();
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                DynamicConfigDocsVisitor dynamicConfigDocsVisitor = new DynamicConfigDocsVisitor(processingEnv);
                DynamicDocData docHeader = dynamicConfigDocsVisitor.visit(element);
                headers.add(docHeader);
            }
        }
        return headers;
    }

    private String readTemplate(String templateClass) {
        String templateFileName = DynamicConfigTemplate.TEMPLATE_DIR + templateClass + DynamicConfigTemplate.TEMPLATE_TYPE;
        InputStream resourceAsStream = templateClassLoader(templateClass).getResourceAsStream(templateFileName);
        if (resourceAsStream != null) {
            try {
                return IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                String msg = MessageFormat.format("Could not read template for class:[{0}], error: [{1}]", templateClass, e.getMessage());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, msg);
            }
        }
        return null;
    }

    private ClassLoader templateClassLoader(String templateClass) {
        if (StringUtils.isBlank(templateClass)) {
            return getClass().getClassLoader();
        }
        try {
            return ClassUtils.getClass(templateClass).getClassLoader();
        } catch (ClassNotFoundException e) {
            return getClass().getClassLoader();
        }
    }

    private void writeConfigDocFile(List<DynamicDocData> dataList, IDocWriter<DynamicDocData> docWriter, ConfigDocConfig config) {
        try (Writer writer = createWriter(config)) {
            docWriter.write(dataList, writer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    private Writer createWriter(ConfigDocConfig config) throws IOException {
        Path path = Paths.get(config.getOutputDir(), config.getDynamicOutputFileName());
        return config.isOutputToClassPath() ? processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", path.toString()).openWriter()
                : Files.newBufferedWriter(path);
    }
}
