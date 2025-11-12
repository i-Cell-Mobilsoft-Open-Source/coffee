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
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;

import jakarta.enterprise.inject.Model;
import jakarta.inject.Inject;

import org.apache.commons.codec.binary.StringUtils;

import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.MethodData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ParamData;
import hu.icellmobilsoft.coffee.module.repserv.api.SqlContext;
import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;

/**
 * Generates request-scoped inheritor classes annotated with {@link Model} for {@link RepositoryService}-annotated types. The generated classes extend
 * the original service and override its public methods to set a unique method identifier into a request-scoped {@link SqlContext}.
 * <p>
 * Each generated class is written as a full Java source file and includes dependency injection support for {@link SqlContext}. During method
 * execution, the generated code sets the ID before invoking the original logic and clears it afterward to ensure request-level isolation.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class JavaFileGenerator implements RepositoryServiceFileGenerator {

    private final JavaFileWriter writer;
    private final ClassData classData;

    /**
     * Creates a new Java file generator instance.
     *
     * @param processingEnv
     *            the annotation processing environment
     * @param classData
     *            the class metadata used for generating method implementations
     * @throws IOException
     *             if a source file cannot be created or written
     */
    public JavaFileGenerator(ProcessingEnvironment processingEnv, ClassData classData) throws IOException {
        this.writer = new JavaFileWriter(createWriter(processingEnv, classData));
        this.classData = classData;
    }

    private Writer createWriter(ProcessingEnvironment processingEnv, ClassData classData) throws IOException {
        return processingEnv.getFiler().createSourceFile(classData.getPackageName() + '.' + classData.getInheritorName()).openWriter();
    }

    /**
     * Generates the Java source file for the {@link RepositoryService}-derived class.
     *
     * @throws IOException
     *             if any write operation fails
     */
    @Override
    public void generate() throws IOException {
        writer.writePackage(classData.getPackageName());
        writer.writeAnnotation(Model.class.getName());
        writer.writePublicClass(classData.getInheritorName());
        writer.writeExtendsClause(classData.getClassName());
        writer.writeOpenBlock();
        writer.writeAnnotation(Inject.class.getName());
        writer.writePrivateField(SqlContext.class.getName(), "context");
        writer.writePublicNoParamConstructor(classData.getInheritorName());

        for (MethodData methodData : classData.getMethodDataList()) {
            writeMethod(methodData);
        }

        writer.writeCloseBlock();
    }

    /**
     * Writes a single overridden method that sets the request-scoped ID before invoking the superclass implementation and clears it afterward.
     *
     * @param methodData
     *            metadata describing the method to generate
     * @throws IOException
     *             if writing the method fails
     */
    private void writeMethod(MethodData methodData) throws IOException {
        writer.writeAnnotation(Override.class.getName());
        writer.writePublic();
        writer.write(methodData.toString());
        writer.writeOpenBlock();

        ParamData setIdParam = new ParamData();
        setIdParam.setParameterName("\"" + methodData.getId() + "\"");
        writer.writeCallMethod("context.setId", List.of(setIdParam));
        writer.writeOpenTryClause();
        if (!StringUtils.equals(methodData.getReturnType(), "void")) {
            writer.writeReturnCallSuperMethod(methodData.getMethodName(), methodData.getParams());
        } else {
            writer.writeCallSuperMethod(methodData.getMethodName(), methodData.getParams());
        }
        writer.writeCloseBlock();
        writer.writeOpenFinallyClause();
        writer.writeCallMethod("context.clear", List.of());
        writer.writeCloseBlock();
        writer.writeCloseBlock();
    }

    /**
     * Closes the underlying {@link JavaFileWriter} if it is open.
     *
     * @throws Exception
     *             if closing the writer fails
     */
    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }
}
