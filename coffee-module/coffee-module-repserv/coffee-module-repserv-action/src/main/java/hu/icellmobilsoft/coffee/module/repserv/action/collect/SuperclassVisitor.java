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
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor14;

import hu.icellmobilsoft.coffee.module.repserv.action.data.ClassData;

/**
 * Visitor for traversing and collecting superclass metadata of repository service classes.
 * <p>
 * It walks through the inheritance chain and delegates visiting of discovered superclass elements back to {@link RepositoryServiceVisitor}. In
 * addition, it maps generic type parameters from the superclass to the current {@link ClassData} instance.
 * </p>
 *
 * @author janos.boroczki
 * @since 3.3.0
 */
@SuppressWarnings("java:S110")
public class SuperclassVisitor extends AbstractTypeVisitor14<Void, ClassData> {

    private final RepositoryServiceVisitor repositoryServiceVisitor;

    /**
     * Creates a new visitor that forwards discovered superclass elements to the main {@link RepositoryServiceVisitor}.
     *
     * @param repositoryServiceVisitor
     *            the main repository service visitor
     */
    public SuperclassVisitor(RepositoryServiceVisitor repositoryServiceVisitor) {
        this.repositoryServiceVisitor = repositoryServiceVisitor;
    }

    @Override
    public Void visitDeclared(DeclaredType t, ClassData classData) {
        if (Object.class.getName().equals(String.valueOf(t))) {
            return null;
        }

        collectTypeParameters(t, classData);
        repositoryServiceVisitor.visit(t.asElement(), classData);
        return null;
    }

    private static void collectTypeParameters(DeclaredType t, ClassData classData) {
        if (t.asElement() instanceof TypeElement e) {
            for (int i = 0; i < t.getTypeArguments().size(); i++) {
                String value = t.getTypeArguments().get(i).toString();
                value = Optional.ofNullable(classData.getTypeArgumentMap().get(value)).orElse(value);

                if (e.getTypeParameters().size() >= i + 1) {
                    String key = e.getTypeParameters().get(i).toString();
                    classData.getTypeArgumentMap().putIfAbsent(key, value);
                }
            }
        }
    }

    @Override
    public Void visitIntersection(IntersectionType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitPrimitive(PrimitiveType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitNull(NullType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitArray(ArrayType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitError(ErrorType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitTypeVariable(TypeVariable t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitWildcard(WildcardType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitExecutable(ExecutableType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitNoType(NoType t, ClassData classData) {
        return null;
    }

    @Override
    public Void visitUnion(UnionType t, ClassData classData) {
        return null;
    }
}
