/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.data.impl.criteria.predicate;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.metamodel.SingularAttribute;

public class NotLike<E> extends SingleValueBuilder<E, String>
{

    private final boolean caseInsensitive;

    public NotLike(SingularAttribute<? super E, String> att, String value)
    {
        this(att, value, false);
    }

    public NotLike(SingularAttribute<? super E, String> att, String value, boolean caseInsensitive)
    {
        super(att, value);
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public List<Predicate> build(CriteriaBuilder builder, Path<E> path)
    {
        return Arrays.asList(builder.notLike(
                caseInsensitive ? builder.upper(path.<String>get(getAtt())) : path.get(getAtt()),
                caseInsensitive ? getValue().toUpperCase() : getValue()));
    }

}
