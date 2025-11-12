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
import java.util.stream.Collectors;

import hu.icellmobilsoft.coffee.module.repserv.action.data.ParamData;

/**
 * Utility class for writing Java source statements using a provided {@link Writer}. Designed to simplify code generation by offering helper methods
 * for writing common Java constructs (e.g., classes, blocks, annotations).
 *
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>{@code
 * try (JavaFileWriter writer = new JavaFileWriter(new FileWriter("MyClass.java"))) {
 *     writer.writePackage("com.example");
 *     writer.writePublicClass("MyClass");
 *     writer.writeOpenBlock();
 *     writer.writePublicNoParamConstructor("MyClass");
 *     writer.writeCloseBlock();
 * }
 * }</pre>
 *
 * 
 * @author janos.boroczki
 * @since 2.12.0
 */
public class JavaFileWriter implements AutoCloseable {

    private final Writer writer;

    /**
     * Creates a new {@code JavaFileWriter} instance using the provided {@link Writer}.
     *
     * @param writer
     *            the {@link Writer} used to write statements
     */
    public JavaFileWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }

    /**
     * Writes a custom text statement as-is.
     *
     * @param text
     *            the statement text
     * @throws IOException
     *             if an I/O error occurs
     */
    public void write(String text) throws IOException {
        writer.write(text);
    }

    /**
     * Writes a {@code package} statement followed by a semicolon.
     *
     * @param packageName
     *            the package name
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writePackage(String packageName) throws IOException {
        write("package " + packageName);
        writeCloseStatement();
    }

    /**
     * Writes a {@code ;} to terminate a statement.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeCloseStatement() throws IOException {
        write(";\n");
    }

    /**
     * Writes a {@code public class} declaration.
     *
     * @param className
     *            the class name
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writePublicClass(String className) throws IOException {
        writePublic();
        write("class " + className);
    }

    /**
     * Writes an opening curly brace to begin a code block.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeOpenBlock() throws IOException {
        write("{\n");
    }

    /**
     * Writes a closing curly brace to end a code block.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeCloseBlock() throws IOException {
        write("}\n");
    }

    /**
     * Writes an {@code extends} clause for a class declaration.
     *
     * @param extendableClass
     *            the class being extended
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeExtendsClause(String extendableClass) throws IOException {
        write(getExtendsClause(extendableClass));
    }

    /**
     * Returns a formatted {@code extends} clause string.
     *
     * @param extendableClass
     *            the class being extended
     * @return the formatted {@code extends} clause
     */
    public static String getExtendsClause(String extendableClass) {
        return " extends " + extendableClass;
    }

    /**
     * Writes a {@code public} no-argument constructor for the specified class, including a call to {@code super()}.
     *
     * @param className
     *            the class name
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writePublicNoParamConstructor(String className) throws IOException {
        writePublic();
        write(className + "()");
        writeOpenBlock();
        write("super()");
        writeCloseStatement();
        writeCloseBlock();
    }

    /**
     * Writes an annotation declaration, prefixed with {@code @}.
     *
     * @param annotationName
     *            the annotation name
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeAnnotation(String annotationName) throws IOException {
        write("@" + annotationName + "\n");
    }

    /**
     * Writes the {@code public} keyword.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writePublic() throws IOException {
        write("public ");
    }

    /**
     * Writes an opening {@code try} clause.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeOpenTryClause() throws IOException {
        write("try {\n");
    }

    /**
     * Writes an opening {@code finally} clause.
     *
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeOpenFinallyClause() throws IOException {
        write("finally {\n");
    }

    /**
     * Writes a method call statement.
     *
     * @param methodName
     *            the method name
     * @param params
     *            list of parameters to pass to the method
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeCallMethod(String methodName, List<ParamData> params) throws IOException {
        write(methodName + "(");
        write(params.stream().map(ParamData::getParameterName).collect(Collectors.joining(",")) + ")");
        writeCloseStatement();
    }

    /**
     * Writes a {@code super.methodName(...)} call statement.
     *
     * @param methodName
     *            the superclass method name
     * @param params
     *            list of parameters to pass
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeCallSuperMethod(String methodName, List<ParamData> params) throws IOException {
        write("super.");
        writeCallMethod(methodName, params);
    }

    /**
     * Writes a {@code return super.methodName(...);} statement.
     *
     * @param methodName
     *            the superclass method name
     * @param params
     *            list of parameters to pass
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writeReturnCallSuperMethod(String methodName, List<ParamData> params) throws IOException {
        write("return ");
        writeCallSuperMethod(methodName, params);
    }

    /**
     * Writes a {@code private} field declaration.
     *
     * @param type
     *            the field type
     * @param name
     *            the field name
     * @throws IOException
     *             if an I/O error occurs
     */
    public void writePrivateField(String type, String name) throws IOException {
        write("private " + type + " " + name);
        writeCloseStatement();
    }
}
