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
package hu.icellmobilsoft.coffee.module.docgen.common.processor;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import hu.icellmobilsoft.coffee.module.docgen.common.writer.IDocWriter;
import hu.icellmobilsoft.coffee.module.docgen.common.config.AbstractDocGenConfig;

/**
 * Abstract annotation processor for generating documentation
 *
 * @param <C>
 *            the type of configuration object
 * @param <D>
 *            the type of documentation data
 * @author janos.boroczki
 * @since 2.12.0
 */
public abstract class AbstractDocGenProcessor<C extends AbstractDocGenConfig, D> extends AbstractProcessor {

    /**
     * Creates the abstract doc gen processor
     */
    protected AbstractDocGenProcessor() {
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        C config;
        try {
            config = getConfig();
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            return false;
        }

        List<D> dataList = collectDocData(annotations, roundEnv);

        if (!dataList.isEmpty()) {
            dataList = sortData(dataList);

            writeToFile(dataList, getDocWriter(config), config);
        }

        return false;
    }

    /**
     * Returns the config object
     * 
     * @return the config object
     */
    protected abstract C getConfig();

    /**
     * Collects data for documentation
     * 
     * @param annotations
     *            the annotation interfaces requested to be processed
     * @param roundEnv
     *            environment for information about the current and prior round
     * @return the collected data
     */
    protected abstract List<D> collectDocData(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    /**
     * Sorts collected data
     * 
     * @param dataList
     *            collected data
     * @return sorted data
     */
    protected abstract List<D> sortData(List<D> dataList);

    /**
     * Returns {@link IDocWriter} implementation which writes collected data
     * 
     * @param config
     *            the config object
     * @return the {@link IDocWriter} implementation
     */
    protected abstract IDocWriter<D> getDocWriter(C config);

    /**
     * Writes dataList with writer
     * 
     * @param dataList
     *            collected data
     * @param docWriter
     *            writer which writes the doc
     * @param config
     *            config object
     */
    protected void writeToFile(List<D> dataList, IDocWriter<D> docWriter, C config) {
        try (Writer writer = createWriter(config)) {
            docWriter.write(dataList, writer);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }

    /**
     * Creates the writer which is used for write documentation to file
     * 
     * @param config
     *            config object
     * @return the writer is used for write documentation to file
     * @throws IOException
     *             If an I/O error occurs
     */
    protected Writer createWriter(AbstractDocGenConfig config) throws IOException {
        return config.isOutputToClassPath() //
                ? processingEnv.getFiler() //
                        .createResource(StandardLocation.CLASS_OUTPUT, "", config.getOutputDir() + config.getOutputFileName()) //
                        .openWriter() //
                : Files.newBufferedWriter(Paths.get(config.getOutputDir(), config.getOutputFileName()));
    }
}
