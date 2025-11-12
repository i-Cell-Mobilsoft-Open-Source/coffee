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
package hu.icellmobilsoft.coffee.module.repserv.action.collect;

import java.text.MessageFormat;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementKindVisitor14;

import hu.icellmobilsoft.coffee.module.repserv.action.config.RepositoryServiceConfig;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.MethodData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ParamData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.RepositoryMethod;
import hu.icellmobilsoft.coffee.module.repserv.action.data.TypeParamData;
import hu.icellmobilsoft.coffee.module.repserv.action.util.IdGenerator;
import hu.icellmobilsoft.coffee.module.repserv.api.annotation.RepositoryService;

/**
 * Collector and visitor class for analyzing {@link RepositoryService}-annotated classes and extracting their structural and method-related metadata
 * into {@link ClassData} objects.
 * <p>
 * This visitor traverses repository service elements to gather information required for:
 * <ul>
 * <li>Generating implementation classes that extend the original {@link RepositoryService} classes.</li>
 * <li>Assigning unique method identifiers via {@link IdGenerator}.</li>
 * <li>Collecting parameter, type parameter, and exception data for each method.</li>
 * <li>Attaching JPQL query strings (if available) through {@link JpqlSetter}.</li>
 * <li>Producing JSON metadata files describing the collected repository data.</li>
 * </ul>
 * <p>
 * It uses {@link NestedPropertyCollector} to extract nested parameter details and {@link JpqlSetter} to associate JPQL queries with the corresponding
 * methods.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
@SuppressWarnings("java:S110")
public class RepositoryServiceVisitor extends ElementKindVisitor14<Void, ClassData> {

    private final NestedPropertyCollector nestedPropertyCollector;
    private final JpqlSetter jpqlSetter;
    private final RepositoryServiceConfig config;

    /**
     * Creates a visitor instance initialized with the processing environment and JPQL mapping data.
     *
     * @param config
     *            the config for generating repository service files
     * @param processingEnv
     *            the annotation processing environment
     * @param jpqlByRepositoryMethod
     *            a {@link Map} containing JPQL strings keyed by {@link RepositoryMethod} metadata
     */
    public RepositoryServiceVisitor(RepositoryServiceConfig config, ProcessingEnvironment processingEnv,
            Map<RepositoryMethod, String> jpqlByRepositoryMethod) {
        super();
        this.config = config;
        this.nestedPropertyCollector = new NestedPropertyCollector();
        this.jpqlSetter = new JpqlSetter(processingEnv, jpqlByRepositoryMethod);
    }

    /**
     * Visits a {@link TypeElement} representing a {@link RepositoryService}-annotated class and collects class-level metadata such as package,
     * ancestor, and implementation name.
     *
     * @param e
     *            the type element being visited
     * @param classData
     *            the {@link ClassData} object to store extracted metadata
     * @return {@code null}
     */
    @Override
    public Void visitType(TypeElement e, ClassData classData) {
        if (e.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
            throw new UnsupportedOperationException(
                    MessageFormat.format(
                            "Inner classes are not supported for id generation. [{0}] inner class exists in a @GenerateId annotated class.",
                            e.getQualifiedName()));
        }

        classData.setInheritorName(e.getSimpleName() + "Impl");
        classData.setClassName(String.valueOf(e.getQualifiedName()));
        classData.setPackageName(String.valueOf(e.getEnclosingElement().asType()));

        e.getEnclosedElements().forEach(ee -> visit(ee, classData));

        return super.visitType(e, classData);
    }

    /**
     * Visits a method element and collects its metadata (name, return type, thrown exceptions, etc.). Non-public or static methods are ignored.
     *
     * @param e
     *            the executable element representing the method
     * @param classData
     *            the {@link ClassData} object to store method metadata
     * @return {@code null}
     */
    @Override
    public Void visitExecutableAsMethod(ExecutableElement e, ClassData classData) {
        if (e.getModifiers().contains(Modifier.STATIC) || !e.getModifiers().contains(Modifier.PUBLIC)) {
            return super.visitExecutableAsMethod(e, classData);
        }

        MethodData methodData = new MethodData();
        methodData.setMethodName(String.valueOf(e.getSimpleName()));
        methodData.setReturnType(String.valueOf(e.getReturnType()));
        methodData.setThrownTypes(e.getThrownTypes().stream().map(String::valueOf).toList());

        classData.addMethodData(methodData);

        e.getTypeParameters().forEach(typeParameter -> visit(typeParameter, classData));
        e.getParameters().forEach(p -> visit(p, classData));

        jpqlSetter.setJpql(e, classData);

        methodData.setId(config.getProjectName() + IdGenerator.generateId(methodData));

        return super.visitExecutableAsMethod(e, classData);
    }

    /**
     * Visits a parameter element, collects its name, type, and nested property data.
     *
     * @param e
     *            the parameter element
     * @param classData
     *            the {@link ClassData} object to store parameter metadata
     * @return {@code null}
     */
    @Override
    public Void visitVariableAsParameter(VariableElement e, ClassData classData) {
        ParamData paramData = new ParamData();
        paramData.setParameterName(String.valueOf(e.getSimpleName()));
        paramData.setParameterType(String.valueOf(e.asType()));
        e.asType().accept(nestedPropertyCollector, paramData);

        classData.getLatestMethodData().addParam(paramData);

        return super.visitVariableAsParameter(e, classData);
    }

    /**
     * Visits a type parameter and collects its name and bounds for the current method.
     *
     * @param e
     *            the type parameter element
     * @param classData
     *            the {@link ClassData} object to store type parameter metadata
     * @return {@code null}
     */
    @Override
    public Void visitTypeParameter(TypeParameterElement e, ClassData classData) {
        TypeParamData typeParamData = new TypeParamData();
        typeParamData.setTypeParameterName(String.valueOf(e.getSimpleName()));
        typeParamData.setBounds(e.getBounds().stream().map(String::valueOf).toList());
        classData.getLatestMethodData().addTypeParam(typeParamData);

        return super.visitTypeParameter(e, classData);
    }

    /**
     * Visits a field element representing a repository reference and stores its name and type.
     *
     * @param e
     *            the variable element representing the repository field
     * @param classData
     *            the {@link ClassData} object to store repository reference metadata
     * @return {@code null}
     */
    @Override
    public Void visitVariableAsField(VariableElement e, ClassData classData) {
        classData.setRepositoryName(String.valueOf(e.getSimpleName()));
        classData.setRepositoryType(String.valueOf(e.asType()));
        return super.visitVariableAsField(e, classData);
    }

}
