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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.Trees;

import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.ParamData;
import hu.icellmobilsoft.coffee.module.repserv.action.data.RepositoryMethod;

/**
 * A tree visitor that analyzes repository method invocations in generated source code and assigns the corresponding JPQL (Java Persistence Query
 * Language) statements to the given {@link ClassData} structure.
 *
 * <p>
 * This class uses the {@link com.sun.source.util.Trees} API to traverse Java syntax trees and resolve repository method calls at compile-time. It
 * maps discovered repository methods to predefined JPQL strings stored in {@code jpqlByRepositoryMethod}.
 * </p>
 *
 * <p>
 * Each detected method invocation updates the latest method metadata in {@link ClassData} with the associated JPQL query string.
 * </p>
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
public class JpqlSetter extends SimpleTreeVisitor<Void, ClassData> {

    /** Processing environment used to obtain compiler utilities and trees. */
    private final ProcessingEnvironment processingEnv;

    /** Mapping between repository methods and their corresponding JPQL queries. */
    private final Map<RepositoryMethod, String> jpqlByRepositoryMethod;

    /** The currently detected repository type. */
    private String repositoryType;

    /** The currently detected repository method name. */
    private Name repositoryMethodName;

    /** The parameter types of the currently processed repository method. */
    private List<String> repositoryMethodParamTypes = new ArrayList<>();

    /**
     * Creates a new {@code JpqlSetter} instance.
     *
     * @param processingEnv
     *            the annotation processing environment
     * @param jpqlByRepositoryMethod
     *            a map linking {@link RepositoryMethod} instances to JPQL queries
     */
    public JpqlSetter(ProcessingEnvironment processingEnv, Map<RepositoryMethod, String> jpqlByRepositoryMethod) {
        super();
        this.processingEnv = processingEnv;
        this.jpqlByRepositoryMethod = jpqlByRepositoryMethod;
    }

    /**
     * Traverses the AST (Abstract Syntax Tree) of the provided method and assigns the corresponding JPQL string to its metadata.
     *
     * @param element
     *            the executable element (method) to analyze
     * @param classData
     *            the {@link ClassData} object containing method metadata
     */
    public void setJpql(ExecutableElement element, ClassData classData) {
        Trees trees = Trees.instance(processingEnv);
        MethodTree methodTree = trees.getTree(element);
        methodTree.accept(this, classData);
    }

    @Override
    public Void visitMethod(MethodTree node, ClassData classData) {
        node.getBody().accept(this, classData);
        return super.visitMethod(node, classData);
    }

    @Override
    public Void visitBlock(BlockTree node, ClassData classData) {
        node.getStatements().forEach(s -> processStatement(s, classData));
        return super.visitBlock(node, classData);
    }

    /**
     * Processes a single statement node and assigns the resolved JPQL string to the latest method in {@link ClassData}.
     *
     * @param node
     *            the statement tree node
     * @param classData
     *            the class metadata
     */
    private void processStatement(StatementTree node, ClassData classData) {
        node.accept(this, classData);
        RepositoryMethod rm = new RepositoryMethod();
        rm.setRepositoryType(repositoryType);
        rm.setMethodName(repositoryMethodName);
        rm.setParameterTypes(repositoryMethodParamTypes);
        classData.getLatestMethodData().setJpql(jpqlByRepositoryMethod.get(rm));
        clear();
    }

    /** Resets temporary repository method tracking state. */
    private void clear() {
        repositoryType = null;
        repositoryMethodName = null;
        repositoryMethodParamTypes = new ArrayList<>();
    }

    @Override
    public Void visitReturn(ReturnTree node, ClassData classData) {
        node.getExpression().accept(this, classData);
        return super.visitReturn(node, classData);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, ClassData classData) {
        node.getMethodSelect().accept(this, classData);
        node.getArguments().forEach(a -> a.accept(this, classData));
        return super.visitMethodInvocation(node, classData);
    }

    /**
     * Handles member reference expressions (method references). Detects repository method references such as {@code Repository::method}.
     */
    @Override
    public Void visitMemberReference(MemberReferenceTree node, ClassData classData) {
        if (node.getQualifierExpression().toString().equals(classData.getRepositoryName())) {
            repositoryType = classData.getRepositoryType();
            repositoryMethodName = node.getName();
        }
        return super.visitMemberReference(node, classData);
    }

    /**
     * Handles member selections (e.g. {@code repo.method} or {@code param.field}). Detects repository calls or nested property access within
     * parameters.
     */
    @Override
    public Void visitMemberSelect(MemberSelectTree node, ClassData classData) {
        if (node.getExpression().toString().equals(classData.getRepositoryName())) {
            repositoryType = classData.getRepositoryType();
            repositoryMethodName = node.getIdentifier();
        } else {
            classData.getLatestMethodData()
                    .getParams()
                    .stream()
                    .filter(p -> node.getExpression().toString().equals(p.getParameterName()))
                    .map(ParamData::getNestedProps)
                    .map(np -> np.get(String.valueOf(node.getIdentifier())))
                    .findFirst()
                    .ifPresent(repositoryMethodParamTypes::add);
        }
        return super.visitMemberSelect(node, classData);
    }

    /**
     * Handles identifier nodes and matches them to known method parameters. Used to track parameter types during traversal.
     */
    @Override
    public Void visitIdentifier(IdentifierTree node, ClassData classData) {
        classData.getLatestMethodData()
                .getParams()
                .stream()
                .filter(p -> node.getName().toString().equals(p.getParameterName()))
                .map(ParamData::getParameterType)
                .findFirst()
                .ifPresent(repositoryMethodParamTypes::add);
        return super.visitIdentifier(node, classData);
    }
}
