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
package org.apache.deltaspike.data.impl.builder.part;

import static org.apache.deltaspike.data.impl.util.QueryUtils.splitByKeyword;

import org.apache.deltaspike.data.impl.builder.QueryBuilderContext;
import org.apache.deltaspike.data.impl.meta.RepositoryMetadata;


class OrQueryPart extends ConnectingQueryPart
{

    public OrQueryPart(boolean first)
    {
        super(first);
    }

    @Override
    protected QueryPart build(String queryPart, String method, RepositoryMetadata repo)
    {
        String[] andParts = splitByKeyword(queryPart, "And");
        boolean first = true;
        for (String and : andParts)
        {
            AndQueryPart andPart = new AndQueryPart(first);
            first = false;
            children.add(andPart.build(and, method, repo));
        }
        return this;
    }

    @Override
    protected QueryPart buildQuery(QueryBuilderContext ctx)
    {
        if (!first)
        {
            ctx.append(" or ");
        }
        buildQueryForChildren(ctx);
        return this;
    }

}
