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
package org.apache.deltaspike.data.impl.builder;

import java.lang.reflect.Method;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.cdi.trace.spi.ITraceHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

import org.apache.deltaspike.data.impl.builder.part.QueryRoot;
import org.apache.deltaspike.data.impl.handler.CdiQueryInvocationContext;
import org.apache.deltaspike.data.impl.param.Parameters;

@ApplicationScoped
public class MethodQueryBuilder extends QueryBuilder
{

    @Inject
    private ITraceHandler traceHandler;

    @Override
    public Object execute(CdiQueryInvocationContext context)
    {
        Query jpaQuery = createJpaQuery(context);
        Method method = context.getMethod();
        Traced traced = new Traced.Literal(SpanAttribute.Database.COMPONENT, SpanAttribute.Database.KIND, SpanAttribute.Database.DB_TYPE);
        String operation = context.getRepositoryClass() + "." + method.getName();
        return traceHandler.runWithTraceNoException(() -> context.executeQuery(jpaQuery), traced, operation);
    }

    private Query createJpaQuery(CdiQueryInvocationContext context)
    {
        Parameters params = context.getParams();
        QueryRoot root = context.getRepositoryMethodMetadata().getQueryRoot();
        String jpqlQuery = context.applyQueryStringPostProcessors(root.getJpqlQuery());
        context.setQueryString(jpqlQuery);
        params.updateValues(root.getParameterUpdates());
        Query result = params.applyTo(context.getEntityManager().createQuery(jpqlQuery));
        return context.applyRestrictions(result);
    }

}
