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

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor14;

import org.apache.commons.lang3.tuple.Pair;

import hu.icellmobilsoft.coffee.module.repserv.action.data.ParamData;

/**
 * A visitor class for recursively collecting nested property metadata from declared types within method parameters. It traverses the structure of
 * complex parameter types and maps their public getter methods to their corresponding return types.
 * <p>
 * This collector helps identify nested object structures inside method parameters, allowing them to be represented as a {@link Map} of property names
 * and {@link TypeMirror} types within {@link ParamData}.
 * </p>
 * <p>
 * The visitor ignores primitive, array, intersection, wildcard, and error types, since these do not contain nested properties.
 * </p>
 *
 * @see ParamData
 * @author janos.boroczki
 * @since 2.12.0
 */
@SuppressWarnings("java:S110")
public class NestedPropertyCollector extends AbstractTypeVisitor14<TypeMirror, ParamData> {

    /**
     * Creates a new {@code NestedPropertyCollector} instance.
     */
    public NestedPropertyCollector() {
        super();
    }

    /**
     * Visits declared types (classes or interfaces) and collects all their public method return types as nested properties into the given
     * {@link ParamData}.
     *
     * @param t
     *            the declared type being visited
     * @param paramData
     *            the {@link ParamData} object where collected properties are stored
     * @return always {@code null} since nested properties are collected directly into {@code paramData}
     */
    @Override
    public TypeMirror visitDeclared(DeclaredType t, ParamData paramData) {
        Map<String, String> nestedProps = t.asElement()
                .getEnclosedElements()
                .stream()
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> Pair.of(e.getSimpleName(), e.asType()))
                .map(pair -> Pair.of(String.valueOf(pair.getKey()), String.valueOf(pair.getValue().accept(this, paramData))))
                .filter(pair -> Objects.nonNull(pair.getValue()))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (tm1, tm2) -> tm1));
        paramData.setNestedProps(nestedProps);
        return null;
    }

    /**
     * Visits executable types (methods) and returns their return type.
     *
     * @param t
     *            the executable type (method)
     * @param paramData
     *            the {@link ParamData} associated with the parameter
     * @return the method's return {@link TypeMirror}
     */
    @Override
    public TypeMirror visitExecutable(ExecutableType t, ParamData paramData) {
        return t.getReturnType();
    }

    @Override
    public TypeMirror visitIntersection(IntersectionType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitPrimitive(PrimitiveType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitNull(NullType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitArray(ArrayType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitError(ErrorType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitTypeVariable(TypeVariable t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitWildcard(WildcardType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitNoType(NoType t, ParamData paramData) {
        return null;
    }

    @Override
    public TypeMirror visitUnion(UnionType t, ParamData paramData) {
        return null;
    }
}
