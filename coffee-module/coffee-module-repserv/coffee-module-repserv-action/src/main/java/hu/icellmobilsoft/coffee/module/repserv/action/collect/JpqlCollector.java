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
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementKindVisitor14;

import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import hu.icellmobilsoft.coffee.module.repserv.action.RepositoryServiceProcessor;
import hu.icellmobilsoft.coffee.module.repserv.action.data.RepositoryMethod;

/**
 * Utility visitor for collecting JPQL query definitions from repository methods.
 * <p>
 * This visitor scans {@link Repository}-annotated classes and extracts JPQL query strings defined in {@link Query} annotations. Each discovered query
 * is mapped to a corresponding {@link RepositoryMethod} object, which uniquely identifies the repository method by its declaring type, name, and
 * parameter types.
 * </p>
 *
 * <p>
 * The collected JPQL mappings are later used by {@link RepositoryServiceProcessor} during code generation to link repository methods with their
 * associated queries.
 * </p>
 *
 * <p>
 * This class extends {@link ElementKindVisitor14} to simplify element traversal.
 * </p>
 *
 *
 * @author janos.boroczki
 * @since 2.12.0
 */
@SuppressWarnings("java:S110")
public class JpqlCollector extends ElementKindVisitor14<List<RepositoryMethod>, TypeMirror> {

    /**
     * Collects JPQL strings from {@link Query} annotations into a {@link Map}, keyed by method identifier data represented as
     * {@link RepositoryMethod} objects.
     *
     * @param annotations
     *            the annotation interfaces to be processed
     * @param roundEnv
     *            the environment providing information about the current and prior processing rounds
     * @return a {@link Map} associating {@link RepositoryMethod} instances with their JPQL query strings
     */
    public static Map<RepositoryMethod, String> collectJpqlMap(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return annotations.stream()
                .filter(a -> a.getSimpleName().toString().equals(Repository.class.getSimpleName()))
                .map(roundEnv::getElementsAnnotatedWith)
                .flatMap(Set::stream)
                .map(JpqlCollector::getJpqlData)
                .flatMap(List::stream)
                .collect(Collectors.toMap(Function.identity(), RepositoryMethod::getJpql));
    }

    /**
     * Retrieves JPQL data for a given repository element.
     *
     * @param e
     *            the element representing a repository
     * @return a list of {@link RepositoryMethod} objects with extracted JPQL data
     */
    private static List<RepositoryMethod> getJpqlData(Element e) {
        JpqlCollector visitor = new JpqlCollector();
        return visitor.visit(e, null);
    }

    /**
     * Visits a type element (repository class) and collects JPQL definitions from all enclosed executable elements (methods).
     *
     * @param e
     *            the type element to visit
     * @param repositoryType
     *            the type mirror of the repository
     * @return a list of {@link RepositoryMethod} objects with JPQL data
     */
    @Override
    public List<RepositoryMethod> visitType(TypeElement e, TypeMirror repositoryType) {
        List<RepositoryMethod> repositoryMethods = new ArrayList<>(e.getEnclosedElements().size());
        e.getEnclosedElements().stream().map(ee -> visit(ee, e.asType())).forEach(repositoryMethods::addAll);
        return repositoryMethods;
    }

    /**
     * Visits a method and collects JPQL data if it is annotated with {@link Query}.
     *
     * @param e
     *            the executable element representing the method
     * @param typeMirror
     *            the type mirror of the enclosing repository
     * @return a list containing a single {@link RepositoryMethod} if JPQL is found, otherwise an empty list
     */
    @Override
    public List<RepositoryMethod> visitExecutableAsMethod(ExecutableElement e, TypeMirror typeMirror) {
        Optional<Query> query = Optional.ofNullable(e.getAnnotation(Query.class));

        if (query.isEmpty()) {
            return List.of();
        }

        RepositoryMethod repositoryMethod = new RepositoryMethod();
        repositoryMethod.setRepositoryType(String.valueOf(typeMirror));
        repositoryMethod.setMethodName(e.getSimpleName());
        List<String> parameterTypes = e.getParameters().stream().map(VariableElement::asType).map(TypeMirror::toString).toList();
        repositoryMethod.setParameterTypes(parameterTypes);
        repositoryMethod.setJpql(query.get().value());
        return List.of(repositoryMethod);
    }
}
