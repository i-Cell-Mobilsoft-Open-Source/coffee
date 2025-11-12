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
package hu.icellmobilsoft.coffee.module.repserv.action.write;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.StandardLocation;

import hu.icellmobilsoft.coffee.module.repserv.action.config.RepositoryServiceConfig;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;
import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;
import hu.icellmobilsoft.coffee.tool.jsonb.FieldOnlyVisibilityStrategy;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonbConfigBuilder;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonbUtil;

/**
 * Generates JSON catalog files for {@link RepositoryService}-annotated types. *
 * <p>
 * The generated JSON contains metadata about the class, its methods, parameters, * return types, thrown exceptions, and type parameters. Files are
 * written to the * {@code generated/repository-service/} folder using the class name and the {@code .json} suffix. *
 * </p>
 * *
 * <p>
 * Example of a generated JSON structure:
 * </p>
 *
 * <pre>{@code
  * {
  *   "className": "...",
  *   "inheritorName": "...",
  *   "methodDataList": [...],
  *   "packageName": "...",
  *   "repositoryName": "...",
  *   "repositoryType": "..."
  * }
  * }</pre>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class JsonFileGenerator implements RepositoryServiceFileGenerator {

    private static final String JSON_POSTFIX = ".json";
    private final Writer writer;
    private final ClassData classData;
    private final RepositoryServiceConfig config;

    /**
     * Creates a new {@code JsonFileGenerator} instance.
     *
     * @param config
     *            the config for generating repository service files
     * @param processingEnv
     *            the annotation processing environment
     * @param classData
     *            the metadata of the class used for JSON generation
     * @throws IOException
     *             if the output file cannot be created or written
     */
    public JsonFileGenerator(RepositoryServiceConfig config, ProcessingEnvironment processingEnv, ClassData classData) throws IOException {
        this.config = config;
        this.writer = createWriter(processingEnv, classData);
        this.classData = classData;
    }

    private Writer createWriter(ProcessingEnvironment processingEnv, ClassData classData) throws IOException {
        int i = classData.getClassName().lastIndexOf('.');
        String fileName = i == -1 ? classData.getClassName() : classData.getClassName().substring(i + 1);
        return processingEnv.getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", config.getGeneratedJsonFolder() + fileName + JSON_POSTFIX)
                .openWriter();
    }

    /**
     * Generates the JSON file for the current class.
     * <p>
     * This method uses {@link JsonbUtil} and {@link JsonbConfigBuilder} to serialize {@link ClassData} into JSON format and write it to the output
     * file.
     * </p>
     */
    @Override
    public void generate() {
        JsonbConfigBuilder jsonbConfigBuilder = new JsonbConfigBuilder().withPropertyVisibilityStrategy(new FieldOnlyVisibilityStrategy());
        JsonbUtil.getContext(jsonbConfigBuilder).toJson(classData, writer);
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }
}
