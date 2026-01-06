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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.lang.model.element.TypeElement;
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

/**
 * Visitor for resolving declared and type variable types into concrete type names.
 * <p>
 * This visitor replaces generic type variables with their resolved values based on a provided type-parameter map and returns string representations
 * of the resolved types.
 * </p>
 * 
 * @author janos.boroczki
 * @since 3.3.0
 */
public class TypeParameterVisitor extends AbstractTypeVisitor14<String, Map<String, String>> {
    /**
     * Creates a new {@code TypeParameterVisitor} instance.
     */
    public TypeParameterVisitor() {
        super();
    }

    @Override
    public String visitDeclared(DeclaredType t, Map<String, String> typeParameterMap) {
        List<String> typeArguments = t.getTypeArguments()
                .stream()
                .map(String::valueOf)
                .map(ta -> Optional.ofNullable(typeParameterMap.get(ta)).orElse(ta))
                .toList();

        if (!typeArguments.isEmpty() && t.asElement() instanceof TypeElement e) {
            return e.getQualifiedName() + "<" + String.join(", ", typeArguments) + ">";
        }

        return defaultValue(t);
    }

    private String defaultValue(TypeMirror t) {
        return String.valueOf(t);
    }

    @Override
    public String visitTypeVariable(TypeVariable t, Map<String, String> typeParameterMap) {
        String defaultValue = defaultValue(t);
        return Optional.ofNullable(typeParameterMap.get(defaultValue)).orElse(defaultValue);
    }

    @Override
    public String visitPrimitive(PrimitiveType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitIntersection(IntersectionType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitNull(NullType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitArray(ArrayType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitError(ErrorType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitWildcard(WildcardType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitExecutable(ExecutableType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitNoType(NoType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }

    @Override
    public String visitUnion(UnionType t, Map<String, String> typeParameterMap) {
        return defaultValue(t);
    }
}
